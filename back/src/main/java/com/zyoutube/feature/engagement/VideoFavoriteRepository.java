package com.zyoutube.feature.engagement;

import com.zyoutube.feature.engagement.model.entity.VideoFavorite;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoFavoriteRepository extends JpaRepository<VideoFavorite, Long> {
    Optional<VideoFavorite> findByVideo_IdAndAccount_Id(Long videoId, Long accountId);

    Page<VideoFavorite> findAllByAccount_IdOrderByCreatedAtDesc(Long accountId, Pageable pageable);

    boolean existsByVideo_IdAndAccount_Id(Long videoId, Long accountId);

    void deleteAllByVideo_Id(Long videoId);
}
