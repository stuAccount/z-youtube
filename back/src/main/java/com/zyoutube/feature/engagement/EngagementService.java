package com.zyoutube.feature.engagement;

import com.zyoutube.common.exception.NotFoundException;
import com.zyoutube.feature.account.AccountFinder;
import com.zyoutube.feature.account.model.entity.Account;
import com.zyoutube.feature.account.model.vo.AccountSummaryResponse;
import com.zyoutube.common.context.CurrentUserProvider;
import com.zyoutube.feature.engagement.model.entity.VideoFavorite;
import com.zyoutube.feature.engagement.model.entity.VideoReaction;
import com.zyoutube.feature.engagement.model.type.ReactionType;
import com.zyoutube.feature.engagement.model.vo.FavoriteVideoSummaryResponse;
import com.zyoutube.feature.engagement.model.vo.VideoEngagementResponse;
import com.zyoutube.feature.video.VideoAccessPolicy;
import com.zyoutube.feature.video.VideoFinder;
import com.zyoutube.feature.video.model.entity.Video;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EngagementService {
    private final VideoReactionRepository videoReactionRepository;
    private final VideoFavoriteRepository videoFavoriteRepository;
    private final VideoFinder videoFinder;
    private final AccountFinder accountFinder;
    private final CurrentUserProvider currentUserProvider;

    private AccountSummaryResponse createAccountSummary(Account author) {
        return new AccountSummaryResponse(
                author.getId(),
                author.getUsername(),
                author.getNickname(),
                author.getAvatarUrl()
        );
    }

    private Video requireInteractableVideo(Long videoId, Long accountId) {
        Video video = videoFinder.findVideo(videoId);
        if (!VideoAccessPolicy.canViewDetail(video, accountId)) {
            throw new NotFoundException("Video not found");
        }
        return video;
    }

    private void increaseReactionCount(Video video, ReactionType reactionType) {
        if (reactionType == ReactionType.LIKE) {
            video.increaseLikeCount();
            return;
        }
        video.increaseDislikeCount();
    }

    private void decreaseReactionCount(Video video, ReactionType reactionType) {
        if (reactionType == ReactionType.LIKE) {
            video.decreaseLikeCount();
            return;
        }
        video.decreaseDislikeCount();
    }

    public VideoEngagementResponse createVideoEngagementResponse(Video video, Long accountId) {
        ReactionType myReaction = null;
        boolean favorited = false;

        if (accountId != null) {
            myReaction = videoReactionRepository.findByVideo_IdAndAccount_Id(video.getId(), accountId)
                    .map(VideoReaction::getReactionType)
                    .orElse(null);
            favorited = videoFavoriteRepository.existsByVideo_IdAndAccount_Id(video.getId(), accountId);
        }

        return new VideoEngagementResponse(
                video.getId(),
                video.getLikeCount(),
                video.getDislikeCount(),
                video.getFavoriteCount(),
                myReaction,
                favorited
        );
    }

    @Transactional
    public VideoEngagementResponse setReaction(Long videoId, ReactionType reactionType) {
        Account account = accountFinder.getCurrentAccount();
        Long accountId = account.getId();
        Video video = requireInteractableVideo(videoId, accountId);

        Optional<VideoReaction> existingReaction = videoReactionRepository.findByVideo_IdAndAccount_Id(videoId, accountId);
        if (existingReaction.isEmpty()) {
            VideoReaction reaction = new VideoReaction();
            reaction.assignVideo(video);
            reaction.assignAccount(account);
            reaction.changeReactionType(reactionType);
            videoReactionRepository.save(reaction);
            increaseReactionCount(video, reactionType);
            return createVideoEngagementResponse(video, accountId);
        }

        VideoReaction reaction = existingReaction.get();
        if (reaction.getReactionType() == reactionType) {
            videoReactionRepository.delete(reaction);
            decreaseReactionCount(video, reactionType);
            return createVideoEngagementResponse(video, accountId);
        }

        decreaseReactionCount(video, reaction.getReactionType());
        reaction.changeReactionType(reactionType);
        increaseReactionCount(video, reactionType);
        return createVideoEngagementResponse(video, accountId);
    }

    @Transactional
    public VideoEngagementResponse clearReaction(Long videoId) {
        Account account = accountFinder.getCurrentAccount();
        Long accountId = account.getId();
        Video video = requireInteractableVideo(videoId, accountId);

        videoReactionRepository.findByVideo_IdAndAccount_Id(videoId, accountId)
                .ifPresent(reaction -> {
                    decreaseReactionCount(video, reaction.getReactionType());
                    videoReactionRepository.delete(reaction);
                });

        return createVideoEngagementResponse(video, accountId);
    }

    @Transactional
    public VideoEngagementResponse addFavorite(Long videoId) {
        Account account = accountFinder.getCurrentAccount();
        Long accountId = account.getId();
        Video video = requireInteractableVideo(videoId, accountId);

        if (videoFavoriteRepository.findByVideo_IdAndAccount_Id(videoId, accountId).isEmpty()) {
            VideoFavorite favorite = new VideoFavorite();
            favorite.assignVideo(video);
            favorite.assignAccount(account);
            videoFavoriteRepository.save(favorite);
            video.increaseFavoriteCount();
        }

        return createVideoEngagementResponse(video, accountId);
    }

    @Transactional
    public VideoEngagementResponse removeFavorite(Long videoId) {
        Account account = accountFinder.getCurrentAccount();
        Long accountId = account.getId();
        Video video = requireInteractableVideo(videoId, accountId);

        videoFavoriteRepository.findByVideo_IdAndAccount_Id(videoId, accountId)
                .ifPresent(favorite -> {
                    videoFavoriteRepository.delete(favorite);
                    video.decreaseFavoriteCount();
                });

        return createVideoEngagementResponse(video, accountId);
    }

    @Transactional(readOnly = true)
    public Page<FavoriteVideoSummaryResponse> getMyFavorites(int page, int size) {
        Long accountId = currentUserProvider.getCurrentAccountId();
        Pageable pageable = PageRequest.of(
                Math.max(0, page),
                Math.max(1, size),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return videoFavoriteRepository.findAllByAccount_IdOrderByCreatedAtDesc(accountId, pageable)
                .map(favorite -> new FavoriteVideoSummaryResponse(
                        favorite.getVideo().getId(),
                        favorite.getVideo().getTitle(),
                        favorite.getVideo().getStatus(),
                        favorite.getVideo().getVisibilityOrDefault(),
                        createAccountSummary(favorite.getVideo().getAuthor()),
                        favorite.getVideo().getCreatedAt(),
                        favorite.getCreatedAt()
                ));
    }
}
