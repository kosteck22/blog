package com.example.blog.base;

import com.example.blog.auth.AuthenticationController;
import com.example.blog.category.CategoryController;
import com.example.blog.post.PostController;
import com.example.blog.tag.TagController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/")
public class MainController {

    @GetMapping
    public ResponseEntity<?> index() {
        RootEntity rootEntity = createRootEntity();

        return ResponseEntity.ok(rootEntity);
    }

    private RootEntity createRootEntity() {
        String loginUrl = linkTo(
                methodOn(AuthenticationController.class)
                        .login(null))
                .toString();

        String signupUrl = linkTo(
                methodOn(AuthenticationController.class)
                        .register(null))
                .toString();
        String categoriesUrl = linkTo(
                methodOn(CategoryController.class).
                        getCategoriesAsPage(null))
                .toString();

        String postsUrl = linkTo(
                methodOn(PostController.class)
                        .getPostsAsPage(null))
                .toString();

        String tagsUrl = linkTo(
                methodOn(TagController.class)
                        .getTagsAsPage(null))
                .toString();

        return RootEntity.builder()
                .signupUrl(signupUrl)
                .loginUrl(loginUrl)
                .categoriesUrl(categoriesUrl)
                .postsUrl(postsUrl)
                .tagsUrl(tagsUrl).build();
    }
}
