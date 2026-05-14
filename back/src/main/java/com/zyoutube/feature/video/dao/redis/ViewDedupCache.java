package com.zyoutube.feature.video.dao.redis;

public interface ViewDedupCache {
    boolean tryMarkViewedToday(Long videoId, String viewerKey);

    void deleteTodayMark(Long videoId, String viewerKey);
}
