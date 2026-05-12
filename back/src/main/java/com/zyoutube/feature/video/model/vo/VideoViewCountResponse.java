package com.zyoutube.feature.video.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VideoViewCountResponse {
    private Long videoId;
    private long viewCount;
}
