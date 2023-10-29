package com.example.blog.user;

import com.example.blog.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserRestController.class)
class UserRestControllerTest {

    private static final String END_POINT_PATH = "/api/v1/users";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void test_get_by_id_should_return_200_ok() throws Exception {
        //given
        Long id = 1L;
        User user = User.builder()
                .id(id)
                .email("abc@gmail.com")
                .password("zxc")
                .firstName("ab")
                .lastName("c")
                .phone("1234 56 78").build();
        when(userService.get(id)).thenReturn(user);

        //when
        //then
        mockMvc.perform(get(END_POINT_PATH + "/" + id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.password", is(user.getPassword())))
                .andExpect(jsonPath("$.firstName", is(user.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(user.getLastName())))
                .andExpect(jsonPath("$.phone", is(user.getPhone())))
                .andDo(print());
    }

    @Test
    public void test_get_by_id_should_return_404_not_found() throws Exception {
        //given
        Long id = 100L;
        when(userService.get(id)).thenThrow(ResourceNotFoundException.class);

        //when
        //then
        mockMvc.perform(get(END_POINT_PATH + "/" + id))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
}