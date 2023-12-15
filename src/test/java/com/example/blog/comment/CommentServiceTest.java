package com.example.blog.comment;

import com.example.blog.auth.AuthorizationService;
import com.example.blog.entity.Comment;
import com.example.blog.entity.User;
import com.example.blog.exception.RequestValidationException;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.entity.Post;
import com.example.blog.post.PostRepository;
import com.example.blog.security.UserPrincipal;
import com.example.blog.user.UserOwnedEntity;
import com.example.blog.user.UserRetrievalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserRetrievalService userRetrievalService;
    @Mock
    private AuthorizationService authorizationService;

    private CommentService underTest;

    @BeforeEach
    public void setUp() {
        underTest = new CommentService(commentRepository, postRepository, userRetrievalService, authorizationService);
    }

    @Test
    public void test_get_comments_page_for_post_id_should_return_empty() {
        //given
        Long postId = 1L;
        when(postRepository.existsById(postId)).thenReturn(true);
        when(commentRepository.findAllInPost(anyLong(), any(Pageable.class))).thenReturn(Page.empty());

        //when
        Page<Comment> actual = underTest.getCommentsAsPage(postId, Pageable.unpaged());

        //then
        assertThat(actual).isEmpty();
        verify(commentRepository).findAllInPost(anyLong(), any(Pageable.class));
    }

    @Test
    public void test_get_comments_page_for_post_id_that_does_not_exists_should_throw_resource_not_found() {
        //given
        Long postId = 1L;
        Pageable pageable = PageRequest.of(0, 5);
        when(postRepository.existsById(postId)).thenReturn(false);

        //when
        //then
        assertThatThrownBy(() -> underTest.getCommentsAsPage(postId, pageable))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Post with id [%d] does not exists".formatted(postId));

        verify(commentRepository, never()).findAllInPost(postId, pageable);
    }

    @Test
    public void test_get_comment_by_id_should_return_comment() {
        //given
        Long postId = 1L;
        Long commentId = 2L;
        Post post = Post.builder()
                .id(postId)
                .title("title")
                .body("body of post").build();
        String bodyOfComment = "body of comment";
        Comment comment = Comment.builder()
                .id(commentId)
                .body(bodyOfComment)
                .post(post).build();


        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        //when
        Comment result = underTest.getById(postId, commentId);

        //then
        assertThat(result.getId()).isEqualTo(commentId);
        assertThat(result.getBody()).isEqualTo(bodyOfComment);
        assertThat(result.getPost()).isEqualTo(post);
    }

    @Test
    public void test_get_comment_should_throw_resource_not_found_for_post_id() {
        //given
        Long postId = 1L;
        Long commentId = 2L;

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> underTest.getById(postId, commentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Post with id [%d] does not exist".formatted(postId));
    }

    @Test
    public void test_get_comment_should_throw_resource_not_found_for_comment_id() {
        //given
        Long postId = 1L;
        Long commentId = 2L;
        Post mockedPost = mock(Post.class);
        when(postRepository.findById(postId)).thenReturn(Optional.of(mockedPost));
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> underTest.getById(postId, commentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Comment with id [%d] does not exist".formatted(commentId));
    }

    @Test
    public void test_get_comment_should_throw_request_validation_exception_comment_does_not_belong_to_post() {
        //given
        Long postId = 1L;
        Long commentId = 2L;
        Post post = Post.builder()
                .id(postId)
                .title("title")
                .body("body of post").build();
        Post postForComment = Post.builder()
                .id(3L)
                .title("title")
                .body("body").build();
        Comment comment = Comment.builder()
                .id(commentId)
                .body("body of comment")
                .post(postForComment).build();
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        //when
        //then
        assertThatThrownBy(() -> underTest.getById(postId, commentId))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("Comment does not belong to post with id [%d]".formatted(postId));
    }

    @Test
    public void test_save_comment_with_valid_data_should_success() {
        //given
        String body = "body of the new comment";
        CommentRequest request = mock(CommentRequest.class);
        when(request.getBody()).thenReturn(body);
        UserPrincipal userPrincipal = mock(UserPrincipal.class);
        when(userPrincipal.getEmail()).thenReturn("zxc@gmail.com");
        Post mockedPost = mock(Post.class);
        User mockedUser = mock(User.class);
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(mockedPost));
        when(userRetrievalService.getUserByEmail(anyString())).thenReturn(mockedUser);

        //when
        underTest.save(anyLong(), request, userPrincipal);

        //then
        ArgumentCaptor<Comment> commentArgumentCaptor = ArgumentCaptor.forClass(Comment.class);

        verify(commentRepository).save(commentArgumentCaptor.capture());

        Comment capturedComment = commentArgumentCaptor.getValue();

        assertThat(capturedComment).isNotNull();
        assertThat(capturedComment.getId()).isNull();
        assertThat(capturedComment.getBody()).isEqualTo(body);
        assertThat(capturedComment.getPost()).isEqualTo(mockedPost);
        assertThat(capturedComment.getUser()).isEqualTo(mockedUser);
    }

    @Test
    public void test_save_comment_should_throw_resource_not_found_exception() {
        //given
        Long postId = 1L;
        CommentRequest request = CommentRequest.builder()
                .body("body of the new comment").build();
        UserPrincipal mockedUser = mock(UserPrincipal.class);

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> underTest.save(postId, request, mockedUser))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Post with id [%d] does not exist".formatted(postId));
        verify(commentRepository, never()).save(any());
    }

    @Test
    public void test_update_comment_with_valid_data_should_success() {
        //given
        Long postId = 1L;
        Long commentId = 1L;
        String requestBody = "This is new body that has to be updated";

        CommentRequest mockedRequest = mock(CommentRequest.class);
        when(mockedRequest.getBody()).thenReturn(requestBody);

        Post mockedPost = mock(Post.class);
        when(mockedPost.getId()).thenReturn(postId);

        Comment comment = Comment.builder()
                .id(commentId)
                .body("Old body of the comment")
                .post(mockedPost).build();

        UserPrincipal mockedUser = mock(UserPrincipal.class);
        when(postRepository.findById(postId)).thenReturn(Optional.of(mockedPost));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        //when
        underTest.update(postId, commentId, mockedRequest, mockedUser);

        //then
        ArgumentCaptor<Comment> commentArgumentCaptor = ArgumentCaptor.forClass(Comment.class);

        verify(commentRepository).save(commentArgumentCaptor.capture());
        verify(authorizationService, times(1)).hasAuthorizationForUpdateOrDeleteEntity(comment, mockedUser);

        Comment result = commentArgumentCaptor.getValue();

        assertThat(result.getPost()).isEqualTo(mockedPost);
        assertThat(result.getBody()).isEqualTo(requestBody);
    }

    @Test
    public void test_update_comment_should_throw_resource_not_found_for_post() {
        //given
        Long postId = 1L;
        Long commentId = 1L;
        CommentRequest mockedRequest = mock(CommentRequest.class);
        UserPrincipal mockedUser = mock(UserPrincipal.class);
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> underTest.update(postId, commentId, mockedRequest, mockedUser))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Post with id [%d] does not exist".formatted(postId));
    }

    @Test
    public void test_update_comment_should_throw_resource_not_found_for_comment() {
        //given
        Long postId = 1L;
        Long commentId = 1L;
        CommentRequest mockedRequest = mock(CommentRequest.class);
        UserPrincipal mockedUser = mock(UserPrincipal.class);
        Post mockedPost = mock(Post.class);

        when(postRepository.findById(postId)).thenReturn(Optional.of(mockedPost));
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> underTest.update(postId, commentId, mockedRequest, mockedUser))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Comment with id [%d] does not exist".formatted(commentId));
    }

    @Test
    public void test_update_comment_should_throw_request_validation() {
        //given
        Long postId = 1L;
        Long commentId = 1L;
        CommentRequest mockedRequest = mock(CommentRequest.class);
        UserPrincipal mockedUser = mock(UserPrincipal.class);

        Post post = Post.builder()
                .id(postId)
                .title("title of post")
                .body("body of post").build();

        Post postForComment = Post.builder()
                .id(2L)
                .title("title of post")
                .body("body of post").build();

        Comment comment = Comment.builder()
                .id(commentId)
                .body("Old body of comment")
                .post(postForComment)
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        //when
        //then
        assertThatThrownBy(() -> underTest.update(postId, commentId, mockedRequest, mockedUser))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("Comment does not belong to post with id [%d]".formatted(postId));
    }

    @Test
    public void test_delete_comment_with_valid_data_should_success() {
        //given
        Long postId = 1L;
        Long commentId = 2L;
        UserPrincipal mockedUser = mock(UserPrincipal.class);
        Post post = Post.builder()
                .id(postId)
                .title("title")
                .body("body of post").build();
        String bodyOfComment = "body of comment";
        Comment comment = Comment.builder()
                .id(commentId)
                .body(bodyOfComment)
                .post(post).build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        //when
        underTest.delete(postId, commentId, mockedUser);

        //then
        ArgumentCaptor<Comment> commentArgumentCaptor = ArgumentCaptor.forClass(Comment.class);

        verify(commentRepository).delete(commentArgumentCaptor.capture());
        verify(authorizationService, times(1)).hasAuthorizationForUpdateOrDeleteEntity(comment, mockedUser);

        Comment capturedComment = commentArgumentCaptor.getValue();

        assertThat(capturedComment.getId()).isEqualTo(commentId);
        assertThat(capturedComment.getBody()).isEqualTo(bodyOfComment);
        assertThat(capturedComment.getPost()).isEqualTo(post);
    }

    @Test
    public void test_delete_comment_should_throw_resource_not_found_exception_for_post_id() {
        //given
        Long postId = 1L;
        Long commentId = 2L;
        UserPrincipal mockedUser = mock(UserPrincipal.class);
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        //when
        assertThatThrownBy(() -> underTest.delete(postId, commentId, mockedUser))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Post with id [%d] does not exist".formatted(postId));

        //then
        verify(commentRepository, never()).delete(any());
    }

    @Test
    public void test_delete_comment_should_throw_resource_not_found_exception_for_comment_id() {
        //given
        Long postId = 1L;
        Long commentId = 2L;
        UserPrincipal mockedUser = mock(UserPrincipal.class);
        Post post = Post.builder()
                .id(postId)
                .title("title")
                .body("body of post").build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        //when
        assertThatThrownBy(() -> underTest.delete(postId, commentId, mockedUser))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Comment with id [%d] does not exist".formatted(commentId));

        //then
        verify(commentRepository, never()).delete(any());
    }

    @Test
    public void test_delete_comment_should_throw_request_validation_exception_comment_does_not_belong_to_post() {
        //given
        Long postId = 1L;
        Long commentId = 2L;
        UserPrincipal mockedUser = mock(UserPrincipal.class);
        Post post = Post.builder()
                .id(postId)
                .title("title")
                .body("body of post").build();
        Post postForComment = Post.builder()
                .id(3L)
                .title("title")
                .body("body").build();
        Comment comment = Comment.builder()
                .id(commentId)
                .body("body of comment")
                .post(postForComment).build();
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        //when
        assertThatThrownBy(() -> underTest.delete(postId, commentId, mockedUser))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("Comment does not belong to post with id [%d]".formatted(postId));

        //then
        verify(commentRepository, never()).delete(any());
    }
}