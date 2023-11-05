package com.example.blog.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(PostController.class)
@Import({ PostModelAssembler.class, PostMapper.class })
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

    @Test
    public void test_fetch_posts_data_should_return_200() throws Exception {
        //given
        Post firstPost = Post.builder()
                .id(1L)
                .title("title 1")
                .body("body of the post 1").build();
        Post secondPost = Post.builder()
                .id(2L)
                .title("title 2")
                .body("body of the post 2").build();
        Post thirdPost = Post.builder()
                .id(3L)
                .title("title 3")
                .body("body of the post 3").build();
        Post fourthPost = Post.builder()
                .id(4L)
                .title("title 4")
                .body("body of the post 4").build();
        List<Post> posts = List.of(firstPost, secondPost, thirdPost, fourthPost);
        Pageable pageable = PageRequest.of(0, 2);
        Page<Post> postPage = new PageImpl<>(posts, pageable, posts.size());

        when(postService.fetchPostDataAsPage(any())).thenReturn(postPage);

        //when
        //then
        mockMvc.perform((get(END_POINT_PATH).contentType(MediaTypes.HAL_JSON)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$._embedded.postModelList[0].id", is(1)))
                .andExpect(jsonPath("$._embedded.postModelList[0].title", is("title 1")))
                .andExpect(jsonPath("$._embedded.postModelList[0].body", is("body of the post 1")))
                .andExpect(jsonPath("$._embedded.postModelList[0]._links.self.href", is("http://localhost/api/v1/posts/1")))
                .andExpect(jsonPath("$._embedded.postModelList[1].id", is(2)))
                .andExpect(jsonPath("$._embedded.postModelList[1].title", is("title 2")))
                .andExpect(jsonPath("$._embedded.postModelList[1].body", is("body of the post 2")))
                .andExpect(jsonPath("$._embedded.postModelList[1]._links.self.href", is("http://localhost/api/v1/posts/2")))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/api/v1/posts?page=0&size=2")))
                .andExpect(jsonPath("$._links.first.href", is("http://localhost/api/v1/posts?page=0&size=2")))
                .andExpect(jsonPath("$._links.next.href", is("http://localhost/api/v1/posts?page=1&size=2")))
                .andExpect(jsonPath("$._links.last.href", is("http://localhost/api/v1/posts?page=1&size=2")))
                .andExpect(jsonPath("$.page.size", is(2)))
                .andExpect(jsonPath("$.page.totalElements", is(4)))
                .andExpect(jsonPath("$.page.totalPages", is(2)))
                .andExpect(jsonPath("$.page.number", is(0)))
                .andDo(print());
    }

    @Test
    public void test_fetch_posts_data_should_return_empty_collection_204() throws Exception {
        //given
        List<Post> posts = List.of();
        Pageable pageable = PageRequest.of(0, 2);
        Page<Post> postPage = new PageImpl<>(posts, pageable, posts.size());

        when(postService.fetchPostDataAsPage(any())).thenReturn(postPage);

        //when
        //then
        MvcResult mvcResult = mockMvc.perform((get(END_POINT_PATH).contentType(MediaTypes.HAL_JSON)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/api/v1/posts?page=0&size=2")))
                .andExpect(jsonPath("$.page.size", is(2)))
                .andExpect(jsonPath("$.page.totalElements", is(0)))
                .andExpect(jsonPath("$.page.totalPages", is(0)))
                .andExpect(jsonPath("$.page.number", is(0)))
                .andDo(print())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();

        assertThat(responseBody).doesNotContain("_embedded");
    }
}