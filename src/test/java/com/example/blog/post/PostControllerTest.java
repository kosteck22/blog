package com.example.blog.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
class PostControllerTest {
    private static final String END_POINT_PATH = "/api/v1/posts";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void test_save_post_should_return_201_created() throws Exception {
        //given
        String title = "This is title";
        String body = "This is body";
        PostRequest request = PostRequest.builder()
                .title(title)
                .body(body).build();
        Post post = Post.builder()
                .id(1L)
                .title(title)
                .body(body)
                .build();
        String requestBody = objectMapper.writeValueAsString(request);
        when(postService.save(request)).thenReturn(post);

        //when
        //then
        mockMvc.perform((post(END_POINT_PATH).contentType(MediaType.APPLICATION_JSON).content(requestBody)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is(title)))
                .andExpect(jsonPath("$.body", is(body)))
                .andDo(print());
    }

    @Test
    public void test_save_post_should_return_400_bad_request_because_invalid_fields() throws Exception {
        //given
        String title = "abc";
        String body = "body";
        PostRequest request = PostRequest.builder()
                .title(title)
                .body(body).build();
        String requestBody = objectMapper.writeValueAsString(request);

        //when
        //then
        MvcResult result = mockMvc.perform((post(END_POINT_PATH).contentType(MediaType.APPLICATION_JSON).content(requestBody)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();

        assertThat(responseBody).contains("title size must be between 5 and 64");
        assertThat(responseBody).contains("body size must be between 10 and 1024");
    }
}