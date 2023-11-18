package com.example.blog.comment;

import com.example.blog.exception.RequestValidationException;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.post.Post;
import com.example.blog.post.PostRepository;
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

    private CommentService underTest;

    @BeforeEach
    public void setUp() {
        underTest = new CommentService(commentRepository, postRepository);
    }

    @Test
    public void test_get_comments_page_for_post_id_return_empty() {
        //given
        Long postId = 1L;
        Pageable pageable = PageRequest.of(0, 5);
        when(commentRepository.findAllInPost(postId, pageable)).thenReturn(Page.empty());
        when(postRepository.existsById(postId)).thenReturn(true);

        //when
        Page<Comment> actual = underTest.fetchCommentDataForPostAsPage(postId, pageable);

        //then
        assertThat(actual).isEmpty();
        verify(commentRepository).findAllInPost(postId, pageable);
    }

    @Test
    public void test_get_comments_page_for_post_id_that_does_not_exists_throw_404() {
        //given
        Long postId = 1L;
        Pageable pageable = PageRequest.of(0, 5);
        when(postRepository.existsById(postId)).thenReturn(false);

        //when
        //then
        assertThatThrownBy(() -> underTest.fetchCommentDataForPostAsPage(postId, pageable))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Post with id [%d] does not exists".formatted(postId));

        verify(commentRepository, never()).findAllInPost(postId, pageable);
    }

    @Test
    public void test_save_comment_success() {
        //given
        Long postId = 1L;
        String body = "body of the new comment";
        CommentRequest request = CommentRequest.builder()
                .body(body).build();
        Post post = Post.builder()
                .id(postId)
                .title("title of post")
                .body("Body of post").build();
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        //when
        underTest.save(postId, request);

        //then
        ArgumentCaptor<Comment> commentArgumentCaptor = ArgumentCaptor.forClass(Comment.class);

        verify(commentRepository).save(commentArgumentCaptor.capture());

        Comment capturedComment = commentArgumentCaptor.getValue();

        assertThat(capturedComment).isNotNull();
        assertThat(capturedComment.getId()).isNull();
        assertThat(capturedComment.getBody()).isEqualTo(body);
        assertThat(capturedComment.getPost()).isEqualTo(post);
    }

    @Test
    public void test_save_comment_should_throw_resource_not_found_exception() {
        //given
        Long postId = 1L;
        CommentRequest request = CommentRequest.builder()
                .body("body of the new comment").build();

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> underTest.save(postId, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Post with id [%d] does not exist".formatted(postId));
        verify(commentRepository, never()).save(any());
    }

    @Test
    public void test_delete_comment_should_throw_resource_not_found_exception_for_post_id() {
        //given
        Long postId = 1L;
        Long commentId = 2L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        //when
        assertThatThrownBy(() -> underTest.delete(postId, commentId))
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
        Post post = Post.builder()
                .id(postId)
                .title("title")
                .body("body of post").build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        //when
        assertThatThrownBy(() -> underTest.delete(postId, commentId))
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
        assertThatThrownBy(() -> underTest.delete(postId, commentId))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("Comment does not belong to post with id [%d]".formatted(postId));

        //then
        verify(commentRepository, never()).delete(any());
    }

    @Test
    public void test_delete_comment_success() {
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
        underTest.delete(postId, commentId);

        //then
        ArgumentCaptor<Comment> commentArgumentCaptor = ArgumentCaptor.forClass(Comment.class);

        verify(commentRepository).delete(commentArgumentCaptor.capture());

        Comment capturedComment = commentArgumentCaptor.getValue();

        assertThat(capturedComment.getId()).isEqualTo(commentId);
        assertThat(capturedComment.getBody()).isEqualTo(bodyOfComment);
        assertThat(capturedComment.getPost()).isEqualTo(post);
    }

    @Test
    public void test_get_comment_success() {
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
        Post post = Post.builder()
                .id(postId)
                .title("title")
                .body("body of post").build();
        String bodyOfComment = "body of comment";
        Comment comment = Comment.builder()
                .id(commentId)
                .body(bodyOfComment)
                .post(post).build();

        when(postRepository.findById(postId)).thenReturn(Optional.empty());
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

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
        Post post = Post.builder()
                .id(postId)
                .title("title")
                .body("body of post").build();
        String bodyOfComment = "body of comment";

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
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
    public void update_comment_should_success() {
        //given
        Long postId = 1L;
        Long commentId = 1L;

        CommentRequest request = CommentRequest.builder()
                .body("This is body of the updated comment").build();

        Post post = Post.builder()
                .id(postId)
                .title("title of post")
                .body("body of post").build();

        Comment comment = Comment.builder()
                .id(commentId)
                .body("Old body of comment")
                .post(post)
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        //when
        underTest.update(postId, commentId, request);

        //then
        ArgumentCaptor<Comment> commentArgumentCaptor = ArgumentCaptor.forClass(Comment.class);

        verify(commentRepository).save(commentArgumentCaptor.capture());

        Comment result = commentArgumentCaptor.getValue();

        assertThat(result.getPost()).isEqualTo(post);
        assertThat(result.getId()).isEqualTo(commentId);
        assertThat(result.getBody()).isEqualTo(request.getBody());
    }

    @Test
    public void test_update_comment_should_throw_resource_not_found_for_post() {
        //given
        Long postId = 1L;
        Long commentId = 1L;

        CommentRequest request = CommentRequest.builder()
                .body("This is body of the updated comment").build();

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> underTest.update(postId, commentId, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Post with id [%d] does not exist".formatted(postId));
    }

    @Test
    public void test_update_comment_should_throw_resource_not_found_for_comment() {
        //given
        Long postId = 1L;
        Long commentId = 1L;

        CommentRequest request = CommentRequest.builder()
                .body("This is body of the updated comment").build();

        Post post = Post.builder()
                .id(postId)
                .title("title of post")
                .body("body of post").build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> underTest.update(postId, commentId, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Comment with id [%d] does not exist".formatted(commentId));
    }

    @Test
    public void test_update_comment_should_throw_request_validation() {
        //given
        Long postId = 1L;
        Long commentId = 1L;

        CommentRequest request = CommentRequest.builder()
                .body("This is body of the updated comment").build();

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
        assertThatThrownBy(() -> underTest.update(postId, commentId, request))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("Comment does not belong to post with id [%d]".formatted(postId));
    }
}