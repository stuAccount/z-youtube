package com.zyoutube.feature.video;

import com.zyoutube.feature.account.AccountRepository;
import com.zyoutube.feature.account.model.entity.Account;
import com.zyoutube.feature.account.model.vo.AccountSummaryResponse;
import com.zyoutube.feature.auth.context.CurrentUserProvider;
import com.zyoutube.feature.video.model.dto.CreateVideoRequest;
import com.zyoutube.feature.video.model.dto.UpdateVideoRequest;
import com.zyoutube.feature.video.model.entity.Video;
import com.zyoutube.feature.video.model.type.VideoStatus;
import com.zyoutube.feature.video.model.type.VideoVisibility;
import com.zyoutube.feature.video.model.vo.VideoDetailResponse;
import com.zyoutube.feature.video.model.vo.VideoSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VideoService {
    private final VideoRepository videoRepository;
    private final AccountRepository accountRepository;
    private final CurrentUserProvider currentUserProvider;

    public VideoService(VideoRepository videoRepository,
                        AccountRepository accountRepository,
                        CurrentUserProvider currentUserProvider) {
        this.videoRepository = videoRepository;
        this.accountRepository = accountRepository;
        this.currentUserProvider = currentUserProvider;
    }

    private AccountSummaryResponse createAccountSummary(Account author) {
        return new AccountSummaryResponse(
                author.getId(),
                author.getUsername(),
                author.getNickname(),
                author.getAvatarUrl()
        );
    }

    private VideoDetailResponse createVideoDetailResponse(Video video) {
        return new VideoDetailResponse(
                video.getId(),
                video.getTitle(),
                video.getDescription(),
                video.getStatus(),
                video.getVisibilityOrDefault(),
                createAccountSummary(video.getAuthor()),
                video.getCreatedAt()
        );
    }

    private String normalizeEditableField(String value, String fieldName) {
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " can not be blank");
        }
        return trimmed;
    }

    private Video getOwnedVideo(Long videoId) {
        return videoRepository.findByIdAndAuthor_Id(videoId, currentUserProvider.getCurrentAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Video not found"));
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null) {
            return null;
        }

        String trimmedKeyword = keyword.trim();
        return trimmedKeyword.isEmpty() ? null : trimmedKeyword;
    }

    @Transactional
    public VideoDetailResponse createVideo(CreateVideoRequest req) {
        Account author = accountRepository.findById(currentUserProvider.getCurrentAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        Video video = new Video();
        video.assignAuthor(author);
        video.changeTitle(req.getTitle().trim());
        video.changeDescription(req.getDescription().trim());
        video.changeStatus(VideoStatus.DRAFT);
        video.changeVisibility(req.getVisibility() != null ? req.getVisibility() : VideoVisibility.PRIVATE);

        Video savedVideo = videoRepository.save(video);
        return createVideoDetailResponse(savedVideo);
    }

    @Transactional
    public VideoDetailResponse updateVideo(Long videoId, UpdateVideoRequest req) {
        if (req.getTitle() == null && req.getDescription() == null && req.getVisibility() == null) {
            throw new IllegalArgumentException("At least one field must be provided");
        }

        Video video = getOwnedVideo(videoId);

        if (req.getTitle() != null) {
            video.changeTitle(normalizeEditableField(req.getTitle(), "Title"));
        }
        if (req.getDescription() != null) {
            video.changeDescription(normalizeEditableField(req.getDescription(), "Description"));
        }
        if (req.getVisibility() != null) {
            video.changeVisibility(req.getVisibility());
        }

        return createVideoDetailResponse(video);
    }

    @Transactional
    public VideoDetailResponse publishVideo(Long videoId) {
        Video video = getOwnedVideo(videoId);
        video.publish();

        return createVideoDetailResponse(video);
    }

    @Transactional
    public VideoDetailResponse unpublishVideo(Long videoId) {
        Video video = getOwnedVideo(videoId);
        video.unpublish();

        return createVideoDetailResponse(video);
    }

    @Transactional(readOnly = true)
    public VideoDetailResponse getDetail(Long videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("Video not found"));

        if (!video.isPubliclyVisible()) {
            throw new IllegalStateException("Video is private");
        }

        return createVideoDetailResponse(video);
    }

    @Transactional(readOnly = true)
    public Page<VideoSummaryResponse> getVideos(Long authorId,
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

        return videoPage.map(video -> new VideoSummaryResponse(
                video.getId(),
                video.getTitle(),
                video.getStatus(),
                video.getVisibilityOrDefault(),
                createAccountSummary(video.getAuthor()),
                video.getCreatedAt()
        ));
    }

    @Transactional
    public void deleteVideo(Long videoId) {
        Video video = getOwnedVideo(videoId);
        videoRepository.delete(video);
    }
}
