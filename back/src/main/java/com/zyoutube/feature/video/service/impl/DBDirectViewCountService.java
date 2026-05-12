package com.zyoutube.feature.video.service.impl;

import com.zyoutube.feature.auth.context.CurrentUserProvider;
import com.zyoutube.feature.video.VideoAccessPolicy;
import com.zyoutube.feature.video.VideoRepository;
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
public class DBDirectViewCountService implements ViewCountService {
    private final VideoRepository videoRepository;
    private final VideoFinder videoFinder;
    private final CurrentUserProvider currentUserProvider;


    @Override
    @Transactional
    public VideoViewCountResponse recordView(Long videoId) {
        Video video = videoFinder.findVideo(videoId);

        Long viewerId = currentUserProvider.getCurrentAccountIdOrNull();
        if (!VideoAccessPolicy.canViewDetail(video, viewerId)) {
            throw new NotFoundException("Video not found");
        }

        video.increaseViewCount();
        videoRepository.save(video);

        return new VideoViewCountResponse(videoId, video.getViewCount());
    }
}
