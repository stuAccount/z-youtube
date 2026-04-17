package com.zyoutube.feature.video.model.vo;

import com.zyoutube.feature.account.model.vo.AccountSummaryResponse;
import com.zyoutube.feature.video.model.type.VideoStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VideoSummaryResponse {
    private Long id;
    private String title;
    private VideoStatus status;
    private AccountSummaryResponse author;
    private LocalDateTime createdAt;
}
