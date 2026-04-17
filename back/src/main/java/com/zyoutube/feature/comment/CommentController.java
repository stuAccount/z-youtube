package com.zyoutube.feature.comment;

import com.zyoutube.common.api.ApiResponse;
import com.zyoutube.feature.comment.model.dto.CreateCommentRequest;
import com.zyoutube.feature.comment.model.vo.CommentDetailResponse;
import com.zyoutube.feature.comment.model.vo.CommentSummaryResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ApiResponse<CommentDetailResponse> createComment(@Valid @RequestBody CreateCommentRequest req) {
        return ApiResponse.success(commentService.createComment(req));
    }

    @GetMapping
    public ApiResponse<Page<CommentSummaryResponse>> getComments(@RequestParam Long videoId,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(commentService.getComments(videoId, page, size));
    }

    @DeleteMapping("{id}")
    public ApiResponse<Void> deleteComment(@PathVariable Long id,
                                           @RequestParam Long requesterAccountId) {
        commentService.deleteComment(id, requesterAccountId);
        return ApiResponse.success(null);
    }
}
