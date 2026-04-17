package com.zyoutube.feature.video;

import com.zyoutube.feature.account.AccountRepository;
import com.zyoutube.feature.account.model.entity.Account;
import com.zyoutube.feature.account.model.vo.AccountSummaryResponse;
import com.zyoutube.feature.video.model.dto.CreateVideoRequest;
import com.zyoutube.feature.video.model.entity.Video;
import com.zyoutube.feature.video.model.type.VideoStatus;
import com.zyoutube.feature.video.model.vo.VideoDetailResponse;
import com.zyoutube.feature.video.model.vo.VideoSummaryResponse;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class VideoService {
    private final VideoRepository videoRepository;
    private final AccountRepository accountRepository;
    private final EntityManager entityManager;

    public VideoService(VideoRepository videoRepository,
                        AccountRepository accountRepository,
                        EntityManager entityManager) {
        this.videoRepository = videoRepository;
        this.accountRepository = accountRepository;
        this.entityManager = entityManager;
    }

    private AccountSummaryResponse createAccountSummary(Account author) {
        return new AccountSummaryResponse(
                author.getId(),
                author.getUsername(),
                author.getNickname(),
                author.getAvatarUrl()
        );
    }

    @Transactional
    public VideoDetailResponse createVideo(CreateVideoRequest req) {
        Account author = accountRepository.findById(req.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        if (author.isDeleted()) {
            throw new IllegalStateException("Withdrawn account can not create videos");
        }

        Video video = new Video();
        video.assignAuthor(author);
        video.changeTitle(req.getTitle().trim());
        video.changeDescription(req.getDescription().trim());
        video.changeStatus(VideoStatus.DRAFT);

        Video savedVideo = videoRepository.save(video);

        AccountSummaryResponse authorSummary = createAccountSummary(author);

        return new VideoDetailResponse(
                savedVideo.getId(),
                savedVideo.getTitle(),
                savedVideo.getDescription(),
                savedVideo.getStatus(),
                authorSummary,
                savedVideo.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public VideoDetailResponse getDetail(Long id) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Video not found"));

        if (video.getStatus() != VideoStatus.PUBLISHED) {
            throw new IllegalStateException("Video is private");
        }

        AccountSummaryResponse authorSummary = createAccountSummary(video.getAuthor());

        return new VideoDetailResponse(
                video.getId(),
                video.getTitle(),
                video.getDescription(),
                video.getStatus(),
                authorSummary,
                video.getCreatedAt()
        );
    }

    public Page<VideoSummaryResponse> getVideos(Long authorId, VideoStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(
                Math.max(0, page),
                Math.max(1, size),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<Video> videoPage;

        if (authorId != null && status != null) {
            videoPage = videoRepository.findAllByAuthor_IdAndStatus(authorId, status, pageable);
        } else if (authorId != null) {
            videoPage = videoRepository.findAllByAuthor_Id(authorId, pageable);
        } else if (status != null) {
            videoPage = videoRepository.findAllByStatus(status, pageable);
        } else {
            videoPage = videoRepository.findAll(pageable);
        }

        return videoPage.map(video -> new VideoSummaryResponse(
                video.getId(),
                video.getTitle(),
                video.getStatus(),
                createAccountSummary(video.getAuthor()),
                video.getCreatedAt()
        ));
    }

    @Transactional
    public void deleteVideo(Long id, Long requesterAccountId) {
        Account account = accountRepository.findById(requesterAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        if (account.isDeleted()) {
            throw new IllegalStateException("Withdrawn account can not delete videos");
        }

        Video video = videoRepository.findByIdAndAuthor_Id(id, requesterAccountId)
                        .orElseThrow(() -> new IllegalArgumentException("Video not found"));

        videoRepository.delete(video);
    }
}
