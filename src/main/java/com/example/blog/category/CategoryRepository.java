package com.example.blog.category;

import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface CategoryRepository {
    List<Category> findAll();
    Category save(Category category);
    Optional<Category> findById(Long id);
    void delete(Category category);
}
