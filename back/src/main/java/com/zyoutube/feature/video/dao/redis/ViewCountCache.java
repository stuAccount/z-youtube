package com.zyoutube.feature.video.dao.redis;

import java.util.Map;

public interface ViewCountCache {
    Long get(Long videoId);
    void set(Long videoId, long value);
    boolean setIfAbsent(Long videoId, long value);
    Long increment(Long videoId);
    Map<Long, Long> findAll();
    void delete(Long videoId);
}
