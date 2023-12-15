package com.example.blog.category;

import com.example.blog.entity.Category;
import com.example.blog.security.JwtAuthenticationTokenFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {
    private static final String END_POINT_PATH = "/api/v1/categories";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private CategoryModelAssembler assembler;

    @MockBean
    private JwtAuthenticationTokenFilter filter;

    @MockBean
    private PagedResourcesAssembler<Category> pagedResourcesAssembler;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void test_get_categories_should_return_204_no_content() throws Exception {
        //given
        List<Category> categories = Collections.emptyList();
        Pageable pageable = PageRequest.of(0, 2);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, 0);
        when(categoryService.getCategoriesAsPage(any())).thenReturn(categoryPage);

        //when
        //then
        mockMvc.perform(get(END_POINT_PATH).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    public void test_get_categories_should_return_200_ok() throws Exception {
        //given
        Category category1 = Category.builder()
                .id(1L)
                .name("Cat 1").build();
        Category category2 = Category.builder()
                .id(2L)
                .name("Cat 2").build();
        List<Category> categories = List.of(category1, category2);
        Pageable pageable = PageRequest.of(0, 5);

        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());
        when(categoryService.getCategoriesAsPage(any())).thenReturn(categoryPage);
        when(pagedResourcesAssembler.toModel(categoryPage, assembler)).thenReturn(null);

        //when
        //then
        mockMvc.perform(get(END_POINT_PATH).param("page", "0").param("size", "5"))
                .andExpect(status().isOk())
                .andDo(print());

        verify(pagedResourcesAssembler, times(1)).toModel(categoryPage, assembler);
    }

    @Test
    public void test_get_category_by_id_should_return_200_ok() throws Exception {
        //given
        Long categoryId = 1L;
        Category category = mock(Category.class);
        when(categoryService.get(categoryId)).thenReturn(category);

        //when
        //then
        mockMvc.perform(get(END_POINT_PATH + "/" + categoryId))
                .andExpect(status().isOk())
                .andDo(print());

        verify(assembler, times(1)).toModel(category);
    }

    @Test
    public void test_save_category_should_throw_400_bad_request_name_cannot_be_null() throws Exception {
        //given
        CategoryRequest categoryRequest = CategoryRequest.builder()
                .name(null).build();
        String requestBody = objectMapper.writeValueAsString(categoryRequest);

        //when
        //then
        MvcResult mvcResult = mockMvc.perform(post(END_POINT_PATH).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();

        assertThat(responseBody).contains("name: must not be null");
    }

    @Test
    public void test_save_category_should_throw_400_bad_request_name_size_incorrect() throws Exception {
        //given
        CategoryRequest categoryRequest = CategoryRequest.builder()
                .name("zx").build();
        String requestBody = objectMapper.writeValueAsString(categoryRequest);

        //when
        //then
        MvcResult mvcResult = mockMvc.perform(post(END_POINT_PATH).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();

        assertThat(responseBody).contains("Name size must be between 3 and 32");
    }

    @Test
    public void test_save_category_should_return_201_created() throws Exception {
        //given
        CategoryRequest categoryRequest = CategoryRequest.builder()
                .name("Backend Dev").build();
        Category savedCategory = mock(Category.class);
        String requestBody = objectMapper.writeValueAsString(categoryRequest);
        when(categoryService.save(any(CategoryRequest.class))).thenReturn(savedCategory);

        //when
        //then
        mockMvc.perform(post(END_POINT_PATH).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated())
                .andDo(print());

        verify(assembler, times(1)).toModel(savedCategory);
    }

    @Test
    public void test_update_category_should_throw_400_bad_request_name_cannot_be_null() throws Exception {
        //given
        long categoryId = 1L;
        CategoryRequest categoryRequest = CategoryRequest.builder()
                .name(null).build();
        String requestBody = objectMapper.writeValueAsString(categoryRequest);

        //when
        //then
        MvcResult mvcResult = mockMvc.perform(put(END_POINT_PATH + "/" + categoryId).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();

        assertThat(responseBody).contains("name: must not be null");
    }

    @Test
    public void test_update_category_should_throw_400_bad_request_name_size_incorrect() throws Exception {
        //given
        long categoryId = 1L;
        CategoryRequest categoryRequest = CategoryRequest.builder()
                .name("zx").build();
        String requestBody = objectMapper.writeValueAsString(categoryRequest);

        //when
        //then
        MvcResult mvcResult = mockMvc.perform(put(END_POINT_PATH + "/" + categoryId).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();

        assertThat(responseBody).contains("Name size must be between 3 and 32");
    }

    @Test
    public void test_update_category_should_return_200_ok() throws Exception {
        //given
        long categoryId = 1L;
        CategoryRequest categoryRequest = CategoryRequest.builder()
                .name("Backend Dev").build();
        String requestBody = objectMapper.writeValueAsString(categoryRequest);
        Category category = mock(Category.class);
        when(categoryService.update(anyLong(), any(CategoryRequest.class))).thenReturn(category);

        //when
        //then
        mockMvc.perform(put(END_POINT_PATH + "/" + categoryId).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk())
                .andDo(print());

        verify(assembler, times(1)).toModel(category);
    }

    @Test
    public void test_delete_category_should_return_200_ok() throws Exception {
        //given
        long categoryId = 1L;

        //when
        //then
        mockMvc.perform(delete(END_POINT_PATH + "/" + categoryId))
                .andExpect(status().isNoContent())
                .andDo(print());

        verify(categoryService, times(1)).delete(categoryId);
    }
}