package com.example.blog.auth;

import com.example.blog.exception.DuplicateResourceException;
import com.example.blog.exception.InvalidUsernameOrPasswordException;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.security.JwtAuthenticationTokenFilter;
import com.example.blog.user.UserRegistrationRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {
    private static final String END_POINT_PATH = "/api/v1/auth";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private JwtAuthenticationTokenFilter filter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void test_register_should_return_200() throws Exception {
        //given
        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .email("zxc@gmail.com")
                .username("zxc")
                .password("Qweqwe1!")
                .firstName("qwe")
                .lastName("zxc")
                .phone("123456789").build();

        String requestBody = objectMapper.writeValueAsString(request);

        //when
        //them
        MvcResult mvcResult = mockMvc.perform(post(END_POINT_PATH + "/register").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();

        assertThat(responseBody).isEqualTo("\"http://localhost/api/v1/auth/login\"");
    }

    @Test
    public void test_register_should_throw_400_bad_request_invalid_data() throws Exception {
        //given
        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .email("zxc")
                .username("zc")
                .password("zxc!")
                .firstName("q")
                .lastName("z")
                .phone("123456").build();

        String requestBody = objectMapper.writeValueAsString(request);

        //when
        //them
        MvcResult mvcResult = mockMvc.perform(post(END_POINT_PATH + "/register").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();

        assertThat(responseBody).contains("lastName: Size must be between 2 and 64");
        assertThat(responseBody).contains("phone: Size must be between 7 and 20");
        assertThat(responseBody).contains("email: must be a well-formed email address");
        assertThat(responseBody).contains("firstName: Size must be between 2 and 64");
        assertThat(responseBody).contains("username: Size must be between 3 and 64");
        assertThat(responseBody).contains("password: Minimum eight characters, at least one uppercase letter, one lowercase letter, one number and one special character");
    }

    @Test
    public void test_register_should_throw_400_bad_request_invalid_data_2() throws Exception {
        //given
        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .email(null)
                .username(null)
                .password(null)
                .firstName(null)
                .lastName(null)
                .phone(null).build();

        String requestBody = objectMapper.writeValueAsString(request);

        //when
        //them
        MvcResult mvcResult = mockMvc.perform(post(END_POINT_PATH + "/register").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();

        assertThat(responseBody).contains("email: must not be blank");
        assertThat(responseBody).contains("username: must not be blank");
        assertThat(responseBody).contains("password: must not be blank");
        assertThat(responseBody).contains("lastName: must not be blank");
        assertThat(responseBody).contains("firstName: must not be blank");
        assertThat(responseBody).contains("phone: must not be blank");
    }

    @Test
    public void test_register_should_throw_409_conflict_duplicate_username_or_email() throws Exception {
        //given
        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .email("zxc@gmail.com")
                .username("qwe")
                .password("Qweqe2zxc1!")
                .firstName("qsad")
                .lastName("zfasd")
                .phone("122313456").build();

        String requestBody = objectMapper.writeValueAsString(request);
        doThrow(new DuplicateResourceException("email already taken")).when(authenticationService).registerUser(any());

        //when
        //them
        mockMvc.perform(post(END_POINT_PATH + "/register").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.path", is("/api/v1/auth/register")))
                .andExpect(jsonPath("$.errors[0]", is("email already taken")))
                .andExpect(jsonPath("$.statusCode", is(409)))
                .andDo(print());
    }

    @Test
    public void test_register_should_throw_404_not_found() throws Exception {
        //given
        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .email("zxc@gmail.com")
                .username("qwe")
                .password("Qweqe2zxc1!")
                .firstName("qsad")
                .lastName("zfasd")
                .phone("122313456").build();

        String requestBody = objectMapper.writeValueAsString(request);
        doThrow(new ResourceNotFoundException("Role not found")).when(authenticationService).registerUser(any());

        //when
        //them
        mockMvc.perform(post(END_POINT_PATH + "/register").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.path", is("/api/v1/auth/register")))
                .andExpect(jsonPath("$.errors[0]", is("Role not found")))
                .andExpect(jsonPath("$.statusCode", is(404)))
                .andDo(print());
    }

    @Test
    public void test_login_should_return_200() throws Exception {
        //given
        AuthenticationRequest request = AuthenticationRequest.builder()
                .username("qwe")
                .password("Qwe1234!").build();

        AuthResponse mockedResponse = new AuthResponse("Bearer nzxcm1mNADMSnd1m23nedsma!N@M#edsnam");
        when(authenticationService.login(any(AuthenticationRequest.class))).thenReturn(mockedResponse);
        String requestBody = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(post(END_POINT_PATH + "/login").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.authorizationToken", is("Bearer nzxcm1mNADMSnd1m23nedsma!N@M#edsnam")))
                .andDo(print());
    }

    @Test
    public void test_login_should_throw_400_bad_request_because_invalid_data() throws Exception {
        //given
        AuthenticationRequest request = AuthenticationRequest.builder()
                .username(null)
                .password(null).build();

        String requestBody = objectMapper.writeValueAsString(request);

        //when
        //then
        MvcResult mvcResult = mockMvc.perform(post(END_POINT_PATH + "/login").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();

        assertThat(responseBody).contains("username: must not be blank");
        assertThat(responseBody).contains("password: must not be blank");
    }

    @Test
    public void test_login_should_throw_401_unauthorized() throws Exception {
        //given
        AuthenticationRequest request = AuthenticationRequest.builder()
                .username("qwe")
                .password("Qwe1234!").build();

       doThrow(new InvalidUsernameOrPasswordException("Invalid username/password supplied")).when(authenticationService).login(any());
        String requestBody = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(post(END_POINT_PATH + "/login").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.path", is("/api/v1/auth/login")))
                .andExpect(jsonPath("$.errors[0]", is("Invalid username/password supplied")))
                .andExpect(jsonPath("$.statusCode", is(401)))
                .andDo(print());
    }
}