package com.example.blog.tag;

import com.example.blog.entity.Post;
import com.example.blog.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("tag-jpa")
public interface TagRepositoryJpa extends TagRepository, JpaRepository<Tag, Long> {
    Page<Tag> findByPostsIn(List<Post> posts, Pageable pageable);
}
