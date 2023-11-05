package com.example.blog.post;

import com.example.blog.DTOMapper;
import org.springframework.stereotype.Component;

@Component
public class PostMapper implements DTOMapper<Post, PostModel> {
    @Override
    public PostModel apply(Post post) {
        return PostModel.builder()
                .id(post.getId())
                .title(post.getTitle())
                .body(post.getBody()).build();
    }
}
