package com.example.blog.post;

import com.example.blog.entity.Post;
import com.example.blog.exception.DuplicateResourceException;
import com.example.blog.exception.RequestValidationException;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.tag.TagRepository;
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
class PostServiceTest {
    @Mock
    private PostRepository postRepository;

    @Mock
    private TagRepository tagRepository;

    private PostService underTest;

    @BeforeEach
    public void setUp() {
        underTest = new PostService(postRepository, tagRepository, null, null, null);
    }

    @Test
    public void test_save_post_success() {
        //given
        String title = "This is title";
        String body = "This is body";
        PostRequest request = PostRequest.builder()
                .title(title)
                .body(body).build();
        when(postRepository.existsByTitle(title)).thenReturn(false);

        //when
        underTest.save(request, null);

        //then
        ArgumentCaptor<Post> postArgumentCaptor = ArgumentCaptor.forClass(Post.class);

        verify(postRepository).save(postArgumentCaptor.capture());

        Post capturedPost = postArgumentCaptor.getValue();

        assertThat(capturedPost.getId()).isNull();
        assertThat(capturedPost.getTitle()).isEqualTo(title);
        assertThat(capturedPost.getBody()).isEqualTo(body);
    }

    @Test
    public void test_save_post_throws_duplicate_resource_exception() {
        //given
        String title = "This is title";
        String body = "This is body";
        PostRequest request = PostRequest.builder()
                .title(title)
                .body(body).build();
        when(postRepository.existsByTitle(title)).thenReturn(true);

        //when
        //then
        assertThatThrownBy(() -> underTest.save(request, null))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Post with title [%s] already exists".formatted(title));
        verify(postRepository, never()).save(any());
    }

    @Test
    public void test_fetch_post_data_as_page_empty() {
        //given
        Pageable pageable = PageRequest.of(0, 5);
        when(postRepository.findAll(pageable)).thenReturn(Page.empty());

        //when
        Page<Post> posts = underTest.getPostsAsPage(pageable);

        //then
        assertThat(posts).isEmpty();
        verify(postRepository).findAll(pageable);
    }

    @Test
    public void test_get_post_by_id_success() {
        //given
        Long id = 1L;
        Post expected = Post.builder()
                .id(id)
                .title("title of post")
                .body("body of post")
                .build();
        when(postRepository.findById(id)).thenReturn(Optional.of(expected));

        //when
        Post actual = underTest.getPostById(id);

        //then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void test_get_post_by_id_throws_exception_when_return_empty_optional() {
        //given
        Long id = 1L;
        when(postRepository.findById(id)).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> underTest.getPostById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Post with id [%d] does not exist".formatted(id));
    }

    @Test
    public void test_delete_post_by_id_success() {
        //given
        Long id = 1L;
        when(postRepository.existsById(id)).thenReturn(true);

        //when
        underTest.delete(id, null);

        //then
        verify(postRepository).deleteById(id);
    }

    @Test
    public void test_delete_post_by_id_throws_resource_not_found_exception() {
        //given
        Long id = 1L;
        when(postRepository.existsById(id)).thenReturn(false);

        //when
        assertThatThrownBy(() -> underTest.delete(id, null))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Post with id [%d] does not exist".formatted(id));

        //then
        verify(postRepository, never()).deleteById(id);
    }

    @Test
    public void test_update_post_success() {
        //given
        Long id = 1L;
        String requestTitle = "This is new title";
        String requestBody = "This is new body";

        Post post = Post.builder()
                .id(id)
                .title("This is title")
                .body("This is body").build();

        PostRequest request = PostRequest.builder()
                .title(requestTitle)
                .body(requestBody).build();

        when(postRepository.findById(id)).thenReturn(Optional.of(post));
        when(postRepository.findByTitle(requestTitle)).thenReturn(Optional.empty());

        //when
        underTest.update(id, request, null);

        //then
        ArgumentCaptor<Post> postArgumentCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postArgumentCaptor.capture());

        Post capturedPost = postArgumentCaptor.getValue();
        assertThat(capturedPost.getTitle()).isEqualTo(requestTitle);
        assertThat(capturedPost.getBody()).isEqualTo(requestBody);
    }

    @Test
    public void test_update_post_throws_resource_not_found() {
        //given
        Long id = 1L;
        String requestTitle = "This is new title";
        String requestBody = "This is new body";

        PostRequest request = PostRequest.builder()
                .title(requestTitle)
                .body(requestBody).build();

        when(postRepository.findById(id)).thenReturn(Optional.empty());

        //when
        assertThatThrownBy(() -> underTest.update(id, request, null))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Post with id [%d] does not exist".formatted(id));

        //then
        verify(postRepository, never()).save(any());
    }

    @Test
    public void test_update_post_throws_request_validation_exception() {
        //given
        Long id = 1L;
        String requestTitle = "This is new title";
        String requestBody = "This is new body";

        Post post = Post.builder()
                .id(id)
                .title("This is title")
                .body("This is body").build();

        Post postFromDB = Post.builder()
                .id(2L)
                .title(requestTitle)
                .body("This is body").build();

        PostRequest request = PostRequest.builder()
                .title(requestTitle)
                .body(requestBody).build();

        when(postRepository.findById(id)).thenReturn(Optional.of(post));
        when(postRepository.findByTitle(requestTitle)).thenReturn(Optional.of(postFromDB));

        //when
        assertThatThrownBy(() -> underTest.update(id, request, null))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("Title [%s] already taken".formatted(requestTitle));

        //then
        verify(postRepository, never()).save(any());
    }
}