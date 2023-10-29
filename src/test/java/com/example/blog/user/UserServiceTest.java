package com.example.blog.user;

import com.example.blog.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    private UserService underTest;

    @BeforeEach
    public void setUp() {
        underTest = new UserService(userRepository);
    }

    @Test
    public void test_get_user_by_id_success() {
        //given
        Long id = 1L;
        User expected = User.builder()
                .id(id)
                .email("abc@gmail.com")
                .firstName("ab")
                .lastName("c")
                .username("ab c")
                .phone("1234 56 78")
                .password("zxc").build();
        when(userRepository.findById(id)).thenReturn(Optional.of(expected));

        //when
        User actual = underTest.get(id);

        //then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void test_get_user_by_id_throws_resource_not_found_exception() {
        //given
        Long id = 10L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> underTest.get(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User with id [%d] not found".formatted(id));
    }

    @Test
    public void test_save_user() {
        //given
        User user = User.builder()
                .email("abc@gmail.com")
                .firstName("ab")
                .lastName("c")
                .username("ab c")
                .phone("1234 56 78")
                .password("zxc").build();
        //when
        underTest.save(user);
        //then
        verify(userRepository).save(user);
    }
}