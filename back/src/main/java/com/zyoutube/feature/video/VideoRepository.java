package com.zyoutube.feature.video;

import com.zyoutube.feature.video.model.entity.Video;
import com.zyoutube.feature.video.model.type.VideoStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    Page<Video> findAllByAuthor_Id(Long authorId, Pageable pageable);

    Page<Video> findAllByStatus(VideoStatus status, Pageable pageable);

    Page<Video> findAllByAuthor_IdAndStatus(Long authorId, VideoStatus status, Pageable pageable);

    Optional<Video> findByIdAndAuthor_Id(Long id, Long authorId);

    @Query("""
            select v from Video v
            where v.status = :status
              and (:authorId is null or v.author.id = :authorId)
              and (
                    :keyword is null
                    or lower(v.title) like lower(concat('%', :keyword, '%'))
                    or lower(v.description) like lower(concat('%', :keyword, '%'))
              )
            """)
    Page<Video> searchVisibleVideos(@Param("authorId") Long authorId,
                                    @Param("status") VideoStatus status,
                                    @Param("keyword") String keyword,
                                    Pageable pageable);
}
