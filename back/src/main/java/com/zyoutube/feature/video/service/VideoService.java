package com.zyoutube.feature.video.service;

import com.zyoutube.common.exception.NotFoundException;
import com.zyoutube.feature.engagement.VideoFavoriteRepository;
import com.zyoutube.feature.engagement.VideoReactionRepository;
import com.zyoutube.feature.engagement.model.entity.VideoFavorite;
import com.zyoutube.feature.engagement.model.entity.VideoReaction;
import com.zyoutube.feature.engagement.model.type.ReactionType;
import com.zyoutube.feature.account.AccountFinder;
import com.zyoutube.feature.account.model.entity.Account;
import com.zyoutube.feature.account.model.vo.AccountSummaryResponse;
import com.zyoutube.feature.auth.context.CurrentUserProvider;
import com.zyoutube.feature.comment.CommentRepository;
import com.zyoutube.feature.video.VideoAccessPolicy;
import com.zyoutube.feature.video.VideoFinder;
import com.zyoutube.feature.video.VideoRepository;
import com.zyoutube.feature.video.model.vo.VideoViewCountResponse;
import com.zyoutube.feature.video.model.dto.CreateVideoRequest;
import com.zyoutube.feature.video.model.dto.UpdateVideoRequest;
import com.zyoutube.feature.video.model.entity.Video;
import com.zyoutube.feature.video.model.type.VideoStatus;
import com.zyoutube.feature.video.model.type.VideoVisibility;
import com.zyoutube.feature.video.model.vo.MyVideoSummaryResponse;
import com.zyoutube.feature.video.model.vo.PublicVideoSummaryResponse;
import com.zyoutube.feature.video.model.vo.VideoDetailResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class VideoService {
    private final VideoRepository videoRepository;
    private final AccountFinder accountFinder;
    private final CommentRepository commentRepository;
    private final VideoReactionRepository videoReactionRepository;
    private final VideoFavoriteRepository videoFavoriteRepository;
    private final CurrentUserProvider currentUserProvider;
    private final VideoFinder videoFinder;

    private AccountSummaryResponse createAccountSummary(Account author) {
        return new AccountSummaryResponse(
                author.getId(),
                author.getUsername(),
                author.getNickname(),
                author.getAvatarUrl()
        );
    }

    private VideoDetailResponse createVideoDetailResponse(Video video) {
        Long currentAccountId = currentUserProvider.getCurrentAccountIdOrNull();
        ReactionType myReaction = null;
        boolean favorited = false;
        if (currentAccountId != null) {
            myReaction = videoReactionRepository.findByVideo_IdAndAccount_Id(video.getId(), currentAccountId)
                    .map(VideoReaction::getReactionType)
                    .orElse(null);
            favorited = videoFavoriteRepository.findByVideo_IdAndAccount_Id(video.getId(), currentAccountId)
                    .map(VideoFavorite::getId)
                    .isPresent();
        }

        return new VideoDetailResponse(
                video.getId(),
                video.getTitle(),
                video.getDescription(),
                video.getVideoUrl(),
                video.getCoverUrl(),
                video.getStatus(),
                video.getVisibilityOrDefault(),
                createAccountSummary(video.getAuthor()),
                video.getCreatedAt(),
                video.getViewCount(),
                video.getLikeCount(),
                video.getDislikeCount(),
                video.getFavoriteCount(),
                myReaction,
                favorited
        );
    }

    private PublicVideoSummaryResponse createPublicVideoSummaryResponse(Video video) {
        return new PublicVideoSummaryResponse(
                video.getId(),
                video.getTitle(),
                video.getVideoUrl(),
                video.getCoverUrl(),
                video.getStatus(),
                video.getVisibilityOrDefault(),
                createAccountSummary(video.getAuthor()),
                video.getCreatedAt()
        );
    }

    private MyVideoSummaryResponse createMyVideoSummaryResponse(Video video) {
        return new MyVideoSummaryResponse(
                video.getId(),
                video.getTitle(),
                video.getVideoUrl(),
                video.getCoverUrl(),
                video.getStatus(),
                video.getVisibilityOrDefault(),
                video.getCreatedAt(),
                video.getUpdatedAt()
        );
    }

    /**
     * 规范化可编辑字段的值
     * 
     * @param value     待规范化的字段值
     * @param fieldName 字段名称，用于异常信息
     * @return 去除首尾空格后的非空字符串
     * @throws IllegalArgumentException 当字段值为空或仅包含空白字符时抛出
     */
    private String normalizeEditableField(String value, String fieldName) {
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " can not be blank");
        }
        return trimmed;
    }

    /**
     * 规范化搜索关键词，去除首尾空格并处理空值情况
     * 
     * @param keyword 原始搜索关键词，可能为 null 或包含首尾空格
     * @return 规范化后的关键词，去除首尾空格；如果输入为 null 或去除空格后为空字符串，则返回 null
     */
    private String normalizeKeyword(String keyword) {
        if (keyword == null) {
            return null;
        }

        String trimmedKeyword = keyword.trim();
        return trimmedKeyword.isEmpty() ? null : trimmedKeyword;
    }

    @Transactional
    public VideoDetailResponse createVideo(CreateVideoRequest req) {
        Account author = accountFinder.getCurrentAccount();

        Video video = new Video();
        video.assignAuthor(author);
        video.changeTitle(req.getTitle().trim());
        video.changeDescription(req.getDescription().trim());
        video.changeVideoUrl(req.getVideoUrl().trim());
        video.changeCoverUrl(normalizeOptionalUrl(req.getCoverUrl()));
        video.changeStatus(VideoStatus.DRAFT);
        video.changeVisibility(req.getVisibility() != null ? req.getVisibility() : VideoVisibility.PRIVATE);

        Video savedVideo = videoRepository.save(video);
        return createVideoDetailResponse(savedVideo);
    }

    @Transactional
    public VideoDetailResponse updateVideo(Long videoId, UpdateVideoRequest req) {
        if (req.getTitle() == null
                && req.getDescription() == null
                && req.getVisibility() == null
                && req.getVideoUrl() == null
                && req.getCoverUrl() == null) {
            throw new IllegalArgumentException("At least one field must be provided");
        }

        Video video = videoFinder.getOwnedVideo(videoId);

        if (req.getTitle() != null) {
            video.changeTitle(normalizeEditableField(req.getTitle(), "Title"));
        }
        if (req.getDescription() != null) {
            video.changeDescription(normalizeEditableField(req.getDescription(), "Description"));
        }
        if (req.getVideoUrl() != null) {
            video.changeVideoUrl(normalizeEditableField(req.getVideoUrl(), "Video url"));
        }
        if (req.getCoverUrl() != null) {
            video.changeCoverUrl(normalizeOptionalUrl(req.getCoverUrl()));
        }
        if (req.getVisibility() != null) {
            video.changeVisibility(req.getVisibility());
        }

        return createVideoDetailResponse(video);
    }

    @Transactional
    public VideoDetailResponse publishVideo(Long videoId) {
        Video video = videoFinder.getOwnedVideo(videoId);
        video.publish();

        return createVideoDetailResponse(video);
    }

    @Transactional
    public VideoDetailResponse unpublishVideo(Long videoId) {
        Video video = videoFinder.getOwnedVideo(videoId);
        video.unpublish();

        return createVideoDetailResponse(video);
    }


    @Transactional(readOnly = true)
    public VideoDetailResponse getVideoDetail(Long videoId) {
        Video video = videoFinder.findVideo(videoId);

        Long viewerId = currentUserProvider.getCurrentAccountIdOrNull();
        if (!VideoAccessPolicy.canViewDetail(video, viewerId)) {
            // Throw 404 Not Found instead of 403 Forbidden or 401 Unauthorized to avoid leaking the existence of the video
            throw new NotFoundException("Video not found");
        }
        return createVideoDetailResponse(video);
    }



    @Transactional(readOnly = true)
    public Page<PublicVideoSummaryResponse> getVideos(Long authorId,
                                                      VideoStatus status,
                                                      VideoVisibility visibility,
                                                      String keyword,
                                                      int page,
                                                      int size) {
        Pageable pageable = PageRequest.of(
                Math.max(0, page),
                Math.max(1, size),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        if (status != null && status != VideoStatus.PUBLISHED) {
            throw new IllegalArgumentException("Only published videos can be searched");
        }
        if (visibility != null && visibility != VideoVisibility.PUBLIC) {
            throw new IllegalArgumentException("Only public videos can be searched");
        }

        VideoStatus visibleStatus = VideoStatus.PUBLISHED;
        VideoVisibility visibleVisibility = VideoVisibility.PUBLIC;

        String normalizedKeyword = normalizeKeyword(keyword);
        Page<Video> videoPage = videoRepository.searchVisibleVideos(
                authorId,
                visibleStatus,
                visibleVisibility,
                normalizedKeyword,
                pageable
        );

        return videoPage.map(this::createPublicVideoSummaryResponse);
    }

    @Transactional(readOnly = true)
    public Page<PublicVideoSummaryResponse> getLatestFeed(int page, int size) {
        return getVideos(null, VideoStatus.PUBLISHED, VideoVisibility.PUBLIC, null, page, size);
    }

    @Transactional(readOnly = true)
    public Page<MyVideoSummaryResponse> getMyVideos(VideoStatus status,
                                                    VideoVisibility visibility,
                                                    String keyword,
                                                    int page,
                                                    int size) {
        Pageable pageable = PageRequest.of(
                Math.max(0, page),
                Math.max(1, size),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Long currentAccountId = currentUserProvider.getCurrentAccountId();
        String normalizedKeyword = normalizeKeyword(keyword);

        Page<Video> videoPage = videoRepository.searchOwnedVideos(
                currentAccountId,
                status,
                visibility,
                normalizedKeyword,
                pageable
        );

        return videoPage.map(this::createMyVideoSummaryResponse);
    }

    @Transactional
    public void deleteVideo(Long videoId) {
        Video video = videoFinder.getOwnedVideo(videoId);

        commentRepository.deleteAllByVideo_Id(videoId);
        videoReactionRepository.deleteAllByVideo_Id(videoId);
        videoFavoriteRepository.deleteAllByVideo_Id(videoId);

        videoRepository.delete(video);
    }

    private String normalizeOptionalUrl(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
