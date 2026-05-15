package com.zyoutube.feature.video.dao.redis.Impl;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import com.zyoutube.feature.video.dao.redis.ViewBaseDao;
import org.springframework.data.redis.core.Cursor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class ViewCountBaseCacheImpl implements ViewBaseDao {
    private static final String KEY_PREFIX = "video:view:count:";

    private final StringRedisTemplate stringRedisTemplate;

    private String buildKey(Long videoId) {
        return KEY_PREFIX + videoId;
    }

    @Override
    public Long get(Long videoId) {
        String value = stringRedisTemplate.opsForValue().get(buildKey(videoId));
        return (value == null) ? null : Long.parseLong(value);
    }

    @Override
    public Long increment(Long videoId) {
        return stringRedisTemplate.opsForValue().increment(buildKey(videoId));
    }

    @Override
    public Long increment(Long videoId, long delta) {
        return stringRedisTemplate.opsForValue().increment(buildKey(videoId), delta);
    }

    @Override
    public Long increment(Long videoId, long delta) {
        return stringRedisTemplate.opsForValue().increment(buildKey(videoId), delta);
    }

    @Override
    public Map<Long, Long> findAll() {
        return stringRedisTemplate.execute((RedisConnection connection) -> {
            Map<Long, Long> counts = new HashMap<>();
            ScanOptions options = ScanOptions.scanOptions()
                    .match(KEY_PREFIX + "*")
                    .count(100)
                    .build();

            try (Cursor<byte[]> cursor = connection.scan(options)) {
                while (cursor.hasNext()) {
                    String key = new String(cursor.next(), StandardCharsets.UTF_8);
                    String value = stringRedisTemplate.opsForValue().get(key);
                    if (value == null) {
                        continue;
                    }
                    Long videoId = parseVideoId(key);
                    if (videoId != null) {
                        counts.put(videoId, Long.parseLong(value));
                    }
                }
            } catch (Exception e) {
                throw new IllegalStateException("Failed to scan view count cache", e);
            }

            return counts;
        });
    }

    @Override
    public void set(Long videoId, long value) {
        stringRedisTemplate.opsForValue().set(buildKey(videoId), String.valueOf(value));
    }

    @Override
    public boolean setIfAbsent(Long videoId, long value) {
        return stringRedisTemplate.opsForValue().setIfAbsent(buildKey(videoId), String.valueOf(value));
    }

    @Override
    public void delete(Long videoId) {
        stringRedisTemplate.delete(buildKey(videoId));
    }

    private Long parseVideoId(String key) {
        if (!key.startsWith(KEY_PREFIX)) {
            return null;
        }
        return Long.parseLong(key.substring(KEY_PREFIX.length()));
    }
}
