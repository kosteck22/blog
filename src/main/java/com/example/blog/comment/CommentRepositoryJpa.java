package com.example.blog.comment;

import com.example.blog.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository("comment-jpa")
public interface CommentRepositoryJpa extends CommentRepository, JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.post.id=?1")
    Page<Comment> findAllInPost(Long postId, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.user.id=?1")
    Page<Comment> findAllInUser(Long userId, Pageable pageable);
}
