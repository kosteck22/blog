package com.example.blog.post;

import com.example.blog.category.CategoryController;
import com.example.blog.comment.CommentController;
import com.example.blog.entity.Post;
import com.example.blog.tag.TagController;
import com.example.blog.user.UserController;
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

        postModel.getCategory().add(
                linkTo(methodOn(PostController.class)
                        .getPostsByCategory(postModel.getCategory().getId(), null))
                        .withRel("posts_for_category"));

        postModel
                .add(linkTo(methodOn(PostController.class).getById(postModel.getId()))
                        .withSelfRel())
                .add(linkTo(methodOn(TagController.class).getTagsForPost(postModel.getId(), null))
                        .withRel("tags"))
                .add(linkTo(methodOn(CommentController.class).getCommentsForPostAsPage(postModel.getId(), null))
                        .withRel("comments"))
                .add(linkTo(methodOn(CategoryController.class).get(post.getCategory().getId()))
                        .withRel("category"))
                .add(linkTo(methodOn(UserController.class).getUser(post.getUser().getId())).withRel("user"));

        return postModel;
    }
}
