package com.example.blog.comment;

import com.example.blog.post.PostController;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.sql.Time;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CommentModelAssembler extends RepresentationModelAssemblerSupport<Comment, CommentModel> {
    private final CommentMapper mapper;

    public CommentModelAssembler(CommentMapper mapper) {
        super(CommentController.class, CommentModel.class);
        this.mapper = mapper;
    }

    @Override
    @NonNull
    public CommentModel toModel(@NonNull Comment comment) {
        CommentModel commentModel = mapper.apply(comment);

        commentModel.add(
                linkTo(methodOn(CommentController.class)
                        .get(comment.getPost().getId(), comment.getId()))
                .withSelfRel());

        commentModel.add(
                linkTo(methodOn(PostController.class)
                        .getById(comment.getPost().getId()))
                .withRel("post"));


        return commentModel;
    }
}
