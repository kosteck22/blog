package com.example.blog.tag;

import com.example.blog.entity.Post;
import com.example.blog.entity.Tag;
import com.example.blog.exception.DuplicateResourceException;
import com.example.blog.exception.ResourceNotFoundException;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {
    @Mock
    private TagRepository tagRepository;

    @Mock
    private PostRepository postRepository;

    private TagService underTest;

    @BeforeEach
    public void setUp() {
        underTest = new TagService(tagRepository, postRepository);
    }

    @Test
    public void test_get_tags_as_page_return_empty() {
        //given
        Pageable pageable = PageRequest.of(0, 5);
        when(tagRepository.findAll(pageable)).thenReturn(Page.empty());

        //when
        Page<Tag> tagPage = underTest.getTagsAsPage(pageable);

        //then
        assertThat(tagPage).isEmpty();
        verify(tagRepository).findAll(pageable);
    }

    @Test
    public void test_get_tags_as_page_by_post_return_empty_page() {
        //given
        long postId = 1L;
        Post post = mock(Post.class);
        Pageable pageable = PageRequest.of(0, 5);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(tagRepository.findByPostsIn(List.of(post), pageable)).thenReturn(Page.empty());

        //when
        Page<Tag> tagPage = underTest.getTagsForPostAsPage(postId, pageable);

        //then
        assertThat(tagPage).isEmpty();
        verify(tagRepository).findByPostsIn(any(List.class), any(Pageable.class));
    }

    @Test
    public void test_get_tags_as_page_by_post_throws_resource_not_found() {
        //given
        long postId = 1L;
        Pageable pageable = PageRequest.of(0, 5);
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        //when
        assertThatThrownBy(() -> underTest.getTagsForPostAsPage(postId, pageable))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Post with id [%d] does not exist".formatted(postId));

        //then
        verify(tagRepository, never()).findByPostsIn(any(List.class), any(Pageable.class));
    }

    @Test
    public void test_get_tag_by_id_return_tag() {
        //given
        long tagId = 1L;
        Tag tag = mock(Tag.class);
        when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));

        //when
        Tag result = underTest.getTagById(tagId);

        //then
        assertThat(result).isEqualTo(tag);
    }

    @Test
    public void test_get_tag_by_id_throws_resource_not_found() {
        //given
        long tagId = 1L;
        when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> underTest.getTagById(tagId))
                .isInstanceOf(ResourceNotFoundException.class)
                        .hasMessage("Tag with id [%d] not found".formatted(tagId));
    }

    @Test
    public void test_save_tag_success() {
        //given
        TagRequest request = TagRequest.builder()
                .name("New tag").build();
        when(tagRepository.existsByName(request.getName())).thenReturn(false);
        Tag mockedTag = mock(Tag.class);
        when(tagRepository.save(any(Tag.class))).thenReturn(mockedTag);

        //when
        Tag result = underTest.save(request);

        //then
        ArgumentCaptor<Tag> tagArgumentCaptor = ArgumentCaptor.forClass(Tag.class);
        verify(tagRepository).save(tagArgumentCaptor.capture());

        Tag tagBeforeSave = tagArgumentCaptor.getValue();

        assertThat(tagBeforeSave.getId()).isNull();
        assertThat(tagBeforeSave.getName()).isEqualTo(request.getName());
        assertThat(tagBeforeSave.getPosts()).isEmpty();
        assertThat(result).isEqualTo(mockedTag);
    }

    @Test
    public void test_save_tag_throws_duplicate_resource_name_not_unique() {
        //given
        TagRequest request = TagRequest.builder()
                .name("New tag").build();
        when(tagRepository.existsByName(request.getName())).thenReturn(true);

        //when
        //then
        assertThatThrownBy(() -> underTest.save(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Tag with name [%s] already exists".formatted(request.getName()));

        verify(tagRepository, never()).save(any(Tag.class));
    }

    @Test
    public void test_update_tag_should_success() {
        //given
        Long tagId = 1L;
        TagRequest request = TagRequest.builder()
                .name("New tag").build();
        Tag mockedTag = Tag.builder()
                .id(1L)
                .name("Old name").build();
        Tag mockedSavedTagResult = mock(Tag.class);
        when(tagRepository.findById(tagId)).thenReturn(Optional.of(mockedTag));
        when(tagRepository.existsByName(request.getName())).thenReturn(false);
        when(tagRepository.save(any(Tag.class))).thenReturn(mockedSavedTagResult);

        //when
        Tag result = underTest.update(tagId, request);

        //then
        ArgumentCaptor<Tag> tagArgumentCaptor = ArgumentCaptor.forClass(Tag.class);
        verify(tagRepository).save(tagArgumentCaptor.capture());

        Tag tagBeforeSave = tagArgumentCaptor.getValue();

        assertThat(tagBeforeSave.getId()).isEqualTo(1L);
        assertThat(tagBeforeSave.getName()).isEqualTo(request.getName());
        assertThat(tagBeforeSave).isEqualTo(mockedTag);
        assertThat(result).isEqualTo(mockedSavedTagResult);
    }

    @Test
    public void test_update_tag_throws_duplicate_resource_new_name_not_unique() {
        //given
        Long tagId = 1L;
        TagRequest request = TagRequest.builder()
                .name("New tag").build();
        when(tagRepository.existsByName(request.getName())).thenReturn(true);

        //when
        //then
        assertThatThrownBy(() -> underTest.update(tagId, request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Tag with name [%s] already exists".formatted(request.getName()));

        verify(tagRepository, never()).save(any(Tag.class));
    }

    @Test
    public void test_update_tag_throws_resource_not_found() {
        //given
        Long tagId = 1L;
        TagRequest request = TagRequest.builder()
                .name("New tag").build();
        when(tagRepository.existsByName(request.getName())).thenReturn(false);
        when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> underTest.update(tagId, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Tag with id [%d] not found".formatted(tagId));

        verify(tagRepository, never()).save(any(Tag.class));
    }

    @Test
    public void test_delete_tag_success() {
        //given
        Long tagId = 1L;
        Tag tag = Tag.builder()
                .id(tagId)
                .name("Tag to delete").build();
        when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));

        //when
        underTest.delete(tagId);

        //then
        ArgumentCaptor<Tag> tagArgumentCaptor = ArgumentCaptor.forClass(Tag.class);
        verify(tagRepository).delete(tagArgumentCaptor.capture());
        Tag tagBeforeDelete = tagArgumentCaptor.getValue();

        assertThat(tagBeforeDelete.getId()).isEqualTo(tagId);
        assertThat(tagBeforeDelete.getName()).isEqualTo(tag.getName());
        assertThat(tagBeforeDelete.getPosts()).isEmpty();

        verify(tagRepository, times(1)).delete(tag);
    }
}