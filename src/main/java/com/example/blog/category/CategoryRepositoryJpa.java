package com.example.blog.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("category-jpa")
public interface CategoryRepositoryJpa extends CategoryRepository, JpaRepository<Category, Long> {
}
