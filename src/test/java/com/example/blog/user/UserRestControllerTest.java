package com.example.blog.user;

import com.example.blog.comment.CommentMapper;
import com.example.blog.comment.CommentModelAssembler;
import com.example.blog.entity.User;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.security.JwtAuthenticationTokenFilter;
import com.example.blog.tag.TagMapper;
import com.example.blog.tag.TagModelAssembler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({UserModelAssembler.class, CommentModelAssembler.class, UserMapper.class, CommentMapper.class})
class UserRestControllerTest {

    private static final String END_POINT_PATH = "/api/v1/users";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtAuthenticationTokenFilter filter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void test_get_by_id_should_return_200_ok() throws Exception {
        //given
        Long id = 1L;
        User user = User.builder()
                .id(id)
                .email("abc@gmail.com")
                .username("qwe")
                .password("zxc")
                .firstName("ab")
                .lastName("c")
                .phone("1234 56 78").build();
        when(userService.getById(id)).thenReturn(user);

        //when
        //then
        mockMvc.perform(get(END_POINT_PATH + "/" + id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.firstName", is(user.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(user.getLastName())))
                .andExpect(jsonPath("$.phone", is(user.getPhone())))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/api/v1/users/1")))
                .andExpect(jsonPath("$._links.posts.href", is("http://localhost/api/v1/posts/user/1")))
                .andExpect(jsonPath("$._links.comments.href", is("http://localhost/api/v1/users/me/comments")))
                .andDo(print());
    }

    @Test
    public void test_get_by_id_should_return_404_not_found() throws Exception {
        //given
        Long id = 100L;
        when(userService.getById(id))
                .thenThrow(new ResourceNotFoundException("user with id [%d] not found".formatted(id)));

        //when
        //then
        mockMvc.perform(get(END_POINT_PATH + "/" + id))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors[0]", is("user with id [%d] not found".formatted(id))))
                .andExpect(jsonPath("$.statusCode",is(404)))
                .andDo(print());
    }

    @Test
    public void test_add_user_should_return_400_bad_request_because_all_fields_invalid() throws Exception {
        //given
        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .email("asdjjdaj")
                .password("zxc")
                .firstName("a")
                .lastName("")
                .username("a")
                .phone("123")
                .build();
        String body = objectMapper.writeValueAsString(request);
        //when
        //then
        MvcResult mvcResult = mockMvc.perform(post(END_POINT_PATH).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();

        assertThat(responseBody).contains("email: must be a well-formed email address");
        assertThat(responseBody).contains("password: Minimum eight characters, at least one uppercase letter, one lowercase letter, one number and one special character");
        assertThat(responseBody).contains("username: Size must be between 3 and 64");
        assertThat(responseBody).contains("firstName: Size must be between 2 and 64");
        assertThat(responseBody).contains("phone: Size must be between 7 and 20");
        assertThat(responseBody).contains("lastName: Size must be between 2 and 64");
    }

    @Test
    public void test_add_user_should_return_201_created() throws Exception {
        //given
        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .email("asd@gmail.com")
                .password("Zxcqwe1!")
                .firstName("zxc")
                .lastName("asd")
                .username("zxc asd")
                .phone("1234 56 78")
                .build();
        User user = User.builder()
                .id(1L)
                .email("asd@gmail.com")
                .password("Zxcqwe1!")
                .firstName("zxc")
                .lastName("asd")
                .username("zxc asd")
                .phone("1234 56 78")
                .build();
        String body = objectMapper.writeValueAsString(request);
        when(userService.addUser(request)).thenReturn(user);

        //when
        //then
        mockMvc.perform(post(END_POINT_PATH).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.firstName", is(user.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(user.getLastName())))
                .andExpect(jsonPath("$.phone", is(user.getPhone())))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/api/v1/users/1")))
                .andExpect(jsonPath("$._links.posts.href", is("http://localhost/api/v1/posts/user/1")))
                .andExpect(jsonPath("$._links.comments.href", is("http://localhost/api/v1/users/me/comments")))
                .andDo(print());
    }

    @Test
    public void test_get_by_email_should_return_200_ok() throws Exception {
        //given
        String email = "zxc@gmail.com";
        User user = User.builder()
                .id(2L)
                .email(email)
                .username("qwe")
                .password("zxc")
                .firstName("ab")
                .lastName("c")
                .phone("1234 56 78").build();
        when(userService.getByEmail(email)).thenReturn(user);

        //when
        //then
        mockMvc.perform(get(END_POINT_PATH + "/identities/email/" + email))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.email", is(email)))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.firstName", is(user.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(user.getLastName())))
                .andExpect(jsonPath("$.phone", is(user.getPhone())))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/api/v1/users/2")))
                .andExpect(jsonPath("$._links.posts.href", is("http://localhost/api/v1/posts/user/2")))
                .andExpect(jsonPath("$._links.comments.href", is("http://localhost/api/v1/users/me/comments")))
                .andDo(print());
    }

    @Test
    public void test_get_by_email_should_return_404_not_found() throws Exception {
        //given
        String email = "zxc@gmail.com";
        when(userService.getByEmail(email))
                .thenThrow(new ResourceNotFoundException("user with email [%s] doesn't exists".formatted(email)));

        //when
        //then
        mockMvc.perform(get(END_POINT_PATH + "/identities/email/" + email))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0]", is("user with email [%s] doesn't exists".formatted(email))))
                .andExpect(jsonPath("$.statusCode",is(404)))
                .andDo(print());
    }

    @Test
    public void test_get_by_username_should_return_200_ok() throws Exception {
        //given
        String username = "zxc";
        User user = User.builder()
                .id(2L)
                .email("zxc@gmail.com")
                .username(username)
                .password("zxc")
                .firstName("ab")
                .lastName("c")
                .phone("1234 56 78").build();
        when(userService.getByUsername(username)).thenReturn(user);

        //when
        //then
        mockMvc.perform(get(END_POINT_PATH + "/identities/username/" + username))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.username", is(username)))
                .andExpect(jsonPath("$.firstName", is(user.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(user.getLastName())))
                .andExpect(jsonPath("$.phone", is(user.getPhone())))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/api/v1/users/2")))
                .andExpect(jsonPath("$._links.posts.href", is("http://localhost/api/v1/posts/user/2")))
                .andExpect(jsonPath("$._links.comments.href", is("http://localhost/api/v1/users/me/comments")))
                .andDo(print());
    }

    @Test
    public void test_get_by_username_should_return_404_not_found() throws Exception {
        //given
        String username = "zxc@gmail.com";
        when(userService.getByUsername(username))
                .thenThrow(new ResourceNotFoundException("user with username [%s] doesn't exists".formatted(username)));

        //when
        //then
        mockMvc.perform(get(END_POINT_PATH + "/identities/username/" + username))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0]", is("user with username [%s] doesn't exists".formatted(username))))
                .andExpect(jsonPath("$.statusCode",is(404)))
                .andDo(print());
    }

    @Test
    public void test_remove_admin_role_should_return_200_ok() throws Exception {
        //given
        long userId = 2L;
        User user = User.builder()
                .id(userId)
                .email("zxc@gmail.com")
                .username("userqwe")
                .password("zxc")
                .firstName("ab")
                .lastName("c")
                .phone("1234 56 78").build();
        when(userService.removeAdminRole(userId)).thenReturn(user);

        //when
        //then
        mockMvc.perform(put(END_POINT_PATH + "/" + userId + "/remove-admin-role"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.firstName", is(user.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(user.getLastName())))
                .andExpect(jsonPath("$.phone", is(user.getPhone())))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/api/v1/users/2")))
                .andExpect(jsonPath("$._links.posts.href", is("http://localhost/api/v1/posts/user/2")))
                .andExpect(jsonPath("$._links.comments.href", is("http://localhost/api/v1/users/me/comments")))
                .andDo(print());
    }

    @Test
    public void test_remove_admin_role_should_return_404_not_found() throws Exception {
        //given
        long userId = 2L;
        when(userService.removeAdminRole(userId))
                .thenThrow(new ResourceNotFoundException("user with id [%d] not found".formatted(userId)));

        //when
        //then
        mockMvc.perform(put(END_POINT_PATH + "/" + userId + "/remove-admin-role"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0]", is("user with id [%d] not found".formatted(userId))))
                .andExpect(jsonPath("$.statusCode",is(404)))
                .andDo(print());
    }
    @Test
    public void test_add_admin_role_should_return_200_ok() throws Exception {
        //given
        long userId = 2L;
        User user = User.builder()
                .id(userId)
                .email("zxc@gmail.com")
                .username("userqwe")
                .password("zxc")
                .firstName("ab")
                .lastName("c")
                .phone("1234 56 78").build();
        when(userService.addAdminRole(userId)).thenReturn(user);

        //when
        //then
        mockMvc.perform(put(END_POINT_PATH + "/" + userId + "/promote-to-admin"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.firstName", is(user.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(user.getLastName())))
                .andExpect(jsonPath("$.phone", is(user.getPhone())))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/api/v1/users/2")))
                .andExpect(jsonPath("$._links.posts.href", is("http://localhost/api/v1/posts/user/2")))
                .andExpect(jsonPath("$._links.comments.href", is("http://localhost/api/v1/users/me/comments")))
                .andDo(print());
    }

    @Test
    public void test_add_admin_role_should_return_404_not_found() throws Exception {
        //given
        long userId = 2L;
        when(userService.addAdminRole(userId))
                .thenThrow(new ResourceNotFoundException("user with id [%d] not found".formatted(userId)));

        //when
        //then
        mockMvc.perform(put(END_POINT_PATH + "/" + userId + "/promote-to-admin"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0]", is("user with id [%d] not found".formatted(userId))))
                .andExpect(jsonPath("$.statusCode",is(404)))
                .andDo(print());
    }
}