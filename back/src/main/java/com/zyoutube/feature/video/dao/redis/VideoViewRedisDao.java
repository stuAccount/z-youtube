package com.zyoutube.feature.video.dao.redis;

import java.util.Set;
import java.util.List;
import java.util.function.LongConsumer;

public interface VideoViewRedisDao {
    // --- base ---
    Long getBase(Long videoId);
    void setBase(Long videoId, long value);
    boolean setBaseIfAbsent(Long videoId, long value);
    Long incrementBase(Long videoId);
    Long incrementBase(Long videoId, long delta);
    void deleteBase(Long videoId);

    // --- delta ---
    long getDelta(Long videoId);
    void setDelta(Long videoId, long delta);
    void incrementDelta(Long videoId);
    void deleteDelta(Long videoId);

    // --- dirty ---
    void markDirty(Long videoId);
    Set<Long> getDirtyVideoIds();
    boolean isDirty(Long videoId);
    void clearDirty(Long videoId);

    // --- dedup ---
    boolean tryMarkViewedToday(Long videoId, String viewerKey);
    void deleteTodayMark(Long videoId, String viewerKey);

    // --- atomics: cross-key operations ---
    List<Object> incrementDeltaAndMarkDirtyTx(Long videoId);

    // --- flush atomics ---
    void flushOne(Long videoId, LongConsumer persister);
}
