package com.example.blog.category;

import com.example.blog.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface CategoryRepository {
    List<Category> findAll();
    Page<Category> findAll(Pageable pageable);
    Optional<Category> findById(Long id);
    Optional<Category> findByName(String name);
    Category save(Category category);
    <S extends Category> List<S> saveAll(Iterable<S> entities);
    void delete(Category category);
    boolean existsByName(String name);
}
