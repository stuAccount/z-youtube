package com.zyoutube.feature.video.service;

import com.zyoutube.feature.video.model.vo.VideoViewCountResponse;

public interface ViewCountService {
    VideoViewCountResponse recordView(Long videoId);
}
