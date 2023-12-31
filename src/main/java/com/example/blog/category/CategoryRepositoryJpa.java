package com.example.blog.category;

import com.example.blog.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("category-jpa")
public interface CategoryRepositoryJpa extends CategoryRepository, JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
}
