package com.zyoutube.feature.engagement;

import com.zyoutube.feature.engagement.model.entity.VideoReaction;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoReactionRepository extends JpaRepository<VideoReaction, Long> {
    Optional<VideoReaction> findByVideo_IdAndAccount_Id(Long videoId, Long accountId);

    void deleteAllByVideo_Id(Long videoId);
}
