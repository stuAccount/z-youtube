package com.zyoutube.feature.video.dao.redis;

public interface ViewDedupDao {
    boolean tryMarkViewedToday(Long videoId, String viewerKey);

    void deleteTodayMark(Long videoId, String viewerKey);
}
