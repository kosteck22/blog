package com.example.blog.category;

import com.example.blog.entity.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
class CategoryModelAssemblerTest {

    private CategoryModelAssembler underTest;

    @BeforeEach
    public void setUp() {
        underTest = new CategoryModelAssembler();
    }

    @Test
    public void test_to_model_should_success() {
        //given
        Category category = Category.builder()
                .id(1L)
                .name("Backend Dev").build();

        //when
        CategoryResponse result = underTest.toModel(category);

        //then
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(CategoryResponse.class);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo(category.getName());
        assertThat(result.getLinks()).isNotEmpty();
        assertThat(result.getLinks().hasSize(2)).isTrue();
        assertThat(result.getLink("self")).isPresent();
        assertThat(result.getLink("posts")).isPresent();
    }
}