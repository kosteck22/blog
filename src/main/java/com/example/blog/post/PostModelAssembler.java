package com.example.blog.post;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PostModelAssembler extends RepresentationModelAssemblerSupport<Post, PostModel> {
    private final PostMapper mapper;

    public PostModelAssembler(PostMapper mapper) {
        super(PostController.class, PostModel.class);
        this.mapper = mapper;
    }

    @Override
    public PostModel toModel(Post entity) {
        PostModel postModel = mapper.apply(entity);
        postModel.add(
                linkTo(methodOn(PostController.class)
                        .getById(postModel.getId()))
                .withSelfRel());

        return postModel;
    }
}
