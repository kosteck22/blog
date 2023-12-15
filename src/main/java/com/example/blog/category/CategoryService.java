package com.example.blog.category;

import com.example.blog.entity.Category;
import com.example.blog.exception.DuplicateResourceException;
import com.example.blog.exception.RequestValidationException;
import com.example.blog.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
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
        validateName(name);

        Category category = Category.builder()
                .name(name).build();

        return categoryRepository.save(category);
    }

    public Category update(Long categoryId, CategoryRequest request) {
        Category category = get(categoryId);
        String requestName = request.getName();
        validateName(categoryId, requestName);

        category.setName(requestName);

        return categoryRepository.save(category);
    }

    public void delete(Long categoryId) {
        Category category = get(categoryId);

        categoryRepository.delete(category);
    }

    private void validateName(Long categoryId, String requestName) {
        if (nameAlreadyTaken(categoryId, requestName)) {
            throw new RequestValidationException("Name [%s] already taken".formatted(requestName));
        }
    }

    private boolean nameAlreadyTaken(Long categoryId, String name) {
        return categoryRepository.findByName(name)
                .filter(value -> !value.getId().equals(categoryId))
                .isPresent();
    }

    private void validateName(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new DuplicateResourceException("Category with name [%s] already exists".formatted(name));
        }
    }
}
