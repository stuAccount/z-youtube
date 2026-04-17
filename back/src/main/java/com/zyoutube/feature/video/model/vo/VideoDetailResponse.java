package com.zyoutube.feature.video.model.vo;

import com.zyoutube.feature.account.model.vo.AccountSummaryResponse;
import com.zyoutube.feature.video.model.type.VideoStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VideoDetailResponse {
    private Long id;
    private String title;
    private String description;
    private VideoStatus status;
    private AccountSummaryResponse author;
    private LocalDateTime createdAt;
}
