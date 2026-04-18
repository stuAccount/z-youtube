package com.zyoutube.feature.comment;

import com.zyoutube.feature.account.AccountRepository;
import com.zyoutube.feature.account.model.entity.Account;
import com.zyoutube.feature.account.model.vo.AccountSummaryResponse;
import com.zyoutube.feature.comment.model.dto.CreateCommentRequest;
import com.zyoutube.feature.comment.model.vo.CommentDetailResponse;
import com.zyoutube.feature.comment.model.vo.CommentSummaryResponse;
import com.zyoutube.feature.video.VideoRepository;
import com.zyoutube.feature.video.model.entity.Video;
import com.zyoutube.feature.video.model.type.VideoStatus;
import com.zyoutube.feature.comment.model.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final AccountRepository accountRepository;
    private final VideoRepository videoRepository;

    public CommentService(CommentRepository commentRepository,
                          AccountRepository accountRepository,
                          VideoRepository videoRepository) {
        this.commentRepository = commentRepository;
        this.accountRepository = accountRepository;
        this.videoRepository = videoRepository;
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
        Account author = accountRepository.findById(req.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        Video video = videoRepository.findById(req.getVideoId())
                .orElseThrow(() -> new IllegalArgumentException("Video not found"));

        if (author.isDeleted()) {
            throw new IllegalStateException("Withdrawn account cannot comment");
        }

        if (video.getStatus() != VideoStatus.PUBLISHED) {
            throw new IllegalArgumentException("Video is private");
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

        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("Video not found"));

        Page<Comment> commentPage = commentRepository.findAllByVideo_Id(videoId, pageable);

        return commentPage.map(comment -> new CommentSummaryResponse(
                comment.getId(),
                comment.getContent(),
                createAccountSummary(comment.getAuthor()),
                comment.getCreatedAt()
        ));
    }

    @Transactional
    public void deleteComment(Long id, Long requesterAccountId) {
        Account account = accountRepository.findById(requesterAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        if (account.isDeleted()) {
            throw new IllegalStateException("Withdrawn account can not delete comments");
        }
        Comment comment = commentRepository.findByIdAndAuthor_Id(id, requesterAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        commentRepository.delete(comment);
    }
}
