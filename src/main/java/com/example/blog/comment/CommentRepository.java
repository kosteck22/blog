package com.example.blog.comment;

import com.example.blog.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface CommentRepository {
    Page<Comment> findAllInPost(Long postId, Pageable pageable);
    Page<Comment> findAllInUser(Long userId, Pageable pageable);
    Optional<Comment> findById(Long id);
    Comment save(Comment comment);
    void delete(Comment comment);
}
