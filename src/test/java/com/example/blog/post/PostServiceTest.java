package com.example.blog.post;

import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    private PostRepository postRepository;

    private PostService underTest;

    @BeforeEach
    public void setUp() {
        underTest = new PostService(postRepository);
    }

    @Test
    public void test_save_post_success() {
        //given
        String title = "This is title";
        String body = "This is body";
        PostRequest request = PostRequest.builder()
                .title(title)
                .body(body).build();

        //when
        underTest.save(request);

        //then
        ArgumentCaptor<Post> postArgumentCaptor = ArgumentCaptor.forClass(Post.class);

        verify(postRepository).save(postArgumentCaptor.capture());

        Post capturedPost = postArgumentCaptor.getValue();

        assertThat(capturedPost.getId()).isNull();
        assertThat(capturedPost.getTitle()).isEqualTo(title);
        assertThat(capturedPost.getBody()).isEqualTo(body);
    }

    @Test
    public void test_fetch_post_data_as_page() {
        //given
        Pageable pageable = PageRequest.of(0, 5);
        when(postRepository.findAll(pageable)).thenReturn(Page.empty());

        //when
        Page<Post> posts = underTest.fetchPostDataAsPage(pageable);

        //then
        assertThat(posts).isEmpty();
    }
}