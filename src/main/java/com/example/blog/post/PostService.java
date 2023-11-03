package com.example.blog.post;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(@Qualifier("jpa") PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Post save(PostRequest request) {
        Post post = Post.builder()
                .title(request.getTitle())
                .body(request.getBody()).build();

        return postRepository.save(post);
    }
}
