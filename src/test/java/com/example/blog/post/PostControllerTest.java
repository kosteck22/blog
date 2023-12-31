package com.example.blog.post;

import com.example.blog.TestConfig;
import com.example.blog.category.CategoryMapper;
import com.example.blog.entity.Category;
import com.example.blog.entity.Post;
import com.example.blog.entity.Tag;
import com.example.blog.entity.User;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.security.JwtAuthenticationTokenFilter;
import com.example.blog.security.UserPrincipal;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.doThrow;


@WebMvcTest(PostController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({ PostModelAssembler.class, PostMapper.class, DetailedPostModelAssembler.class, CategoryMapper.class})
class PostControllerTest {
    private static final String END_POINT_PATH = "/api/v1/posts";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @MockBean
    private JwtAuthenticationTokenFilter filter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void test_save_post_should_return_201_created() throws Exception {
        //given
        String title = "This is title";
        String body = "This is body";
        List<String> tagNames = List.of("tag1", "tag2");
        Set<Tag> tags = Set.of(
                Tag.builder().id(1L).name("tag1").build(),
                Tag.builder().id(2L).name("tag2").build());
        Category category = Category.builder()
                .id(1L)
                .name("Category name").build();
        User user = User.builder().id(1L).build();
        PostRequest request = PostRequest.builder()
                .title(title)
                .body(body)
                .categoryId(1L)
                .tags(tagNames).build();
        Post post = Post.builder()
                .id(1L)
                .title(title)
                .body(body)
                .category(category)
                .user(user)
                .tags(tags).build();
        String requestBody = objectMapper.writeValueAsString(request);
        when(postService.save(request, null)).thenReturn(post);

        //when
        //then
        mockMvc.perform(post(END_POINT_PATH).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is(title)))
                .andExpect(jsonPath("$.body", is(body)))
                .andExpect(jsonPath("$.category.id", is(1)))
                .andExpect(jsonPath("$.category.name", is("Category name")))
                .andExpect(jsonPath("$.category._links.posts_for_category.href", is("http://localhost/api/v1/posts/category/1")))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/api/v1/posts/1")))
                .andExpect(jsonPath("$._links.tags.href", is("http://localhost/api/v1/tags/post/1")))
                .andExpect(jsonPath("$._links.comments.href", is("http://localhost/api/v1/posts/1/comments")))
                .andExpect(jsonPath("$._links.category.href", is("http://localhost/api/v1/categories/1")))
                .andExpect(jsonPath("$._links.user.href", is("http://localhost/api/v1/users/1")))
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
        MvcResult result = mockMvc.perform(post(END_POINT_PATH).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();

        assertThat(responseBody).contains("title size must be between 5 and 64");
        assertThat(responseBody).contains("body size must be between 10 and 1024");
        assertThat(responseBody).contains("tags: must not be null");
        assertThat(responseBody).contains("categoryId: must not be null");
    }

    @Test
    public void test_get_posts_as_page_should_return_200() throws Exception {
        //given
        Set<Tag> tags = Set.of(
                Tag.builder().id(1L).name("tag1").build(),
                Tag.builder().id(2L).name("tag2").build());
        Category category = Category.builder()
                .id(1L)
                .name("Category name").build();
        User user = User.builder().id(1L).build();
        Post firstPost = Post.builder()
                .id(1L)
                .title("title 1")
                .user(user)
                .tags(tags)
                .category(category)
                .body("body of the post 1").build();
        Post secondPost = Post.builder()
                .id(2L)
                .title("title 2")
                .user(user)
                .tags(tags)
                .category(category)
                .body("body of the post 2").build();
        Post thirdPost = Post.builder()
                .id(3L)
                .title("title 3")
                .user(user)
                .tags(tags)
                .category(category)
                .body("body of the post 3").build();
        Post fourthPost = Post.builder()
                .id(4L)
                .title("title 4")
                .user(user)
                .tags(tags)
                .category(category)
                .body("body of the post 4").build();
        List<Post> posts = List.of(firstPost, secondPost, thirdPost, fourthPost);
        Pageable pageable = PageRequest.of(0, 2);
        Page<Post> postPage = new PageImpl<>(posts, pageable, posts.size());

        when(postService.getPostsAsPage(pageable)).thenReturn(postPage);

        //when
        //then
        mockMvc.perform(get(END_POINT_PATH).contentType(MediaType.APPLICATION_JSON)
                        .param("size", "2").param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$._embedded.posts[0].id", is(1)))
                .andExpect(jsonPath("$._embedded.posts[0].title", is("title 1")))
                .andExpect(jsonPath("$._embedded.posts[0].body", is("body of the post 1")))
                .andExpect(jsonPath("$._embedded.posts[0].category.name", is("Category name")))
                .andExpect(jsonPath("$._embedded.posts[0]._links.self.href", is("http://localhost/api/v1/posts/1")))
                .andExpect(jsonPath("$._embedded.posts[1].id", is(2)))
                .andExpect(jsonPath("$._embedded.posts[1].title", is("title 2")))
                .andExpect(jsonPath("$._embedded.posts[1].body", is("body of the post 2")))
                .andExpect(jsonPath("$._embedded.posts[1].category.id", is(1)))
                .andExpect(jsonPath("$._embedded.posts[1]._links.self.href", is("http://localhost/api/v1/posts/2")))
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
    public void test_get_posts_as_page_should_return_empty_collection_204() throws Exception {
        //given
        Page<Post> mockedPage = mock(Page.class);
        when(mockedPage.isEmpty()).thenReturn(true);

        when(postService.getPostsAsPage(any())).thenReturn(mockedPage);

        //when
        //then
        MvcResult mvcResult = mockMvc.perform(get(END_POINT_PATH).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andDo(print())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();

        assertThat(responseBody).doesNotContain("_embedded");
    }

    @Test
    public void test_get_post_by_id_should_return_200() throws Exception {
        //given
        Set<Tag> tags = Set.of(
                Tag.builder().id(1L).name("tag1").build(),
                Tag.builder().id(2L).name("tag2").build());
        Category category = Category.builder()
                .id(1L)
                .name("Category name").build();
        User user = User.builder().id(1L).build();
        Long id = 1L;
        Post post = Post.builder()
                .id(id)
                .title("title 1")
                .body("body of the post 1")
                .category(category)
                .tags(tags)
                .user(user).build();

        when(postService.getPostById(id)).thenReturn(post);

        //when
        //then
        mockMvc.perform(get(END_POINT_PATH + "/" + id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is(post.getTitle())))
                .andExpect(jsonPath("$.body", is(post.getBody())))
                .andExpect(jsonPath("$.category.id", is(1)))
                .andExpect(jsonPath("$.category.name", is("Category name")))
                .andExpect(jsonPath("$.category._links.posts_for_category.href", is("http://localhost/api/v1/posts/category/1")))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/api/v1/posts/1")))
                .andExpect(jsonPath("$._links.tags.href", is("http://localhost/api/v1/tags/post/1")))
                .andExpect(jsonPath("$._links.comments.href", is("http://localhost/api/v1/posts/1/comments")))
                .andExpect(jsonPath("$._links.category.href", is("http://localhost/api/v1/categories/1")))
                .andExpect(jsonPath("$._links.user.href", is("http://localhost/api/v1/users/1")))
                .andDo(print());
    }

    @Test
    public void test_get_post_by_id_should_return_404_not_found() throws Exception {
        //given
        Long id = 1L;

        when(postService.getPostById(id)).thenThrow(new ResourceNotFoundException("Post with id [%d] does not exist".formatted(id)));

        //when
        //then
        mockMvc.perform(get(END_POINT_PATH + "/" + id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors[0]", is("Post with id [%d] does not exist".formatted(id))))
                .andDo(print());
    }

    @Test
    public void test_delete_post_should_return_204_success() throws Exception {
        //given
        long id = 1L;

        //when
        mockMvc.perform(delete(END_POINT_PATH + "/" + id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());

        verify(postService, times(1)).delete(anyLong(), any());
    }

    @Test
    public void test_delete_post_should_return_404_not_found() throws Exception {
        //given
        Long id = 1L;
        doThrow(ResourceNotFoundException.class).when(postService).delete(id, null);

        //when
        mockMvc.perform(delete(END_POINT_PATH + "/" + id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void test_update_post_should_return_200() throws Exception {
        //given
        Long id = 1L;
        Set<Tag> tags = Set.of(
                Tag.builder().id(1L).name("tag1").build(),
                Tag.builder().id(2L).name("tag2").build());
        Category category = Category.builder()
                .id(1L)
                .name("Category name").build();
        User user = User.builder().id(1L).build();
        String title = "This is updated title";
        String body = "This is updated body";
        PostRequest request = PostRequest.builder()
                .title(title)
                .body(body)
                .categoryId(1L)
                .tags(List.of("tag1", "tag2")).build();
        Post post = Post.builder()
                .id(1L)
                .title(title)
                .body(body)
                .category(category)
                .tags(tags)
                .user(user).build();
        String requestBody = objectMapper.writeValueAsString(request);
        when(postService.update(id, request, null)).thenReturn(post);

        //when
        //then
        mockMvc.perform(put(END_POINT_PATH + "/" + id).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is(title)))
                .andExpect(jsonPath("$.body", is(body)))
                .andExpect(jsonPath("$.category.id", is(1)))
                .andExpect(jsonPath("$.category.name", is("Category name")))
                .andExpect(jsonPath("$.category._links.posts_for_category.href", is("http://localhost/api/v1/posts/category/1")))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/api/v1/posts/1")))
                .andExpect(jsonPath("$._links.tags.href", is("http://localhost/api/v1/tags/post/1")))
                .andExpect(jsonPath("$._links.comments.href", is("http://localhost/api/v1/posts/1/comments")))
                .andExpect(jsonPath("$._links.category.href", is("http://localhost/api/v1/categories/1")))
                .andExpect(jsonPath("$._links.user.href", is("http://localhost/api/v1/users/1")))
                .andDo(print());
    }

    @Test
    public void test_update_post_should_return_404_not_found() throws Exception {
        //given
        long id = 1L;
        long categoryId = 1L;
        List<String> tags = List.of("tag1", "tag2");
        String title = "This is updated title";
        String body = "This is updated body";
        PostRequest request = PostRequest.builder()
                .title(title)
                .body(body)
                .categoryId(categoryId)
                .tags(tags).build();
        String requestBody = objectMapper.writeValueAsString(request);

        when(postService.update(id, request, null)).thenThrow(new ResourceNotFoundException("Post with id [%d] does not exist".formatted(id)));

        //when
        //then
        mockMvc.perform(put(END_POINT_PATH + "/" + id).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors[0]", is("Post with id [1] does not exist")))
                .andDo(print());
    }

    @Test
    public void test_update_post_should_return_400_bad_request() throws Exception {
        //given
        long id = 1L;
        String title = "T";
        String body = "T";
        PostRequest request = PostRequest.builder()
                .title(title)
                .body(body).build();
        String requestBody = objectMapper.writeValueAsString(request);

        //when
        //then
        MvcResult mvcResult = mockMvc.perform(put(END_POINT_PATH + "/" + id).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertThat(response).contains("title size must be between 5 and 64");
        assertThat(response).contains("body size must be between 10 and 1024");
        assertThat(response).contains("tags: must not be null");
        assertThat(response).contains("categoryId: must not be null");
    }

    @Test
    public void test_get_posts_by_category_should_return_200() throws Exception {
        //given
        long categoryId = 1L;
        Set<Tag> tags = Set.of(
                Tag.builder().id(1L).name("tag1").build(),
                Tag.builder().id(2L).name("tag2").build());
        Category category = Category.builder()
                .id(categoryId)
                .name("Category name").build();
        User user = User.builder().id(1L).build();
        Post firstPost = Post.builder()
                .id(1L)
                .title("title 1")
                .user(user)
                .tags(tags)
                .category(category)
                .body("body of the post 1").build();
        Post secondPost = Post.builder()
                .id(2L)
                .title("title 2")
                .user(user)
                .tags(tags)
                .category(category)
                .body("body of the post 2").build();
        Post thirdPost = Post.builder()
                .id(3L)
                .title("title 3")
                .user(user)
                .tags(tags)
                .category(category)
                .body("body of the post 3").build();
        Post fourthPost = Post.builder()
                .id(4L)
                .title("title 4")
                .user(user)
                .tags(tags)
                .category(category)
                .body("body of the post 4").build();
        List<Post> posts = List.of(firstPost, secondPost, thirdPost, fourthPost);
        Pageable pageable = PageRequest.of(0, 2);
        Page<Post> postPage = new PageImpl<>(posts, pageable, posts.size());

        when(postService.getPostsByCategoryId(categoryId, pageable)).thenReturn(postPage);

        //when
        //then
        mockMvc.perform(get(END_POINT_PATH + "/category/" + categoryId).contentType(MediaType.APPLICATION_JSON)
                        .param("size", "2").param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$._embedded.posts[0].id", is(1)))
                .andExpect(jsonPath("$._embedded.posts[0].title", is("title 1")))
                .andExpect(jsonPath("$._embedded.posts[0].body", is("body of the post 1")))
                .andExpect(jsonPath("$._embedded.posts[0].category.name", is("Category name")))
                .andExpect(jsonPath("$._embedded.posts[0]._links.self.href", is("http://localhost/api/v1/posts/1")))
                .andExpect(jsonPath("$._embedded.posts[1].id", is(2)))
                .andExpect(jsonPath("$._embedded.posts[1].title", is("title 2")))
                .andExpect(jsonPath("$._embedded.posts[1].body", is("body of the post 2")))
                .andExpect(jsonPath("$._embedded.posts[1].category.id", is(1)))
                .andExpect(jsonPath("$._embedded.posts[1]._links.self.href", is("http://localhost/api/v1/posts/2")))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/api/v1/posts/category/1?page=0&size=2")))
                .andExpect(jsonPath("$._links.first.href", is("http://localhost/api/v1/posts/category/1?page=0&size=2")))
                .andExpect(jsonPath("$._links.next.href", is("http://localhost/api/v1/posts/category/1?page=1&size=2")))
                .andExpect(jsonPath("$._links.last.href", is("http://localhost/api/v1/posts/category/1?page=1&size=2")))
                .andExpect(jsonPath("$.page.size", is(2)))
                .andExpect(jsonPath("$.page.totalElements", is(4)))
                .andExpect(jsonPath("$.page.totalPages", is(2)))
                .andExpect(jsonPath("$.page.number", is(0)))
                .andDo(print());
    }

    @Test
    public void test_get_posts_by_category_should_return_empty_collection_204() throws Exception {
        //given
        Long categoryId = 1L;
        Page<Post> mockedPage = mock(Page.class);
        when(mockedPage.isEmpty()).thenReturn(true);

        when(postService.getPostsByCategoryId(any(Long.class), any(Pageable.class))).thenReturn(mockedPage);

        //when
        //then
        MvcResult mvcResult = mockMvc.perform(get(END_POINT_PATH+ "/category/" + categoryId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andDo(print())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();

        assertThat(responseBody).doesNotContain("_embedded");
    }

    @Test
    public void test_get_posts_by_tag_should_return_200() throws Exception {
        //given
        long tagId = 1L;
        Set<Tag> tags = Set.of(
                Tag.builder().id(1L).name("tag1").build(),
                Tag.builder().id(2L).name("tag2").build());
        Category category = Category.builder()
                .id(1L)
                .name("Category name").build();
        User user = User.builder().id(1L).build();
        Post firstPost = Post.builder()
                .id(1L)
                .title("title 1")
                .user(user)
                .tags(tags)
                .category(category)
                .body("body of the post 1").build();
        Post secondPost = Post.builder()
                .id(2L)
                .title("title 2")
                .user(user)
                .tags(tags)
                .category(category)
                .body("body of the post 2").build();
        Post thirdPost = Post.builder()
                .id(3L)
                .title("title 3")
                .user(user)
                .tags(tags)
                .category(category)
                .body("body of the post 3").build();
        Post fourthPost = Post.builder()
                .id(4L)
                .title("title 4")
                .user(user)
                .tags(tags)
                .category(category)
                .body("body of the post 4").build();
        List<Post> posts = List.of(firstPost, secondPost, thirdPost, fourthPost);
        Pageable pageable = PageRequest.of(0, 2);
        Page<Post> postPage = new PageImpl<>(posts, pageable, posts.size());

        when(postService.getPostsByTagId(tagId, pageable)).thenReturn(postPage);

        //when
        //then
        mockMvc.perform(get(END_POINT_PATH + "/tag/" + tagId).contentType(MediaType.APPLICATION_JSON)
                        .param("size", "2").param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$._embedded.posts[0].id", is(1)))
                .andExpect(jsonPath("$._embedded.posts[0].title", is("title 1")))
                .andExpect(jsonPath("$._embedded.posts[0].body", is("body of the post 1")))
                .andExpect(jsonPath("$._embedded.posts[0].category.name", is("Category name")))
                .andExpect(jsonPath("$._embedded.posts[0]._links.self.href", is("http://localhost/api/v1/posts/1")))
                .andExpect(jsonPath("$._embedded.posts[1].id", is(2)))
                .andExpect(jsonPath("$._embedded.posts[1].title", is("title 2")))
                .andExpect(jsonPath("$._embedded.posts[1].body", is("body of the post 2")))
                .andExpect(jsonPath("$._embedded.posts[1].category.id", is(1)))
                .andExpect(jsonPath("$._embedded.posts[1]._links.self.href", is("http://localhost/api/v1/posts/2")))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/api/v1/posts/tag/1?page=0&size=2")))
                .andExpect(jsonPath("$._links.first.href", is("http://localhost/api/v1/posts/tag/1?page=0&size=2")))
                .andExpect(jsonPath("$._links.next.href", is("http://localhost/api/v1/posts/tag/1?page=1&size=2")))
                .andExpect(jsonPath("$._links.last.href", is("http://localhost/api/v1/posts/tag/1?page=1&size=2")))
                .andExpect(jsonPath("$.page.size", is(2)))
                .andExpect(jsonPath("$.page.totalElements", is(4)))
                .andExpect(jsonPath("$.page.totalPages", is(2)))
                .andExpect(jsonPath("$.page.number", is(0)))
                .andDo(print());
    }

    @Test
    public void test_get_posts_by_tag_should_return_empty_collection_204() throws Exception {
        //given
        Long tagId = 1L;
        Page<Post> mockedPage = mock(Page.class);
        when(mockedPage.isEmpty()).thenReturn(true);

        when(postService.getPostsByTagId(any(Long.class), any(Pageable.class))).thenReturn(mockedPage);

        //when
        //then
        MvcResult mvcResult = mockMvc.perform(get(END_POINT_PATH+ "/tag/" + tagId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andDo(print())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();

        assertThat(responseBody).doesNotContain("_embedded");
    }

    @Test
    public void test_get_posts_by_user_should_return_200() throws Exception {
        //given
        long userId = 1L;
        Set<Tag> tags = Set.of(
                Tag.builder().id(1L).name("tag1").build(),
                Tag.builder().id(2L).name("tag2").build());
        Category category = Category.builder()
                .id(1L)
                .name("Category name").build();
        User user = User.builder().id(1L).build();
        Post firstPost = Post.builder()
                .id(1L)
                .title("title 1")
                .user(user)
                .tags(tags)
                .category(category)
                .body("body of the post 1").build();
        Post secondPost = Post.builder()
                .id(2L)
                .title("title 2")
                .user(user)
                .tags(tags)
                .category(category)
                .body("body of the post 2").build();
        Post thirdPost = Post.builder()
                .id(3L)
                .title("title 3")
                .user(user)
                .tags(tags)
                .category(category)
                .body("body of the post 3").build();
        Post fourthPost = Post.builder()
                .id(4L)
                .title("title 4")
                .user(user)
                .tags(tags)
                .category(category)
                .body("body of the post 4").build();
        List<Post> posts = List.of(firstPost, secondPost, thirdPost, fourthPost);
        Pageable pageable = PageRequest.of(0, 2);
        Page<Post> postPage = new PageImpl<>(posts, pageable, posts.size());

        when(postService.getPostsByUserId(userId, pageable)).thenReturn(postPage);

        //when
        //then
        mockMvc.perform(get(END_POINT_PATH + "/user/" + userId).contentType(MediaType.APPLICATION_JSON)
                        .param("size", "2").param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$._embedded.posts[0].id", is(1)))
                .andExpect(jsonPath("$._embedded.posts[0].title", is("title 1")))
                .andExpect(jsonPath("$._embedded.posts[0].body", is("body of the post 1")))
                .andExpect(jsonPath("$._embedded.posts[0].category.name", is("Category name")))
                .andExpect(jsonPath("$._embedded.posts[0]._links.self.href", is("http://localhost/api/v1/posts/1")))
                .andExpect(jsonPath("$._embedded.posts[1].id", is(2)))
                .andExpect(jsonPath("$._embedded.posts[1].title", is("title 2")))
                .andExpect(jsonPath("$._embedded.posts[1].body", is("body of the post 2")))
                .andExpect(jsonPath("$._embedded.posts[1].category.id", is(1)))
                .andExpect(jsonPath("$._embedded.posts[1]._links.self.href", is("http://localhost/api/v1/posts/2")))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/api/v1/posts/user/1?page=0&size=2")))
                .andExpect(jsonPath("$._links.first.href", is("http://localhost/api/v1/posts/user/1?page=0&size=2")))
                .andExpect(jsonPath("$._links.next.href", is("http://localhost/api/v1/posts/user/1?page=1&size=2")))
                .andExpect(jsonPath("$._links.last.href", is("http://localhost/api/v1/posts/user/1?page=1&size=2")))
                .andExpect(jsonPath("$.page.size", is(2)))
                .andExpect(jsonPath("$.page.totalElements", is(4)))
                .andExpect(jsonPath("$.page.totalPages", is(2)))
                .andExpect(jsonPath("$.page.number", is(0)))
                .andDo(print());
    }

    @Test
    public void test_get_posts_by_user_should_return_empty_collection_204() throws Exception {
        //given
        Long userId = 1L;
        Page<Post> mockedPage = mock(Page.class);
        when(mockedPage.isEmpty()).thenReturn(true);

        when(postService.getPostsByUserId(any(Long.class), any(Pageable.class))).thenReturn(mockedPage);

        //when
        //then
        MvcResult mvcResult = mockMvc.perform(get(END_POINT_PATH+ "/user/" + userId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andDo(print())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();

        assertThat(responseBody).doesNotContain("_embedded");
    }
}