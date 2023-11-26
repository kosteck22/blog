package com.example.blog.post;

import com.example.blog.DTOMapper;
import com.example.blog.category.CategoryMapper;
import com.example.blog.category.CategoryModel;
import com.example.blog.entity.Post;
import org.springframework.stereotype.Component;

@Component
public class PostMapper implements DTOMapper<Post, PostModel> {
    private final CategoryMapper categoryMapper;

    public PostMapper(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public PostModel apply(Post post) {
        CategoryModel category = categoryMapper.apply(post.getCategory());

        return PostModel.builder()
                .id(post.getId())
                .title(post.getTitle())
                .body(post.getBody())
                .category(category).build();
    }
}
