package com.zyoutube.feature.video.service;

import com.zyoutube.feature.video.dao.mysql.VideoRepository;
import com.zyoutube.feature.video.dao.redis.ViewCountCache;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ViewCountSyncService {
    private final ViewCountCache viewCountCache;
    private final VideoRepository videoRepository;

    /**
     * 注意：当前采用的是“总量替换回写”方案，不是“增量累加”方案。
     * Redis 中的 video:view:count:{videoId} 被视为当前播放量真值，
     * 定时任务会直接用这个总量覆盖数据库中的 view_count。
     * 如果后续改成增量同步，需要改成单独维护 delta key，而不是继续 replace。
     */
    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void syncViewCountsToDatabase() {
        Map<Long, Long> counts = viewCountCache.findAll();
        for (Map.Entry<Long, Long> entry : counts.entrySet()) {
            videoRepository.replaceViewCount(entry.getKey(), entry.getValue());
        }
    }
}
