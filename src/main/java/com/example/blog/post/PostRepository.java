package com.example.blog.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface PostRepository {
    Post save(Post post);
    Page<Post> findAll(Pageable pageable);
}
