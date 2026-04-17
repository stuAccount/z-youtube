package com.zyoutube.feature.comment;

import com.zyoutube.feature.account.AccountRepository;
import com.zyoutube.feature.account.model.entity.Account;
import com.zyoutube.feature.account.model.vo.AccountSummaryResponse;
import com.zyoutube.feature.comment.model.dto.CreateCommentRequest;
import com.zyoutube.feature.comment.model.vo.CommentDetailResponse;
import com.zyoutube.feature.comment.model.vo.CommentSummaryResponse;
import com.zyoutube.feature.video.VideoRepository;
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
        /*
         * Sprint 6 hand-typing target:
         * 1. find the author by req.getAuthorId()
         * 2. find the target video by req.getVideoId()
         * 3. block withdrawn accounts from commenting
         * 4. decide whether non-published videos can receive comments in Sprint 6
         * 5. create/save Comment and return CommentDetailResponse
         */
        throw new UnsupportedOperationException("TODO Sprint 6: create comment with account/video association");
    }

    @Transactional(readOnly = true)
    public Page<CommentSummaryResponse> getComments(Long videoId, int page, int size) {
        Pageable pageable = PageRequest.of(
                Math.max(0, page),
                Math.max(1, size),
                Sort.by(Sort.Direction.ASC, "createdAt")
        );

        /*
         * Sprint 6 hand-typing target:
         * - maybe verify the video exists first
         * - query with commentRepository.findAllByVideo_Id(videoId, pageable)
         * - map Page<Comment> -> Page<CommentSummaryResponse>
         * - think about whether comment order should be ASC or DESC
         */
        throw new UnsupportedOperationException("TODO Sprint 6: implement comment list pagination by video");
    }

    @Transactional
    public void deleteComment(Long id, Long requesterAccountId) {
        /*
         * Sprint 6 hand-typing target:
         * 1. find the requester account
         * 2. decide how withdrawn accounts should behave here
         * 3. find the comment owned by requesterAccountId
         * 4. hard delete for now; Sprint 7 can replace requesterAccountId with login user
         */
        throw new UnsupportedOperationException("TODO Sprint 6: delete comment with ownership check");
    }
}
