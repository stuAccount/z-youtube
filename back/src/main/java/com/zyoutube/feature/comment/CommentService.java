package com.zyoutube.feature.comment;

import com.zyoutube.common.exception.NotFoundException;
import com.zyoutube.feature.account.AccountFinder;
import com.zyoutube.feature.account.model.entity.Account;
import com.zyoutube.feature.account.model.vo.AccountSummaryResponse;
import com.zyoutube.feature.auth.context.CurrentUserProvider;
import com.zyoutube.feature.comment.model.dto.CreateCommentRequest;
import com.zyoutube.feature.comment.model.entity.Comment;
import com.zyoutube.feature.comment.model.vo.CommentDetailResponse;
import com.zyoutube.feature.comment.model.vo.CommentSummaryResponse;
import com.zyoutube.feature.video.VideoAccessPolicy;
import com.zyoutube.feature.video.VideoFinder;
import com.zyoutube.feature.video.model.entity.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final AccountFinder accountFinder;
    private final VideoFinder videoFinder;
    private final CurrentUserProvider currentUserProvider;

    public CommentService(CommentRepository commentRepository,
                          AccountFinder accountFinder,
                          VideoFinder videoFinder,
                          CurrentUserProvider currentUserProvider) {
        this.commentRepository = commentRepository;
        this.accountFinder = accountFinder;
        this.videoFinder = videoFinder;
        this.currentUserProvider = currentUserProvider;
    }

    private Comment getOwnedComment(Long commentId, Long accountId) {
        return commentRepository.findByIdAndAuthor_Id(commentId, accountId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));
    }

    private AccountSummaryResponse createAccountSummary(Account author) {
        return new AccountSummaryResponse(
                author.getId(),
                author.getUsername(),
                author.getNickname(),
                author.getAvatarUrl()
        );
    }

    @Transactional
    public CommentDetailResponse createComment(CreateCommentRequest req) {
        Account author = accountFinder.getCurrentAccount();
        Long currentAccountId = author.getId();

        Video video = videoFinder.findVideo(req.getVideoId());

        if (author.isDeleted()) {
            throw new IllegalStateException("Withdrawn account cannot comment");
        }

        if (!VideoAccessPolicy.canViewDetail(video, currentAccountId)) {
            throw new NotFoundException("Video not found");
        }
        if (!VideoAccessPolicy.canCreateComment(video, currentAccountId)) {
            throw new IllegalStateException("Commenting is not allowed for this video");
        }

        Comment comment = new Comment();
        comment.assignVideo(video);
        comment.assignAuthor(author);
        comment.changeContent(req.getContent());
        commentRepository.save(comment);

        return new CommentDetailResponse(
                comment.getId(),
                comment.getVideo().getId(),
                comment.getContent(),
                createAccountSummary(author),
                comment.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public Page<CommentSummaryResponse> getComments(Long videoId, int page, int size) {
        Pageable pageable = PageRequest.of(
                Math.max(0, page),
                Math.max(1, size),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Video video = videoFinder.findVideo(videoId);
        Long viewerId = currentUserProvider.getCurrentAccountIdOrNull();
        if (!VideoAccessPolicy.canReadComments(video, viewerId)) {
            throw new NotFoundException("Video not found");
        }

        Page<Comment> commentPage = commentRepository.findAllByVideo_Id(videoId, pageable);

        return commentPage.map(comment -> new CommentSummaryResponse(
                comment.getId(),
                comment.getContent(),
                createAccountSummary(comment.getAuthor()),
                comment.getCreatedAt()
        ));
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Account account = accountFinder.getCurrentAccount();
        Long currentAccountId = account.getId();

        if (account.isDeleted()) {
            throw new IllegalStateException("Withdrawn account can not delete comments");
        }

        Comment comment = getOwnedComment(commentId, currentAccountId);
        commentRepository.delete(comment);
    }
}
