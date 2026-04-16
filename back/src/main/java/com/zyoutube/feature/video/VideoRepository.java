package com.zyoutube.feature.video;

import com.zyoutube.feature.video.model.entity.Video;
import com.zyoutube.feature.video.model.type.VideoStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    Page<Video> findAllByAuthor_Id(Long authorId, Pageable pageable);

    Page<Video> findAllByStatus(VideoStatus status, Pageable pageable);

    Page<Video> findAllByAuthor_IdAndStatus(Long authorId, VideoStatus status, Pageable pageable);

    Optional<Video> findByIdAndAuthor_Id(Long id, Long authorId);
}
