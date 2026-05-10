package com.zyoutube.feature.video;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VideoViewCountResponse {
    private Long videoId;
    private long viewCount;
}
