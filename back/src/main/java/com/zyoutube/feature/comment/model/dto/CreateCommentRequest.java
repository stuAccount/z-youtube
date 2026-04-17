package com.zyoutube.feature.comment.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCommentRequest {
    @NotNull(message = "Video id is required")
    private Long videoId;

    @NotNull(message = "Author id is required before Sprint 7 login is added")
    private Long authorId;

    @NotBlank(message = "Comment content can not be blank")
    @Size(max = 1000, message = "Comment content must be at most 1000 characters")
    private String content;
}
