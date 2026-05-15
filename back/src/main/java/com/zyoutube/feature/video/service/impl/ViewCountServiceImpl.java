package com.zyoutube.feature.video.service.impl;

import com.zyoutube.common.context.CurrentUserProvider;
import com.zyoutube.common.context.RequestContextProvider;
import com.zyoutube.feature.video.VideoAccessPolicy;
import com.zyoutube.feature.video.dao.redis.VideoViewRedisDao;
import com.zyoutube.feature.video.model.entity.Video;
import com.zyoutube.feature.video.model.vo.VideoViewCountResponse;
import com.zyoutube.feature.video.service.ViewCountService;
import lombok.AllArgsConstructor;
import com.zyoutube.feature.video.VideoFinder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
// exception
import com.zyoutube.common.exception.NotFoundException;

@Service
@AllArgsConstructor
public class ViewCountServiceImpl implements ViewCountService {
    private final VideoFinder videoFinder;
    private final RequestContextProvider requestContextProvider;
    private final CurrentUserProvider currentUserProvider;
    private final VideoViewRedisDao videoViewRedisDao;


    @Override
    @Transactional
    // TODO: 校验这次播放是否有效 防刷 去重 判断是否满足计数条件 播放量加一 记录行为日志 发消息或更新缓存
    public VideoViewCountResponse recordView(Long videoId) {
        /**
         * 先通过传参传出来的ID找到对应的视频，然后根据政策鉴权，判断当前登录用户有没有权限观看这个视频。如果没有权限则抛异常。
         * 然后生成一个去重key，比如视频ID加用户ID加时间窗口，然后用Redis的setnx防止短时间重复刷视频。
         * 去重通过后，根据有效播放条件来判断是否要对视频播放量自增，
         * 最后通过其他组件异步地、定时地把视频播放量从 Redis 缓存层中同步到数据库里
         */
        Video video = videoFinder.findVideo(videoId);
        Long viewerId = currentUserProvider.getCurrentAccountIdOrNull();
        if (!VideoAccessPolicy.canViewDetail(video, viewerId)) {
            throw new NotFoundException("Video not found");
        }

        // 登录的用户用 accountId 作为去重 key，未登录的用户用 IP 地址 + User-Agent 作为去重 key
        String viewerkey;
        if (viewerId != null) {
            viewerkey = "account:" + viewerId;
        } else {
            String ip = requestContextProvider.getCurrentRequestIpOrNull();
            String userAgent = requestContextProvider.getCurrentRequestUserAgentOrNull();
            String ipHash = String.valueOf(ip == null ? 0 : ip.hashCode());
            String uaHash = String.valueOf(userAgent == null ? 0 : userAgent.hashCode());
            viewerkey = "anon:" + ipHash + ":" + uaHash;
        }

        videoViewRedisDao.incrementDeltaAndMarkDirtyTx(videoId);

        return new VideoViewCountResponse(videoId, getViewCount(videoId)); // 直接从缓存里读最新的播放量返回给前端
    }

    @Override
    public long getViewCount(Long videoId) {
        // TODO: 后期可以引入布隆过滤器来解决缓存穿透问题，避免频繁访问数据库

        // 先从缓存里面读视频播放量的基准值，如果缓存里面没有，就从数据库里面读。并写到缓存层，然后再返回加上缓存里的增量的总量
        Long base = videoViewRedisDao.getBase(videoId);
        if (base != null) {
            return base + videoViewRedisDao.getDelta(videoId);
        }

        Video video = videoFinder.findVideo(videoId);
        long dbBase = video.getViewCount();
        if (videoViewRedisDao.setBaseIfAbsent(videoId, dbBase)) {
            return dbBase + videoViewRedisDao.getDelta(videoId);
        }
        return videoViewRedisDao.getBase(videoId) + videoViewRedisDao.getDelta(videoId);
    }

}
