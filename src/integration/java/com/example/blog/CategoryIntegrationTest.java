package com.example.blog;

import com.example.blog.category.CategoryRepository;
import com.example.blog.category.CategoryRequest;
import com.example.blog.category.CategoryResponse;
import com.example.blog.entity.Category;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@Rollback
public class CategoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @MockBean
    private AuditorAware<Long> auditorAware;

    @BeforeEach
    public void mockAudit() {
        Mockito.when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of(2L));
    }


    @Test
    public void shouldGetCategoriesReturnEmptyPage() throws Exception {
        mockMvc.perform(get("/api/v1/categories").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @WithMockUser(username = "user", roles = "ADMIN")
    @Test
    public void shouldAddCategoryToDBThenGetIt() throws Exception {
        //given
        CategoryRequest request = CategoryRequest.builder()
                .name("Backend Development").build();
        String requestBody = objectMapper.writeValueAsString(request);

        //when
        //then
        //add new category
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/categories")
                        .content(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$.id", is(greaterThan(0))))
                .andExpect(jsonPath("$.name", is(request.getName())))
                .andExpect(jsonPath("$._links.posts.href", is("http://localhost/api/v1/posts/category/1")))
                .andDo(print())
                .andReturn();

        CategoryResponse response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), CategoryResponse.class);

        Long categoryId = response.getId();
        assertThat(categoryId).isEqualTo(1L);

        //get created category by id
        mockMvc.perform(get("/api/v1/categories/" + categoryId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$.id", is(greaterThan(0))))
                .andExpect(jsonPath("$.name", is(request.getName())))
                .andExpect(jsonPath("$._links.posts.href", is("http://localhost/api/v1/posts/category/1")))
                .andDo(print());

        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);

        assertThat(categoryOptional).isPresent();
        assertThat(categoryOptional.get().getCreatedBy()).isEqualTo(2L);
    }
}
