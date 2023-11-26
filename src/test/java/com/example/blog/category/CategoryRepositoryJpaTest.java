package com.example.blog.category;

import com.example.blog.entity.Category;
import com.example.blog.entity.Post;
import com.example.blog.post.PostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CategoryRepositoryJpaTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PostRepository postRepository;

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
    void test_delete_category_should_cascade_delete_posts() {
        // Given
        Category category = Category.builder()
                .name("Test Category")
                .build();
        Post post1 = Post.builder()
                .title("Post 1")
                .body("Body of Post 1")
                .category(category)
                .build();
        Post post2 = Post.builder()
                .title("Post 2")
                .body("Body of Post 2")
                .category(category)
                .build();
        category.addPost(post1);
        category.addPost(post2);

        categoryRepository.save(category);
        postRepository.save(post1);
        postRepository.save(post2);

        // When
        categoryRepository.delete(category);

        // Then
        Optional<Category> deletedCategory = categoryRepository.findById(category.getId());
        assertThat(deletedCategory).isEmpty();

        Optional<Post> deletedPost1 = postRepository.findById(post1.getId());
        assertThat(deletedPost1).isEmpty();

        Optional<Post> deletedPost2 = postRepository.findById(post2.getId());
        assertThat(deletedPost2).isEmpty();
    }

    @Test
    void delete_category_should_not_delete_unrelated_posts() {
        // Given
        Category category1 = Category.builder()
                .name("Category 1")
                .build();
        Category category2 = Category.builder()
                .name("Category 2")
                .build();
        Post post1 = Post.builder()
                .title("Post 1")
                .body("Body of Post 1")
                .category(category1)
                .build();
        Post post2 = Post.builder()
                .title("Post 2")
                .body("Body of Post 2")
                .category(category2)
                .build();

        category1.addPost(post1);
        category2.addPost(post2);

        categoryRepository.saveAll(Arrays.asList(category1, category2));
        postRepository.saveAll(Arrays.asList(post1, post2));

        // When
        categoryRepository.delete(category1);

        // Then
        Optional<Category> deletedCategory = categoryRepository.findById(category1.getId());
        assertThat(deletedCategory).isEmpty();

        Optional<Post> post1AfterDeletion = postRepository.findById(post1.getId());
        assertThat(post1AfterDeletion).isEmpty();

        Optional<Post> post2AfterDeletion = postRepository.findById(post2.getId());
        assertThat(post2AfterDeletion).isPresent(); // Post from another category should not be deleted
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
