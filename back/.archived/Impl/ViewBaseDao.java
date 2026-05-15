package com.zyoutube.feature.video.dao.redis;

import java.util.Map;

public interface ViewBaseDao {
    Long get(Long videoId);
    void set(Long videoId, long value);
    boolean setIfAbsent(Long videoId, long value);
    Long increment(Long videoId);
    Long increment(Long videoId, long delta);
    Map<Long, Long> findAll();
    void delete(Long videoId);
}
