package com.zyoutube.feature.video.model.vo;

import com.zyoutube.feature.account.model.vo.AccountSummaryResponse;
import com.zyoutube.feature.engagement.model.type.ReactionType;
import com.zyoutube.feature.video.model.type.VideoStatus;
import com.zyoutube.feature.video.model.type.VideoVisibility;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VideoDetailResponse {
    private Long id;
    private String title;
    private String description;
    private String videoUrl;
    private String coverUrl;
    private VideoStatus status;
    private VideoVisibility visibility;
    private AccountSummaryResponse author;
    private LocalDateTime createdAt;
    private long viewCount;
    private long likeCount;
    private long dislikeCount;
    private long favoriteCount;
    private ReactionType myReaction;
    private boolean favorited;
}
