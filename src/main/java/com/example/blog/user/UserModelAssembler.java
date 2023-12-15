package com.example.blog.user;


import com.example.blog.entity.User;
import com.example.blog.post.PostController;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserModelAssembler extends RepresentationModelAssemblerSupport<User, UserResponse> {
    private final UserMapper mapper;

    public UserModelAssembler(UserMapper mapper) {
        super(UserController.class, UserResponse.class);
        this.mapper = mapper;
    }

    @Override
    public UserResponse toModel(User entity) {
        UserResponse user = mapper.apply(entity);

        user
                .add(linkTo(methodOn(UserController.class).getUser(user.getId())).withSelfRel())
                .add(linkTo(methodOn(PostController.class).getPostsByUser(user.getId(), null)).withRel("posts"))
                .add(linkTo(methodOn(UserController.class).getCommentsForCurrentUser(null, null)).withRel("comments"));

        return user;
    }
}
