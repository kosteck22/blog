package com.example.blog.post;

import com.example.blog.comment.CommentController;
import com.example.blog.tag.TagController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;
import org.springframework.lang.NonNullApi;
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
    @NonNull
    public PostModel toModel(@NonNull Post post) {
        PostModel postModel = mapper.apply(post);

        postModel.add(
                linkTo(methodOn(PostController.class)
                        .getById(postModel.getId()))
                .withSelfRel());

        return postModel;
    }
}
