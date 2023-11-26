package com.example.blog.category;

import com.example.blog.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
class CategoryRepositoryJpaTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void saveCategory() {
        //given
        String categoryName = "Frontend Development";
        Category category = Category.builder()
                .name(categoryName)
                .build();

        //when
        Category result = categoryRepository.save(category);

        //then
        assertThat(result).isInstanceOf(Category.class);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isGreaterThan(0);
        assertThat(result.getName()).isEqualTo(categoryName);
    }

    @Test
    void findCategoryById() {
        //given
        Category category = Category.builder()
                .name("Test Category")
                .build();

        Category savedCategory = categoryRepository.save(category);

        //when
        Optional<Category> result = categoryRepository.findById(savedCategory.getId());

        //then
        assertThat(result).isPresent();
        assertThat(savedCategory.getName()).isEqualTo(result.get().getName());
    }

    @Test
    void updateCategory() {
        //when
        Category category = Category.builder()
                .name("Test Category")
                .build();

        Category savedCategory = categoryRepository.save(category);

        //when
        savedCategory.setName("Updated Category");
        Category result = categoryRepository.save(savedCategory);

        //then
        assertThat(savedCategory.getId()).isEqualTo(result.getId());
        assertThat("Updated Category").isEqualTo(result.getName());
    }

    @Test
    void deleteCategory() {
        //given
        Category category = Category.builder()
                .name("Test Category")
                .build();

        Category savedCategory = categoryRepository.save(category);

        //when
        categoryRepository.delete(savedCategory);

        //then
        Optional<Category> deletedCategory = categoryRepository.findById(savedCategory.getId());
        assertThat(deletedCategory).isEmpty();
    }

    @Test
    void findAllCategories() {
        //given
        Category category1 = Category.builder()
                .name("Category 1")
                .build();

        Category category2 = Category.builder()
                .name("Category 2")
                .build();

        categoryRepository.save(category1);
        categoryRepository.save(category2);

        //when
        List<Category> categories = categoryRepository.findAll();

        //then
        assertThat(categories.size()).isEqualTo(2);
    }
}