package com.example.blog.post;

import com.example.blog.entity.Post;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PostModelAssembler extends RepresentationModelAssemblerSupport<Post, PostResponse> {
    private final PostMapper mapper;

    public PostModelAssembler(PostMapper mapper) {
        super(PostController.class, PostResponse.class);
        this.mapper = mapper;
    }

    @Override
    @NonNull
    public PostResponse toModel(@NonNull Post post) {
        PostResponse postModel = mapper.apply(post);

        postModel.add(
                linkTo(methodOn(PostController.class)
                        .getById(postModel.getId()))
                .withSelfRel());

        return postModel;
    }
}
