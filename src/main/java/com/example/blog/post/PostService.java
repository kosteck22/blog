package com.example.blog.post;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Post save(PostRequest request) {
        Post post = Post.builder()
                .title(request.getTitle())
                .body(request.getBody()).build();

        return postRepository.save(post);
    }

    public Page<Post> fetchPostDataAsPage(Pageable pageable) {
        return postRepository.findAll(pageable);
    }
}
