package com.example.blog.post;

import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface PostRepository {
    Post save(Post post);
}
