package com.example.blog.tag;

import com.example.blog.config.AppConfig;
import com.example.blog.entity.Category;
import com.example.blog.entity.Post;
import com.example.blog.entity.Tag;
import com.example.blog.entity.User;
import com.example.blog.exception.DuplicateResourceException;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.security.JwtAuthenticationTokenFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(TagController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({TagModelAssembler.class, TagMapper.class})
class TagControllerTest {

    private static final String END_POINT_PATH = "/api/v1/tags";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TagService tagService;

    @MockBean
    private JwtAuthenticationTokenFilter filter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void test_get_tags_as_page_should_return_204_no_content() throws Exception {
        //given
        Pageable pageable = PageRequest.of(0, 2);
        when(tagService.getTagsAsPage(pageable)).thenReturn(Page.empty());

        //when
        //then
        mockMvc.perform(get(END_POINT_PATH).contentType(MediaType.APPLICATION_JSON)
                        .param("size", "2").param("page", "0"))
                .andExpect(status().isNoContent())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andDo(print());
    }

    @Test
    public void test_get_tags_as_page_should_return_200_ok() throws Exception {
        //given
        Tag tag1 = Tag.builder()
                .id(1L)
                .name("Tag 1").build();
        Tag tag2 = Tag.builder()
                .id(2L)
                .name("Tag 2").build();
        Tag tag3 = Tag.builder()
                .id(3L)
                .name("Tag 3").build();
        Pageable pageable = PageRequest.of(0, 2);
        Page<Tag> pageTag = new PageImpl<>(List.of(tag1, tag2, tag3), pageable, 3);
        when(tagService.getTagsAsPage(pageable)).thenReturn(pageTag);

        //when
        //then
        mockMvc.perform(get(END_POINT_PATH).contentType(MediaType.APPLICATION_JSON)
                        .param("size", "2").param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$._embedded.tags[0].name", is("Tag 1")))
                .andExpect(jsonPath("$._embedded.tags[1].name", is("Tag 2")))
                .andExpect(jsonPath("$._embedded.tags[0]._links.self.href", is("http://localhost/api/v1/tags/1")))
                .andExpect(jsonPath("$._embedded.tags[0]._links.posts.href", is("http://localhost/api/v1/posts/tag/1")))
                .andExpect(jsonPath("$._embedded.tags[1]._links.self.href", is("http://localhost/api/v1/tags/2")))
                .andExpect(jsonPath("$._embedded.tags[1]._links.posts.href", is("http://localhost/api/v1/posts/tag/2")))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/api/v1/tags?page=0&size=2")))
                .andExpect(jsonPath("$._links.first.href", is("http://localhost/api/v1/tags?page=0&size=2")))
                .andExpect(jsonPath("$._links.next.href", is("http://localhost/api/v1/tags?page=1&size=2")))
                .andExpect(jsonPath("$._links.last.href", is("http://localhost/api/v1/tags?page=1&size=2")))
                .andExpect(jsonPath("$.page.size", is(2)))
                .andExpect(jsonPath("$.page.totalElements", is(3)))
                .andExpect(jsonPath("$.page.totalPages", is(2)))
                .andExpect(jsonPath("$.page.number", is(0)))
                .andDo(print());
    }

    @Test
    public void test_get_tags_by_post_should_return_204_no_content() throws Exception {
        //given
        Long postId = 1L;
        Pageable pageable = PageRequest.of(0, 2);
        when(tagService.getTagsForPostAsPage(postId, pageable)).thenReturn(Page.empty());

        //when
        //then
        MvcResult mvcResult = mockMvc.perform(get(END_POINT_PATH + "/post/" + postId).contentType(MediaType.APPLICATION_JSON)
                        .param("size", "2").param("page", "0"))
                .andExpect(status().isNoContent())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andDo(print())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString()).doesNotContain("_embedded");
    }

    @Test
    public void test_get_tags_by_post_should_throw_404_not_found() throws Exception {
        //given
        Long postId = 1L;
        Pageable pageable = PageRequest.of(0, 2);
        when(tagService.getTagsForPostAsPage(postId, pageable))
                .thenThrow(new ResourceNotFoundException(("Post with id [%d] does not exist".formatted(postId))));

        //when
        //then
        MvcResult mvcResult = mockMvc.perform(get(END_POINT_PATH + "/post/" + postId).contentType(MediaType.APPLICATION_JSON)
                        .param("size", "2").param("page", "0"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors[0]", is("Post with id [%d] does not exist".formatted(postId))))
                .andDo(print())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString()).doesNotContain("_embedded");
    }

    @Test
    public void test_get_tags_by_post_should_return_200_ok() throws Exception {
        //given
        Long postId = 1L;
        Tag tag1 = Tag.builder()
                .id(1L)
                .name("Tag 1").build();
        Tag tag2 = Tag.builder()
                .id(2L)
                .name("Tag 2").build();
        Tag tag3 = Tag.builder()
                .id(3L)
                .name("Tag 3").build();
        Pageable pageable = PageRequest.of(0, 2);
        Page<Tag> pageTag = new PageImpl<>(List.of(tag1, tag2, tag3), pageable, 3);
        when(tagService.getTagsForPostAsPage(postId, pageable)).thenReturn(pageTag);

        //when
        //then
        mockMvc.perform(get(END_POINT_PATH + "/post/" + postId).contentType(MediaType.APPLICATION_JSON)
                        .param("size", "2").param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$._embedded.tags[0].name", is("Tag 1")))
                .andExpect(jsonPath("$._embedded.tags[1].name", is("Tag 2")))
                .andExpect(jsonPath("$._embedded.tags[0]._links.self.href", is("http://localhost/api/v1/tags/1")))
                .andExpect(jsonPath("$._embedded.tags[0]._links.posts.href", is("http://localhost/api/v1/posts/tag/1")))
                .andExpect(jsonPath("$._embedded.tags[1]._links.self.href", is("http://localhost/api/v1/tags/2")))
                .andExpect(jsonPath("$._embedded.tags[1]._links.posts.href", is("http://localhost/api/v1/posts/tag/2")))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/api/v1/tags/post/1?page=0&size=2")))
                .andExpect(jsonPath("$._links.first.href", is("http://localhost/api/v1/tags/post/1?page=0&size=2")))
                .andExpect(jsonPath("$._links.next.href", is("http://localhost/api/v1/tags/post/1?page=1&size=2")))
                .andExpect(jsonPath("$._links.last.href", is("http://localhost/api/v1/tags/post/1?page=1&size=2")))
                .andExpect(jsonPath("$.page.size", is(2)))
                .andExpect(jsonPath("$.page.totalElements", is(3)))
                .andExpect(jsonPath("$.page.totalPages", is(2)))
                .andExpect(jsonPath("$.page.number", is(0)))
                .andDo(print());
    }

    @Test
    public void test_get_tag_by_id_should_return_200() throws Exception {
        //given
        long tagId = 1L;
        Tag tag = Tag.builder()
                .id(tagId)
                .name("Tag 1").build();

        when(tagService.getTagById(tagId)).thenReturn(tag);

        //when
        //then
        mockMvc.perform(get(END_POINT_PATH + "/" + tagId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(tag.getName())))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/api/v1/tags/1")))
                .andExpect(jsonPath("$._links.posts.href", is("http://localhost/api/v1/posts/tag/1")))
                .andDo(print());
    }

    @Test
    public void test_get_tag_by_id_should_throw_404_not_found() throws Exception {
        //given
        long tagId = 1L;

        when(tagService.getTagById(tagId))
                .thenThrow(new ResourceNotFoundException("Tag with id [%d] not found".formatted(tagId)));

        //when
        //then
        mockMvc.perform(get(END_POINT_PATH + "/" + tagId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors[0]", is("Tag with id [%d] not found".formatted(tagId))))
                .andDo(print());

    }

    @Test
    public void test_save_tag_should_return_201_created() throws Exception {
        //given
        TagRequest request = TagRequest.builder()
                .name("New tag name").build();
        Tag tag = Tag.builder()
                .id(1L)
                .name(request.getName()).build();
        when(tagService.save(request)).thenReturn(tag);

        String requestBody = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(post(END_POINT_PATH).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(request.getName())))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/api/v1/tags/1")))
                .andExpect(jsonPath("$._links.posts.href", is("http://localhost/api/v1/posts/tag/1")))
                .andDo(print());
    }

    @Test
    public void test_save_tag_should_throw_400_bad_request_invalid_input() throws Exception {
        //given
        TagRequest request = TagRequest.builder()
                .name("s").build();
        String requestBody = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(post(END_POINT_PATH).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    @Test
    public void test_save_tag_should_throw_404_conflict_name_not_unique() throws Exception {
        //given
        TagRequest request = TagRequest.builder()
                .name("New name for tag").build();
        String requestBody = objectMapper.writeValueAsString(request);
        when(tagService.save(request)).thenThrow(new DuplicateResourceException("Tag with name [%s] already exists".formatted(request.getName())));

        //when
        //then
        mockMvc.perform(post(END_POINT_PATH).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors[0]", is("Tag with name [%s] already exists".formatted(request.getName()))))
                .andDo(print());
    }

    @Test
    public void test_update_tag_should_return_200_ok() throws Exception {
        //given
        long tagId = 1L;
        TagRequest request = TagRequest.builder()
                .name("New tag name").build();
        Tag tag = Tag.builder()
                .id(tagId)
                .name(request.getName()).build();
        when(tagService.update(tagId, request)).thenReturn(tag);

        String requestBody = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(put(END_POINT_PATH + "/" + tagId).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(request.getName())))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/api/v1/tags/1")))
                .andExpect(jsonPath("$._links.posts.href", is("http://localhost/api/v1/posts/tag/1")))
                .andDo(print());
    }

    @Test
    public void test_update_tag_should_throw_400_bad_request_invalid_input() throws Exception {
        //given
        long tagId = 1L;
        TagRequest request = TagRequest.builder()
                .name("s").build();
        String requestBody = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(put(END_POINT_PATH + "/" + tagId).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    @Test
    public void test_update_tag_should_throw_404_conflict_name_not_unique() throws Exception {
        //given
        long tagId = 1L;
        TagRequest request = TagRequest.builder()
                .name("New name for tag").build();
        String requestBody = objectMapper.writeValueAsString(request);
        when(tagService.update(tagId, request)).thenThrow(new DuplicateResourceException("Tag with name [%s] already exists".formatted(request.getName())));

        //when
        //then
        mockMvc.perform(put(END_POINT_PATH + "/" + tagId).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors[0]", is("Tag with name [%s] already exists".formatted(request.getName()))))
                .andDo(print());
    }

    @Test
    public void test_delete_tag_should_return_204_success() throws Exception {
        //given
        long id = 1L;

        //when
        mockMvc.perform(delete(END_POINT_PATH + "/" + id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());

        verify(tagService, times(1)).delete(id);
    }

    @Test
    public void test_delete_tag_should_return_404_not_found() throws Exception {
        //given
        Long tagId = 1L;
        doThrow(ResourceNotFoundException.class).when(tagService).delete(tagId);

        //when
        mockMvc.perform(delete(END_POINT_PATH + "/" + tagId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
}