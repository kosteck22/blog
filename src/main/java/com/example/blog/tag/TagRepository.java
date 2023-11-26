package com.example.blog.tag;

import com.example.blog.entity.Post;
import com.example.blog.entity.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface TagRepository {
    Tag save(Tag tag);
    Page<Tag> findAll(Pageable pageable);
    Optional<Tag> findById(Long id);
    Optional<Tag> findByName(String name);
    Page<Tag> findByPostsIn(List<Post> posts, Pageable pageable);
    boolean existsByName(String name);
    void delete(Tag tag);
}
