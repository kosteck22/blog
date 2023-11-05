package com.example.blog.post;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

@Getter @Setter
@Builder
public class PostModel extends RepresentationModel<PostModel> {
    private Long id;
    private String title;
    private String body;
}
