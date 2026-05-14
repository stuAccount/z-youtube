package com.zyoutube.feature.video.dao.mysql;

import com.zyoutube.feature.video.model.entity.Video;
import com.zyoutube.feature.video.model.type.VideoStatus;
import com.zyoutube.feature.video.model.type.VideoVisibility;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
              and v.visibility = :visibility
              and (:authorId is null or v.author.id = :authorId)
              and (
                    :keyword is null
                    or lower(v.title) like lower(concat('%', :keyword, '%'))
                    or lower(v.description) like lower(concat('%', :keyword, '%'))
              )
            """)
    Page<Video> searchVisibleVideos(@Param("authorId") Long authorId,
                                    @Param("status") VideoStatus status,
                                    @Param("visibility") VideoVisibility visibility,
                                    @Param("keyword") String keyword,
                                    Pageable pageable);

    @Query("""
            select v from Video v
            where v.author.id = :authorId
              and (:status is null or v.status = :status)
              and (:visibility is null or v.visibility = :visibility)
              and (
                    :keyword is null
                    or lower(v.title) like lower(concat('%', :keyword, '%'))
                    or lower(v.description) like lower(concat('%', :keyword, '%'))
              )
            """)
    Page<Video> searchOwnedVideos(@Param("authorId") Long authorId,
                                  @Param("status") VideoStatus status,
                                  @Param("visibility") VideoVisibility visibility,
                                  @Param("keyword") String keyword,
                                  Pageable pageable);

    @Modifying
    @Query("""
            update Video v
            set v.viewCount = v.viewCount + 1
            where v.id = :videoId
            """)
    int incrementViewCount(@Param("videoId") Long videoId);

    @Modifying
    @Query("""
            update Video v
            set v.viewCount = :viewCount
            where v.id = :videoId
            """)
    // 注意：这里是用 Redis 当前总量直接替换数据库值，不是做数据库增量累加。
    int replaceViewCount(@Param("videoId") Long videoId, @Param("viewCount") long viewCount);

    @Query("""
            select v.viewCount from Video v
            where v.id = :videoId
            """)
    Optional<Long> findViewCountById(@Param("videoId") Long videoId);
}
