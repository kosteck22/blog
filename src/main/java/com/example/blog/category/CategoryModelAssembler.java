package com.example.blog.category;

import com.example.blog.post.PostController;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CategoryModelAssembler extends RepresentationModelAssemblerSupport<Category, CategoryModel> {

    public CategoryModelAssembler() {
        super(CategoryController.class, CategoryModel.class);
    }

    @Override
    @NonNull
    public CategoryModel toModel(@NonNull Category entity) {
        Long id = entity.getId();

        CategoryModel model = CategoryModel.builder()
                .id(id)
                .name(entity.getName()).build();

        model.add(linkTo(methodOn(CategoryController.class)
                .get(id))
                .withSelfRel());

        model.add(linkTo(methodOn(PostController.class)
                .getPostsByCategory(id, null))
                .withRel("posts"));

        return model;
    }
}
