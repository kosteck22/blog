package com.example.blog.post;

import com.example.blog.category.CategoryController;
import com.example.blog.comment.CommentController;
import com.example.blog.tag.TagController;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class DetailedPostModelAssembler extends RepresentationModelAssemblerSupport<Post, PostModel> {
    private final PostMapper mapper;

    public DetailedPostModelAssembler(PostMapper mapper) {
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

        postModel.add(
                linkTo(methodOn(TagController.class)
                        .getTagsForPost(postModel.getId(), null))
                        .withRel("tags"));

        postModel.add(
                linkTo(methodOn(CommentController.class)
                        .getCommentsForPostAsPage(postModel.getId(), null))
                        .withRel("comments"));

        if (post.getCategory() != null) {
            postModel.add(
                    linkTo(methodOn(CategoryController.class)
                            .get(post.getCategory().getId()))
                            .withRel("category"));
        }

        return postModel;
    }
}
