package com.example.blog.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("post-jpa")
public interface PostRepositoryJPA extends PostRepository, JpaRepository<Post, Long> {
    boolean existsByTitle(String title);
}
