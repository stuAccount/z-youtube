package com.zyoutube.feature.comment;

import com.zyoutube.feature.comment.model.entity.Comment;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // Study point: derived query name -> SQL by video_id with pagination.
    Page<Comment> findAllByVideo_Id(Long videoId, Pageable pageable);

    Optional<Comment> findByIdAndAuthor_Id(Long id, Long authorId);
}
