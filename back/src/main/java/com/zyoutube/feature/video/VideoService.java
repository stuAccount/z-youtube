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
                Math.max(page, 0),
                Math.max(size, 1),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        /*
         * Sprint 5 hand-typing target:
         * - no filter: videoRepository.findAll(pageable)
         * - only authorId: videoRepository.findAllByAuthor_Id(authorId, pageable)
         * - only status: videoRepository.findAllByStatus(status, pageable)
         * - both: videoRepository.findAllByAuthor_IdAndStatus(authorId, status, pageable)
         * Then map Page<Video> -> Page<VideoSummaryResponse>.
         */
        throw new UnsupportedOperationException("TODO Sprint 5: implement list query, filters, and pagination");
    }

    @Transactional
    public void deleteVideo(Long id, Long requesterAccountId) {


        /*
         * Sprint 5 hand-typing target:
         * 1. find the video owned by requesterAccountId
         * 2. if not found, decide whether it means "video not found" or "no permission"
         * 3. hard delete for now; Sprint 7 can replace requesterAccountId with the logged-in user
         */
        throw new UnsupportedOperationException("TODO Sprint 5: delete video with ownership check");
    }
}
