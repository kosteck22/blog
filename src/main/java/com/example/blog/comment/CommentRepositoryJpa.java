package com.example.blog.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository("comment-jpa")
public interface CommentRepositoryJpa extends CommentRepository, JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.post.id = ?1")
    Page<Comment> findAllInPost(Long postId, Pageable pageable);
}
