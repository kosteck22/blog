package com.example.blog.category;

import com.example.blog.exception.DuplicateResourceException;
import com.example.blog.exception.RequestValidationException;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.post.Post;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;


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

    public Category update(Long categoryId, CategoryRequest request) {
        Category category = get(categoryId);
        String requestName = request.getName();

        if (nameAlreadyTaken(categoryId, requestName)) {
            throw new RequestValidationException("Name [%s] already taken".formatted(requestName));
        }

        category.setName(requestName);

        return categoryRepository.save(category);
    }

    private boolean nameAlreadyTaken(Long categoryId, String name) {
        Optional<Category> categoryWithGivenName = categoryRepository.findByName(name);

        return categoryWithGivenName.filter(value -> !value.getId().equals(categoryId)).isPresent();
    }
}
