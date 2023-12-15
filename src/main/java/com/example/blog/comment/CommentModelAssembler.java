package com.example.blog.comment;

import com.example.blog.entity.Comment;
import com.example.blog.post.PostController;
import com.example.blog.user.UserController;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CommentModelAssembler extends RepresentationModelAssemblerSupport<Comment, CommentResponse> {
    private final CommentMapper mapper;

    public CommentModelAssembler(CommentMapper mapper) {
        super(CommentController.class, CommentResponse.class);
        this.mapper = mapper;
    }

    @Override
    @NonNull
    public CommentResponse toModel(@NonNull Comment comment) {
        CommentResponse commentModel = mapper.apply(comment);

        commentModel
                .add(linkTo(methodOn(CommentController.class).get(comment.getPost().getId(), comment.getId()))
                        .withSelfRel())
                .add(linkTo(methodOn(PostController.class).getById(comment.getPost().getId()))
                        .withRel("post"))
                .add(linkTo(methodOn(UserController.class).getUser(comment.getUser().getId())).withRel("user"));


        return commentModel;
    }
}
