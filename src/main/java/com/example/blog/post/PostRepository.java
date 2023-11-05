package com.example.blog.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface PostRepository {
    Post save(Post post);
    Page<Post> findAll(Pageable pageable);
    boolean existsByTitle(String title);
}
