package com.zyoutube.feature.video.dao.redis;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.function.LongConsumer;
import lombok.AllArgsConstructor;

import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

@AllArgsConstructor
@Repository
public class VideoViewRedisDaoImpl implements VideoViewRedisDao {
    private static final String BASE_KEY_PREFIX = "video:view:base:";
    private static final String DELTA_KEY_PREFIX = "video:view:delta:";
    private static final String DIRTY_KEY = "video:view:dirty:";
    private static final String DEDUP_KEY_PREFIX = "video:view:dedup:";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;
    private static final DefaultRedisScript<Long> CLAIM_DELTA_SCRIPT = buildClaimDeltaScript();
    private static final DefaultRedisScript<Long> COMMIT_FLUSH_SCRIPT = buildCommitFlushScript();
    private static final DefaultRedisScript<Long> ROLLBACK_FLUSH_SCRIPT = buildRollbackFlushScript();

    private final StringRedisTemplate stringRedisTemplate;

    private String baseKey(Long videoId) {
        return BASE_KEY_PREFIX + videoId;
    }

    private String deltaKey(Long videoId) {
        return DELTA_KEY_PREFIX + videoId;
    }

    private String dedupKey(Long videoId, String viewerKey) {
        String date = LocalDate.now().format(DATE_FORMATTER);
        return DEDUP_KEY_PREFIX + videoId + ":" + viewerKey + ":" + date;
    }

    private Duration ttlUntilTomorrow() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrowStart = LocalDate.now().plusDays(1).atStartOfDay();
        return Duration.between(now, tomorrowStart);
    }

    private static DefaultRedisScript<Long> buildClaimDeltaScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setResultType(Long.class);
        script.setScriptText(
                "local d = tonumber(redis.call('GET', KEYS[1]) or '0')\n" +
                "if d <= 0 then\n" +
                "  redis.call('SREM', KEYS[2], ARGV[1])\n" +
                "  return 0\n" +
                "end\n" +
                "redis.call('SET', KEYS[1], 0)\n" +
                "return d"
        );
        return script;
    }

    private static DefaultRedisScript<Long> buildCommitFlushScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setResultType(Long.class);
        script.setScriptText(
                "redis.call('INCRBY', KEYS[1], tonumber(ARGV[1]))\n" +
                "local cur = tonumber(redis.call('GET', KEYS[2]) or '0')\n" +
                "if cur == 0 then\n" +
                "  redis.call('SREM', KEYS[3], ARGV[2])\n" +
                "end\n" +
                "return cur"
        );
        return script;
    }

    private static DefaultRedisScript<Long> buildRollbackFlushScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setResultType(Long.class);
        script.setScriptText(
                "redis.call('INCRBY', KEYS[1], tonumber(ARGV[1]))\n" +
                "redis.call('SADD', KEYS[2], ARGV[2])\n" +
                "return 1"
        );
        return script;
    }

    @Override
    public Long getBase(Long videoId) {
        String value = stringRedisTemplate.opsForValue().get(baseKey(videoId));
        return value == null ? null : Long.parseLong(value);
    }

    @Override
    public void setBase(Long videoId, long value) {
        stringRedisTemplate.opsForValue().set(baseKey(videoId), String.valueOf(value));
    }

    @Override
    public boolean setBaseIfAbsent(Long videoId, long value) {
        return Boolean.TRUE.equals(
                stringRedisTemplate.opsForValue().setIfAbsent(baseKey(videoId), String.valueOf(value))
        );
    }

    @Override
    public Long incrementBase(Long videoId) {
        return stringRedisTemplate.opsForValue().increment(baseKey(videoId));
    }

    @Override
    public Long incrementBase(Long videoId, long delta) {
        return stringRedisTemplate.opsForValue().increment(baseKey(videoId), delta);
    }

    @Override
    public void deleteBase(Long videoId) {
        stringRedisTemplate.delete(baseKey(videoId));
    }

    @Override
    public long getDelta(Long videoId) {
        String value = stringRedisTemplate.opsForValue().get(deltaKey(videoId));
        return value == null ? 0 : Long.parseLong(value);
    }

    @Override
    public void setDelta(Long videoId, long delta) {
        stringRedisTemplate.opsForValue().set(deltaKey(videoId), String.valueOf(delta));
    }

    @Override
    public void incrementDelta(Long videoId) {
        stringRedisTemplate.opsForValue().increment(deltaKey(videoId), 1);
    }

    @Override
    public void deleteDelta(Long videoId) {
        stringRedisTemplate.delete(deltaKey(videoId));
    }

    @Override
    public void markDirty(Long videoId) {
        stringRedisTemplate.opsForSet().add(DIRTY_KEY, videoId.toString());
    }

    @Override
    public Set<Long> getDirtyVideoIds() {
        Set<String> members = stringRedisTemplate.opsForSet().members(DIRTY_KEY);
        if (members == null || members.isEmpty()) {
            return Collections.emptySet();
        }
        Set<Long> result = new HashSet<>(members.size());
        for (String member : members) {
            try {
                result.add(Long.parseLong(member));
            } catch (NumberFormatException ignored) {
                // ignore malformed member
            }
        }
        return result;
    }

    @Override
    public boolean isDirty(Long videoId) {
        return Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember(DIRTY_KEY, videoId.toString()));
    }

    @Override
    public void clearDirty(Long videoId) {
        stringRedisTemplate.opsForSet().remove(DIRTY_KEY, videoId.toString());
    }

    @Override
    public boolean tryMarkViewedToday(Long videoId, String viewerKey) {
        Boolean success = stringRedisTemplate.opsForValue()
                .setIfAbsent(dedupKey(videoId, viewerKey), "1", ttlUntilTomorrow());
        return Boolean.TRUE.equals(success);
    }

    @Override
    public void deleteTodayMark(Long videoId, String viewerKey) {
        stringRedisTemplate.delete(dedupKey(videoId, viewerKey));
    }


    // 增量和打脏标记两redis事务实现
    @Override
    public List<Object> incrementDeltaAndMarkDirtyTx(Long videoId) {
        return stringRedisTemplate.execute(new SessionCallback<List<Object>>() {
            @Override
            public List<Object> execute(org.springframework.data.redis.core.RedisOperations operations) {
                operations.multi();
                incrementDelta(videoId);
                markDirty(videoId);
                List<Object> results = operations.exec();
                if (results == null) {
                    // CAS retry
                    incrementDeltaAndMarkDirtyTx(videoId);
                }
                return results;
            }
        });
    }

    // lua脚本实现播放增量脏数据刷盘操作
    @Override
    public void flushOne(Long videoId, LongConsumer persister) {
        long delta = claimDelta(videoId);
        if (delta <= 0) {
            return;
        }
        try {
            persister.accept(delta);
            commitFlush(videoId, delta);
        } catch (RuntimeException ex) {
            rollbackFlush(videoId, delta);
            throw ex;
        }
    }

    private long claimDelta(Long videoId) {
        Long result = stringRedisTemplate.execute(
                CLAIM_DELTA_SCRIPT,
                List.of(deltaKey(videoId), DIRTY_KEY),
                videoId.toString()
        );
        return result == null ? 0L : result;
    }

    private void commitFlush(Long videoId, long delta) {
        stringRedisTemplate.execute(
                COMMIT_FLUSH_SCRIPT,
                List.of(baseKey(videoId), deltaKey(videoId), DIRTY_KEY),
                String.valueOf(delta),
                videoId.toString()
        );
    }

    private void rollbackFlush(Long videoId, long delta) {
        stringRedisTemplate.execute(
                ROLLBACK_FLUSH_SCRIPT,
                List.of(deltaKey(videoId), DIRTY_KEY),
                String.valueOf(delta),
                videoId.toString()
        );
    }

}
