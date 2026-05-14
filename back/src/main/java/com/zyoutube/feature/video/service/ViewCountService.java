package com.zyoutube.feature.video.service;

import com.zyoutube.feature.video.model.vo.VideoViewCountResponse;

public interface ViewCountService {
    // TODO: 校验这次播放是否有效 防刷 去重 判断是否满足计数条件 播放量加一 记录行为日志 发消息或更新缓存
    VideoViewCountResponse recordView(Long videoId);

    long getViewCount(Long videoId);
}
