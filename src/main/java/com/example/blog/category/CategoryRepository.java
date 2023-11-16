package com.example.blog.category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface CategoryRepository {
    List<Category> findAll();
    Page<Category> findAll(Pageable pageable);
    Category save(Category category);
    Optional<Category> findById(Long id);
    void delete(Category category);
}
