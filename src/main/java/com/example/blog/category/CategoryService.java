package com.example.blog.category;

import com.example.blog.exception.DuplicateResourceException;
import com.example.blog.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(@Qualifier("category-jpa") CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Page<Category> getCategoriesAsPage(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    public Category get(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category with id [%d] not found"
                        .formatted(categoryId)));
    }

    public Category save(CategoryRequest categoryRequest) {
        String name = categoryRequest.getName();

        if (categoryRepository.existsByName(name)) {
            throw new DuplicateResourceException("Category with name [%s] already exists".formatted(name));
        }

        Category category = Category.builder()
                .name(name).build();

        return categoryRepository.save(category);
    }

    public void delete(Long categoryId) {
        Category category = get(categoryId);

        categoryRepository.delete(category);
    }
}
