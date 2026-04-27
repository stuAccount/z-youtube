package com.zyoutube.feature.video;

import com.zyoutube.common.exception.NotFoundException;
import com.zyoutube.feature.auth.context.CurrentUserProvider;
import com.zyoutube.feature.video.model.entity.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VideoFinder {
    private final VideoRepository videoRepository;
    private final CurrentUserProvider currentUserProvider;

    /**
     * 根据视频ID查找视频
     *
     * @param videoId 视频ID
     * @return 视频对象
     * @throws NotFoundException 如果视频不存在
     */
    public Video findVideo(Long videoId) {
        return videoRepository.findById(videoId)
                .orElseThrow(() -> new NotFoundException("Video not found"));
    }

    /**
     * 获取当前用户拥有的视频
     *
     * @param videoId 视频ID
     * @return 当前用户拥有的视频对象
     * @throws NotFoundException 如果视频不存在或不属于当前用户
     */
    public Video getOwnedVideo(Long videoId) {
        return videoRepository.findByIdAndAuthor_Id(videoId, currentUserProvider.getCurrentAccountId())
                .orElseThrow(() -> new NotFoundException("Video not found"));
    }
}
