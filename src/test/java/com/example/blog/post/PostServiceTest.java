package com.example.blog.post;

import com.example.blog.auth.AuthorizationService;
import com.example.blog.category.CategoryRepository;
import com.example.blog.entity.Category;
import com.example.blog.entity.Post;
import com.example.blog.entity.Tag;
import com.example.blog.entity.User;
import com.example.blog.exception.DuplicateResourceException;
import com.example.blog.exception.RequestValidationException;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.security.UserPrincipal;
import com.example.blog.tag.TagRepository;
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

import java.util.*;

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

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRetrievalService userRetrievalService;

    @Mock
    private AuthorizationService authorizationService;

    private PostService underTest;

    @BeforeEach
    public void setUp() {
        underTest = new PostService(
                postRepository,
                tagRepository,
                categoryRepository,
                userRetrievalService,
                authorizationService);
    }

    @Test
    public void test_save_post_success() {
        //given
        PostRequest request = PostRequest.builder()
                .title("This is title")
                .body("This is body")
                .categoryId(1L)
                .tags(List.of("tag1", "tag2")).build();
        Tag tag1 = Tag.builder().id(1L).name("tag1").build();
        Tag tag2 = Tag.builder().id(2L).name("tag2").build();

        UserPrincipal mockedUserPrincipal = mock(UserPrincipal.class);
        Category mockedCategory = mock(Category.class);

        User mockedUser = mock(User.class);
        when(mockedUserPrincipal.getEmail()).thenReturn("qwe@gmail.com");

        when(postRepository.existsByTitle("This is title")).thenReturn(false);
        when(userRetrievalService.getUserByEmail(mockedUserPrincipal.getEmail())).thenReturn(mockedUser);
        when(categoryRepository.findById(request.getCategoryId())).thenReturn(Optional.of(mockedCategory));
        when(tagRepository.findByName("tag1")).thenReturn(Optional.of(tag1));
        when(tagRepository.findByName("tag2")).thenReturn(Optional.of(tag2));

        //when
        underTest.save(request, mockedUserPrincipal);

        //then
        ArgumentCaptor<Post> postArgumentCaptor = ArgumentCaptor.forClass(Post.class);

        verify(postRepository).save(postArgumentCaptor.capture());

        Post capturedPost = postArgumentCaptor.getValue();

        assertThat(capturedPost.getId()).isNull();
        assertThat(capturedPost.getTitle()).isEqualTo(request.getTitle());
        assertThat(capturedPost.getBody()).isEqualTo(request.getBody());
        assertThat(capturedPost.getCategory()).isEqualTo(mockedCategory);
        assertThat(capturedPost.getUser()).isEqualTo(mockedUser);
        assertThat(capturedPost.getTags()).contains(tag1);
        assertThat(capturedPost.getTags()).contains(tag2);
    }

    @Test
    public void test_save_post_throws_duplicate_resource_exception() {
        //given
        PostRequest request = PostRequest.builder()
                .title("This is title")
                .body("This is body")
                .categoryId(1L)
                .tags(List.of("tag1", "tag2")).build();
        UserPrincipal mockedUserPrincipal = mock(UserPrincipal.class);
        when(postRepository.existsByTitle(request.getTitle())).thenReturn(true);

        //when
        //then
        assertThatThrownBy(() -> underTest.save(request, mockedUserPrincipal))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Post with title [%s] already exists".formatted(request.getTitle()));
        verify(postRepository, never()).save(any());
    }

    @Test
    public void test_get_posts_as_page_returns_empty() {
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
    public void test_get_posts_as_page_by_tag_id_returns_empty() {
        //given
        long tagId = 1L;
        Tag tag = mock(Tag.class);
        Pageable pageable = PageRequest.of(0, 5);
        when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));
        when(postRepository.findByTagsIn(Collections.singletonList(tag), pageable)).thenReturn(Page.empty());

        //when
        Page<Post> posts = underTest.getPostsByTagId(tagId, pageable);

        //then
        assertThat(posts).isEmpty();
        verify(postRepository).findByTagsIn(any(List.class), any(Pageable.class));
    }

    @Test
    public void test_get_posts_as_page_by_tag_id_throws_resource_not_found() {
        //given
        long tagId = 1L;
        Pageable pageable = PageRequest.of(0, 5);
        when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

        //when
        assertThatThrownBy(() -> underTest.getPostsByTagId(tagId, pageable))
                .isInstanceOf(ResourceNotFoundException.class)
                        .hasMessage("Tag with id [%d] does not exists".formatted(tagId));

        //then
        verify(postRepository, never()).findByTagsIn(any(List.class), any(Pageable.class));
    }

    @Test
    public void test_get_posts_as_page_by_category_id_returns_empty() {
        //given
        long categoryId = 1L;
        Category category = mock(Category.class);
        when(category.getId()).thenReturn(categoryId);
        Pageable pageable = PageRequest.of(0, 5);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(postRepository.findByCategoriesIn(Collections.singletonList(category.getId()), pageable)).thenReturn(Page.empty());

        //when
        Page<Post> posts = underTest.getPostsByCategoryId(categoryId, pageable);

        //then
        assertThat(posts).isEmpty();
        verify(postRepository).findByCategoriesIn(any(List.class), any(Pageable.class));
    }

    @Test
    public void test_get_posts_as_page_by_category_id_throws_resource_not_found() {
        //given
        long categoryId = 1L;
        Pageable pageable = PageRequest.of(0, 5);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        //when
        assertThatThrownBy(() -> underTest.getPostsByCategoryId(categoryId, pageable))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Category with id [%d] does not exists".formatted(categoryId));

        //then
        verify(postRepository, never()).findByCategoriesIn(any(List.class), any(Pageable.class));
    }

    @Test
    public void test_get_posts_as_page_by_user_id_returns_empty() {
        //given
        long userId = 1L;
        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);
        Pageable pageable = PageRequest.of(0, 5);
        when(userRetrievalService.getUserById(userId)).thenReturn(user);
        when(postRepository.findByUsersIn(Collections.singletonList(user.getId()), pageable)).thenReturn(Page.empty());

        //when
        Page<Post> posts = underTest.getPostsByUserId(userId, pageable);

        //then
        assertThat(posts).isEmpty();
        verify(postRepository).findByUsersIn(any(List.class), any(Pageable.class));
        verify(userRetrievalService, times(1)).getUserById(userId);
    }

    @Test
    public void test_get_posts_as_page_by_user_id_throws_resource_not_found() {
        //given
        long userId = 1L;
        Pageable pageable = PageRequest.of(0, 5);
        when(userRetrievalService.getUserById(userId)).thenThrow(new ResourceNotFoundException("User with id [%d] not found".formatted(userId)));

        //when
        assertThatThrownBy(() -> underTest.getPostsByUserId(userId, pageable))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User with id [%d] not found".formatted(userId));

        //then
        verify(postRepository, never()).findByUsersIn(any(List.class), any(Pageable.class));
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
        UserPrincipal mockedUserPrincipal = mock(UserPrincipal.class);
        Post mockedPost = mock(Post.class);
        Tag mockedTag1 = mock(Tag.class);
        Tag mockedTag2 = mock(Tag.class);

        when(postRepository.findById(id)).thenReturn(Optional.of(mockedPost));
        when(tagRepository.findOrphanedTags()).thenReturn(List.of(mockedTag1, mockedTag2));

        //when
        underTest.delete(id, mockedUserPrincipal);

        //then
        verify(postRepository).delete(mockedPost);
        verify(authorizationService).hasAuthorizationForUpdateOrDeleteEntity(mockedPost, mockedUserPrincipal);
        verify(tagRepository).delete(mockedTag1);
        verify(tagRepository).delete(mockedTag2);
    }

    @Test
    public void test_delete_post_by_id_throws_resource_not_found_exception() {
        //given
        Long id = 1L;
        UserPrincipal mockedUserPrincipal = mock(UserPrincipal.class);

        when(postRepository.findById(id)).thenReturn(Optional.empty());

        //when
        assertThatThrownBy(() -> underTest.delete(id, mockedUserPrincipal))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Post with id [%d] does not exist".formatted(id));

        //then
        verify(postRepository, never()).delete(any(Post.class));
    }

    @Test
    public void test_update_post_success() {
        //given
        Long id = 1L;
        PostRequest request = PostRequest.builder()
                .title("This is title")
                .body("This is body")
                .categoryId(1L)
                .tags(List.of("tag1", "tag2")).build();
        UserPrincipal mockedUserPrincipal = mock(UserPrincipal.class);

        Tag postTag1 = Tag.builder().id(1L).name("tag1").build();
        Tag postTag2 = Tag.builder().id(2L).name("tag2").build();
        Tag postTag3= Tag.builder().id(3L).name("tag3").build();
        Tag postTag4 = Tag.builder().id(4L).name("tag4").build();

        Category mockedCategory = mock(Category.class);
        User mockedUser = mock(User.class);

        Post post = Post.builder()
                .id(id)
                .title("Old title")
                .body("Old body")
                .category(mock(Category.class))
                .user(mockedUser)
                .tags(Set.of(postTag1, postTag2, postTag3, postTag4)).build();

        when(postRepository.findById(id)).thenReturn(Optional.of(post));
        when(categoryRepository.findById(request.getCategoryId())).thenReturn(Optional.of(mockedCategory));
        when(postRepository.findByTitle(request.getTitle())).thenReturn(Optional.empty());
        when(tagRepository.findByName("tag1")).thenReturn(Optional.of(postTag1));
        when(tagRepository.findByName("tag2")).thenReturn(Optional.of(postTag2));
        when(tagRepository.findOrphanedTags()).thenReturn(List.of(postTag3, postTag4));

        //Mock authorization
        doNothing().when(authorizationService).hasAuthorizationForUpdateOrDeleteEntity(post, mockedUserPrincipal);

        //when
        Post result = underTest.update(id, request, mockedUserPrincipal);

        //then
        verify(postRepository).save(post);
        verify(authorizationService).hasAuthorizationForUpdateOrDeleteEntity(post, mockedUserPrincipal);
        verify(tagRepository).delete(postTag3);
        verify(tagRepository).delete(postTag4);

        assertThat(result.getTitle()).isEqualTo(request.getTitle());
        assertThat(result.getBody()).isEqualTo(request.getBody());
        assertThat(result.getTags().size()).isEqualTo(2);
        assertThat(result.getTags().contains(postTag1)).isTrue();
        assertThat(result.getTags().contains(postTag2)).isTrue();
        assertThat(result.getTags().contains(postTag3)).isFalse();
        assertThat(result.getTags().contains(postTag4)).isFalse();
        assertThat(result.getCategory()).isEqualTo(mockedCategory);
        assertThat(result.getUser()).isEqualTo(mockedUser);
    }

    @Test
    public void test_update_post_throws_resource_not_found() {
        //given
        Long id = 1L;
        PostRequest request = PostRequest.builder()
                .title("This is title")
                .body("This is body")
                .categoryId(1L)
                .tags(List.of("tag1", "tag2")).build();
        UserPrincipal mockedUserPrincipal = mock(UserPrincipal.class);

        when(postRepository.findById(id)).thenReturn(Optional.empty());

        //when
        assertThatThrownBy(() -> underTest.update(id, request, mockedUserPrincipal))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Post with id [%d] does not exist".formatted(id));

        //then
        verify(postRepository, never()).save(any());
        verify(tagRepository, never()).save(any());
        verify(tagRepository, never()).delete(any());
    }
    @Test
    public void test_update_post_throws_resource_not_found_validation_exception_new_category_not_found() {
        //given
        Long id = 1L;
        PostRequest request = PostRequest.builder()
                .title("This is new title")
                .body("This is new body")
                .categoryId(1L)
                .tags(List.of("tag1", "tag2")).build();
        UserPrincipal mockedUserPrincipal = mock(UserPrincipal.class);

        Post post = Post.builder()
                .id(id)
                .title("This is title")
                .body("This is body").build();

        when(postRepository.findById(id)).thenReturn(Optional.of(post));
        when(categoryRepository.findById(request.getCategoryId())).thenReturn(Optional.empty());

        //when
        assertThatThrownBy(() -> underTest.update(id, request, mockedUserPrincipal))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Category with id [%d] does not exists".formatted(request.getCategoryId()));

        //then
        verify(postRepository, never()).save(any());
        verify(tagRepository, never()).save(any());
        verify(tagRepository, never()).delete(any());
    }

    @Test
    public void test_update_post_throws_request_validation_exception_title_not_unique() {
        //given
        Long id = 1L;
        PostRequest request = PostRequest.builder()
                .title("This is new title")
                .body("This is new body")
                .categoryId(1L)
                .tags(List.of("tag1", "tag2")).build();
        UserPrincipal mockedUserPrincipal = mock(UserPrincipal.class);

        Post post = Post.builder()
                .id(id)
                .title("This is title")
                .body("This is body").build();

        Post postFromDB = Post.builder()
                .id(2L)
                .title(request.getTitle())
                .body("This is body").build();

        Category mockedCategory = mock(Category.class);

        when(postRepository.findById(id)).thenReturn(Optional.of(post));
        when(categoryRepository.findById(request.getCategoryId())).thenReturn(Optional.of(mockedCategory));
        when(postRepository.findByTitle(request.getTitle())).thenReturn(Optional.of(postFromDB));

        //when
        assertThatThrownBy(() -> underTest.update(id, request, mockedUserPrincipal))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("Title [%s] already taken".formatted(request.getTitle()));

        //then
        verify(postRepository, never()).save(any());
        verify(tagRepository, never()).save(any());
        verify(tagRepository, never()).delete(any());
    }
}