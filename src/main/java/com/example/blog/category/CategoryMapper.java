package com.example.blog.category;

import com.example.blog.DTOMapper;
import com.example.blog.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper implements DTOMapper<Category, CategoryResponse> {
    @Override
    public CategoryResponse apply(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName()).build();
    }
}
