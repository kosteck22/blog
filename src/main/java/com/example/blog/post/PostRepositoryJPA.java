package com.example.blog.post;

import com.example.blog.tag.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository("post-jpa")
public interface PostRepositoryJPA extends PostRepository, JpaRepository<Post, Long> {
    boolean existsByTitle(String title);
    Optional<Post> findByTitle(String title);
    Page<Post> findByTagsIn(List<Tag> tags, Pageable pageable);
}
