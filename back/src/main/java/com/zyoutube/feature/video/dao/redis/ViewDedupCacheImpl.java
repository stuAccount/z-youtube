package com.zyoutube.feature.video.dao.redis;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class ViewDedupCacheImpl implements ViewDedupCache {
    private static final String KEY_PREFIX = "video:view:dedup:";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;

    private final StringRedisTemplate stringRedisTemplate;

    // video:view:dedup:{videoId}:{viewerKey}:{yyyyMMdd}
    private String buildTodayKey(Long videoId, String viewerKey) {
        String date = LocalDate.now().format(DATE_FORMATTER);
        return KEY_PREFIX + videoId + ":" + viewerKey + ":" + date;
    }

    // 计算从现在到明天零点的秒数，作为 Redis key 的过期时间
    private Duration ttlUntilTomorrow() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrowStart = LocalDate.now().plusDays(1).atStartOfDay();
        return Duration.between(now, tomorrowStart);
    }

    // 尝试标记今天已经看过了，如果已经标记过了则返回 false，否则标记并返回 true
    @Override
    public boolean tryMarkViewedToday(Long videoId, String viewerKey) {
        Boolean success = stringRedisTemplate.opsForValue()
                .setIfAbsent(buildTodayKey(videoId, viewerKey), "1", ttlUntilTomorrow());
        return Boolean.TRUE.equals(success);
    }

    // 删除今天的观看标记，通常在视频被删除或者用户被封禁时调用?
    @Override
    public void deleteTodayMark(Long videoId, String viewerKey) {
        stringRedisTemplate.delete(buildTodayKey(videoId, viewerKey));
    }
}
