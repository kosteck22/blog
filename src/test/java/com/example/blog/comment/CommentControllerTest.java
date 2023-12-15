package com.example.blog.comment;

import com.example.blog.entity.Comment;
import com.example.blog.entity.User;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.entity.Post;
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

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(CommentController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({ CommentModelAssembler.class, CommentMapper.class })
class CommentControllerTest {

    private static final String END_POINT_PATH = "/api/v1/posts/%d/comments";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtAuthenticationTokenFilter filter;

    @Test
    public void test_get_comments_for_post_should_return_200() throws Exception {
        //given
        Long postId = 1L;
        Post post = Post.builder()
                .id(postId)
                .title("Title of post")
                .body("Body of post").build();
        User user = User.builder()
                .id(1L).build();
        Comment firstComment = Comment.builder()
                .id(1L)
                .body("body of the comment 1")
                .user(user)
                .post(post).build();
        Comment secondComment = Comment.builder()
                .id(2L)
                .body("body of the comment 2")
                .user(user)
                .post(post).build();
        Comment thirdComment = Comment.builder()
                .id(3L)
                .body("body of the comment 3")
                .user(user)
                .post(post).build();
        Comment fourthComment = Comment.builder()
                .id(4L)
                .body("body of the comment 4")
                .user(user)
                .post(post).build();
        List<Comment> comments = List.of(firstComment, secondComment, thirdComment, fourthComment);
        Pageable pageable = PageRequest.of(0, 2);
        Page<Comment> commentPage = new PageImpl<>(comments, pageable, comments.size());

        doReturn(commentPage).when(commentService).getCommentsAsPage(any(Long.class), any(Pageable.class));

        //when
        //then
        mockMvc.perform(get(END_POINT_PATH.formatted(postId)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$._embedded.comments[0].id", is(1)))
                .andExpect(jsonPath("$._embedded.comments[0].body", is("body of the comment 1")))
                .andExpect(jsonPath("$._embedded.comments[0]._links.post.href", is("http://localhost/api/v1/posts/1")))
                .andExpect(jsonPath("$._embedded.comments[0]._links.user.href", is("http://localhost/api/v1/users/1")))
                .andExpect(jsonPath("$._embedded.comments[1].id", is(2)))
                .andExpect(jsonPath("$._embedded.comments[1].body", is("body of the comment 2")))
                .andExpect(jsonPath("$._embedded.comments[1]._links.post.href", is("http://localhost/api/v1/posts/1")))
                .andExpect(jsonPath("$._embedded.comments[1]._links.user.href", is("http://localhost/api/v1/users/1")))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/api/v1/posts/1/comments?page=0&size=2")))
                .andExpect(jsonPath("$._links.first.href", is("http://localhost/api/v1/posts/1/comments?page=0&size=2")))
                .andExpect(jsonPath("$._links.next.href", is("http://localhost/api/v1/posts/1/comments?page=1&size=2")))
                .andExpect(jsonPath("$._links.last.href", is("http://localhost/api/v1/posts/1/comments?page=1&size=2")))
                .andExpect(jsonPath("$.page.size", is(2)))
                .andExpect(jsonPath("$.page.totalElements", is(4)))
                .andExpect(jsonPath("$.page.totalPages", is(2)))
                .andExpect(jsonPath("$.page.number", is(0)))
                .andDo(print());
    }

    @Test
    public void test_get_comments_for_post_should_return_204_empty_collection() throws Exception {
        //given
        Long postId = 1L;
        List<Comment> comments = List.of();
        Pageable pageable = PageRequest.of(0, 2);
        Page<Comment> commentPage = new PageImpl<>(comments, pageable, comments.size());

        when(commentService.getCommentsAsPage(any(), any())).thenReturn(commentPage);

        //when
        //then
        MvcResult mvcResult = mockMvc.perform(get(END_POINT_PATH.formatted(postId)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();

        assertThat(responseBody).doesNotContain("_embedded");
    }

    @Test
    public void test_get_comments_for_post_that_does_not_exist_should_return_404_not_found() throws Exception {
        //given
        Long postId = 1L;

        when(commentService.getCommentsAsPage(any(), any())).thenThrow(ResourceNotFoundException.class);

        //when
        //then
        mockMvc.perform(get(END_POINT_PATH.formatted(postId)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void test_save_comment_should_return_400_bad_request() throws Exception {
        //given
        Long postId = 1L;
        CommentRequest commentRequest = CommentRequest.builder()
                .body("ABC").build();
        String requestBody = objectMapper.writeValueAsString(commentRequest);

        //when
        MvcResult mvcResult = mockMvc.perform(post(END_POINT_PATH.formatted(postId)).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();

        //then
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(response).contains("body size must be between 10 and 1024");
    }

    @Test
    public void test_save_comment_should_return_404_resource_not_found() throws Exception {
        //given
        Long postId = 1L;
        CommentRequest commentRequest = CommentRequest.builder()
                .body("This is body of the new comment").build();
        String requestBody = objectMapper.writeValueAsString(commentRequest);

        when(commentService.save(postId, commentRequest, null)).thenThrow(ResourceNotFoundException.class);

        //when
        //then
        mockMvc.perform(post(END_POINT_PATH.formatted(postId)).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void test_save_comment_should_return_201_success() throws Exception {
        //given
        Long postId = 1L;
        String commentBody = "This is body of the new comment";
        LocalDateTime createdDate = LocalDateTime.of(2023, 10, 12, 12, 12, 12);
        Timestamp timestamp = Timestamp.valueOf(createdDate);

        CommentRequest commentRequest = CommentRequest.builder()
                .body(commentBody).build();

        User user = User.builder()
                .id(2L).build();

        Post post = Post.builder()
                .id(postId)
                .title("Post title")
                .body("post body").build();

        Comment comment = Comment.builder()
                .id(10L)
                .body(commentBody)
                .user(user)
                .post(post).build();
        comment.setCreatedDate(timestamp.getTime());

        String requestBody = objectMapper.writeValueAsString(commentRequest);

        when(commentService.save(postId, commentRequest, null)).thenReturn(comment);

        //when
        //then
        mockMvc.perform(post(END_POINT_PATH.formatted(postId)).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.body", is(commentBody)))
                .andExpect(jsonPath("$.createdDate", is("2023-10-12T12:12:12")))
                .andExpect(jsonPath("$._links.post.href", is("http://localhost/api/v1/posts/1")))
                .andExpect(jsonPath("$._links.user.href", is("http://localhost/api/v1/users/2")))
                .andDo(print());
    }

    @Test
    public void test_delete_comment_should_return_no_content() throws Exception {
        //given
        Long postId = 1L;
        Long commentId = 1L;

        //when
        //then
        mockMvc.perform(delete(END_POINT_PATH.formatted(postId) + "/" + commentId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());

        verify(commentService).delete(postId, commentId, null);
    }

    @Test
    public void test_get_comment_should_return_200_ok() throws Exception {
        //given
        Long postId = 1L;
        Long commentId = 1L;
        String commentBody = "This is comment body";

        User user = User.builder()
                .id(2L).build();

        Post post = Post.builder()
                .id(postId)
                .title("title of the post")
                .body("body of the post").build();

        Comment comment = Comment.builder()
                .id(commentId)
                .body(commentBody)
                .user(user)
                .post(post).build();

        when(commentService.getById(postId, commentId)).thenReturn(comment);

        //when
        //then
        mockMvc.perform(get(END_POINT_PATH.formatted(postId) + "/" + commentId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.body", is(commentBody)))
                .andExpect(jsonPath("$._links.post.href", is("http://localhost/api/v1/posts/" + postId)))
                .andExpect(jsonPath("$._links.user.href", is("http://localhost/api/v1/users/2")))
                .andDo(print());
    }

    @Test
    public void test_update_comment_should_return_400_bad_request() throws Exception {
        //given
        Long postId = 1L;
        long commentId = 1L;

        CommentRequest commentRequest = CommentRequest.builder()
                .body("ABC").build();

        String requestBody = objectMapper.writeValueAsString(commentRequest);

        //when
        MvcResult mvcResult = mockMvc.perform(put(END_POINT_PATH.formatted(postId) + "/" + commentId).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();

        //then
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(response).contains("body size must be between 10 and 1024");
    }

    @Test
    public void test_update_comment_should_return_404_resource_not_found() throws Exception {
        //given
        Long postId = 1L;
        long commentId = 1L;

        CommentRequest commentRequest = CommentRequest.builder()
                .body("This is body of the new comment").build();

        String requestBody = objectMapper.writeValueAsString(commentRequest);

        when(commentService.update(postId, commentId, commentRequest, null)).thenThrow(ResourceNotFoundException.class);

        //when
        //then
        mockMvc.perform(put(END_POINT_PATH.formatted(postId) + "/" + commentId).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void test_update_comment_should_return_200_success() throws Exception {
        //given
        Long postId = 1L;
        long commentId = 1L;
        String commentBody = "This is body of the new comment";
        LocalDateTime createdDate = LocalDateTime.of(2023, 10, 12, 12, 12, 12);
        Timestamp timestamp = Timestamp.valueOf(createdDate);
        CommentRequest commentRequest = CommentRequest.builder()
                .body(commentBody).build();

        Post post = Post.builder()
                .id(postId)
                .title("Post title")
                .body("post body").build();

        User user = User.builder()
                .id(1L).build();

        Comment comment = Comment.builder()
                .id(commentId)
                .body(commentBody)
                .user(user)
                .post(post).build();
        comment.setCreatedDate(timestamp.getTime());

        String requestBody = objectMapper.writeValueAsString(commentRequest);

        when(commentService.update(postId, commentId, commentRequest, null)).thenReturn(comment);

        //when
        //then
        mockMvc.perform(put(END_POINT_PATH.formatted(postId) + "/" + commentId).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.body", is(commentBody)))
                .andExpect(jsonPath("$.createdDate", is("2023-10-12T12:12:12")))
                .andExpect(jsonPath("$._links.post.href", is("http://localhost/api/v1/posts/1")))
                .andExpect(jsonPath("$._links.user.href", is("http://localhost/api/v1/users/1")))
                .andDo(print());
    }
}