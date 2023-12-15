package com.example.blog.category;

import com.example.blog.entity.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CategoryMapperTest {
    private CategoryMapper underTest;

    @BeforeEach
    public void setUp() {
        underTest = new CategoryMapper();
    }

    @Test
    public void test_apply_success() {
        //given
        Category category = Category.builder()
                .id(1L)
                .name("Backend Dev").build();

        //when
        CategoryResponse result = underTest.apply(category);

        //then
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(CategoryResponse.class);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Backend Dev");
        assertThat(result.getLinks()).isEmpty();
    }
}