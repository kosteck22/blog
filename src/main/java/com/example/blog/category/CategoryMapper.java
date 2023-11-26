package com.example.blog.category;

import com.example.blog.DTOMapper;
import com.example.blog.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper implements DTOMapper<Category, CategoryModel> {
    @Override
    public CategoryModel apply(Category category) {
        return CategoryModel.builder()
                .id(category.getId())
                .name(category.getName()).build();
    }
}
