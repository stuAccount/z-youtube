package com.zyoutube.feature.video.dao.redis;

import java.util.Set;

public interface ViewDirtyDao {
        Set<Long> getDirtyVideoIds();
        void markDirty(Long videoId);
        boolean isDirty(Long videoId);
        void clearDirty(Long videoId);
}
