package com.example.blog.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface PostRepository {
    Post save(Post post);
    Page<Post> findAll(Pageable pageable);
    List<Post> findAll();
    boolean existsByTitle(String title);
    boolean existsById(Long id);
    Optional<Post> findById(Long id);
    Optional<Post> findByTitle(String title);
    void deleteById(Long id);
}