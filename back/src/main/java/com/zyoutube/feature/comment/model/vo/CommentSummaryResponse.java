package com.zyoutube.feature.comment.model.vo;

import com.zyoutube.feature.account.model.vo.AccountSummaryResponse;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentSummaryResponse {
    private Long id;
    private String content;
    private AccountSummaryResponse author;
    private LocalDateTime createdAt;
}
