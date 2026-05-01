package com.zyoutube.feature.engagement.model.vo;

import com.zyoutube.feature.account.model.vo.AccountSummaryResponse;
import com.zyoutube.feature.video.model.type.VideoStatus;
import com.zyoutube.feature.video.model.type.VideoVisibility;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FavoriteVideoSummaryResponse {
    private Long id;
    private String title;
    private VideoStatus status;
    private VideoVisibility visibility;
    private AccountSummaryResponse author;
    private LocalDateTime createdAt;
    private LocalDateTime favoritedAt;
}
