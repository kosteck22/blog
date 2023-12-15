package com.example.blog.category;

import com.example.blog.entity.Category;
import com.example.blog.exception.DuplicateResourceException;
import com.example.blog.exception.RequestValidationException;
import com.example.blog.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    private CategoryService underTest;

    @BeforeEach
    public void setUp() {
        underTest = new CategoryService(categoryRepository);
    }

    @Test
    public void test_get_categories_as_page_should_return_page_of_categories() {
        //given
        Page<Category> mockPage = mock(Page.class);
        when(categoryRepository.findAll(any(Pageable.class))).thenReturn(mockPage);

        //when
        Page<Category> result = underTest.getCategoriesAsPage(Pageable.unpaged());

        //then
        verify(categoryRepository).findAll(any(Pageable.class));
        assertThat(result).isSameAs(mockPage);
    }
    
    @Test
    public void test_get_category_with_valid_id_should_return_category() {
        //given
        Long categoryId = 1L;
        Category mockCategory = mock(Category.class);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(mockCategory));
        
        //when
        Category result = underTest.get(categoryId);
        
        //then
        verify(categoryRepository).findById(categoryId);
        assertThat(result).isSameAs(mockCategory);
    }

    @Test
    public void test_get_category_with_invalid_id_should_throw_resource_not_found_exception() {
        //given
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> underTest.get(categoryId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Category with id [%d] not found"
                .formatted(categoryId));
    }
    
    @Test
    public void test_save_category_with_valid_data_should_success() {
        //given
        String categoryName = "Category name";
        CategoryRequest request = CategoryRequest.builder()
                .name(categoryName).build();
        Category category = mock(Category.class);
        when(categoryRepository.existsByName(categoryName)).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        //when
        Category result = underTest.save(request);

        //then
        ArgumentCaptor<Category> categoryArgumentCaptor = ArgumentCaptor.forClass(Category.class);

        verify(categoryRepository).save(categoryArgumentCaptor.capture());
        Category capturedCategory = categoryArgumentCaptor.getValue();

        assertThat(capturedCategory.getId()).isNull();
        assertThat(capturedCategory.getName()).isEqualTo(categoryName);
        assertThat(result).isSameAs(category);
    }

    @Test
    public void test_save_category_should_throw_duplicate_resource_exception() {
        //given
        String categoryName = "Category name";
        CategoryRequest request = mock(CategoryRequest.class);
        when(request.getName()).thenReturn(categoryName);
        when(categoryRepository.existsByName(categoryName)).thenReturn(true);

        //when
        //then
        assertThatThrownBy(() -> underTest.save(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Category with name [%s] already exists".formatted(categoryName));
        verify(categoryRepository, never()).save(any());
    }

    @Test
    public void test_update_category_with_valid_data_should_success() {
        //given
        Long categoryId = 1L;
        String categoryName = "Category name";
        CategoryRequest request = CategoryRequest.builder()
                .name(categoryName).build();
        Category category = Category.builder()
                .id(categoryId)
                .name("Old name").build();
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        //when
        underTest.update(categoryId, request);

        //then
        ArgumentCaptor<Category> categoryArgumentCaptor = ArgumentCaptor.forClass(Category.class);

        verify(categoryRepository).save(categoryArgumentCaptor.capture());
        Category capturedCategory = categoryArgumentCaptor.getValue();

        assertThat(capturedCategory.getId()).isEqualTo(categoryId);
        assertThat(capturedCategory.getName()).isEqualTo(categoryName);
    }

    @Test
    public void test_update_category_should_throw_duplicate_resource_exception() {
        //given
        Long categoryId = 1L;
        String categoryName = "Category name";
        CategoryRequest request = CategoryRequest.builder()
                .name(categoryName).build();
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());


        //when
        //then
        assertThatThrownBy(() -> underTest.update(categoryId, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Category with id [%d] not found"
                        .formatted(categoryId));
        verify(categoryRepository, never()).save(any());
    }

    @Test
    public void test_update_category_should_throw_request_validation_exception_because_not_unique_name() {
        //given
        Long categoryId = 1L;
        String categoryName = "New category name";
        CategoryRequest request = CategoryRequest.builder()
                .name(categoryName).build();
        Category mockCategory = Category.builder()
                .id(2L)
                .name(categoryName).build();
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(mock(Category.class)));
        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.of(mockCategory));


        //when
        //then
        assertThatThrownBy(() -> underTest.update(categoryId, request))
                .isInstanceOf(RequestValidationException.class);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    public void test_delete_category_should_success() {
        //given
        Long categoryId = 1L;
        Category mockCategory = mock(Category.class);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(mockCategory));

        //when
        underTest.delete(categoryId);

        //then
        verify(categoryRepository).delete(mockCategory);
    }

    @Test
    public void test_delete_category_should_throw_resource_not_found_exception() {
        //given
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        //when
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> underTest.delete(categoryId))
                .withMessage("Category with id [%d] not found", categoryId);

        //then
        verify(categoryRepository, never()).delete(any(Category.class));
    }
}