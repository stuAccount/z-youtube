package com.zyoutube.feature.video.dao.redis.Impl;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.zyoutube.feature.video.dao.redis.ViewDeltaDao;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class ViewCountDeltaCacheImpl implements ViewDeltaDao {
    private static final String KEY_PREFIX = "video:view:delta:";    

    private final StringRedisTemplate stringRedisTemplate;

    private String buildKey(Long videoId) {
        return KEY_PREFIX + videoId;
    }

    @Override
    public long get(Long videoId) {
        String key = buildKey(videoId);
        String value = stringRedisTemplate.opsForValue().get(key);
        return value != null ? Long.parseLong(value) : 0;
    }

    @Override
    public void set(Long videoId, long delta) {
        String key = buildKey(videoId);
        stringRedisTemplate.opsForValue().set(key, String.valueOf(delta));
    }

    @Override
    public void increment(Long videoId) {
        String key = buildKey(videoId);
        stringRedisTemplate.opsForValue().increment(key, 1);
    }

    @Override
    public void delete(Long videoId) {
        String key = buildKey(videoId);
        stringRedisTemplate.delete(key);
    }

}
