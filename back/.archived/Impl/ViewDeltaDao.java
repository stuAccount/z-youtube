package com.zyoutube.feature.video.dao.redis;

public interface ViewDeltaDao {

    long get(Long videoId);
    void set(Long videoId, long delta);
    void increment(Long videoId);
    void delete(Long videoId);
    
}
