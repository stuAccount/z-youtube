package com.zyoutube.feature.engagement.model.vo;

import com.zyoutube.feature.engagement.model.type.ReactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VideoEngagementResponse {
    private Long videoId;
    private long likeCount;
    private long dislikeCount;
    private long favoriteCount;
    private ReactionType myReaction;
    private boolean favorited;
}
