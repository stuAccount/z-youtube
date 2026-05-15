package com.zyoutube.feature.video.service;

import com.zyoutube.feature.video.dao.mysql.VideoRepository;
import com.zyoutube.feature.video.dao.redis.VideoViewRedisDao;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;

@Service
@AllArgsConstructor
public class ViewCountSyncService {
    private final VideoViewRedisDao videoViewRedisDao;
    private final VideoRepository videoRepository;

    /**
     * schedulded flush
     */
    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void syncViewCountsToDatabase() {
        Set<Long> dirtyVideoIds = videoViewRedisDao.getDirtyVideoIds();
        for (Long videoId : dirtyVideoIds) {
            videoViewRedisDao.flushOne(videoId, delta -> videoRepository.incrementViewCountBy(videoId, delta));
        }
    }
}
