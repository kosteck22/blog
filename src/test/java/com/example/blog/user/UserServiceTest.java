package com.example.blog.user;

import com.example.blog.entity.User;
import com.example.blog.exception.DuplicateResourceException;
import com.example.blog.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    private UserService underTest;

    @BeforeEach
    public void setUp() {
        underTest = new UserService(userRepository, null, null, null);
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
        User actual = underTest.getById(id);

        //then
        assertThat(actual).isEqualTo(expected);
        assertThat(actual.getId()).isEqualTo(id);
    }

    @Test
    public void test_get_user_by_id_throws_resource_not_found_exception() {
        //given
        Long id = 10L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> underTest.getById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("user with id [%d] not found".formatted(id));
    }

    @Test
    public void test_save_user() {
        //given
        String email = "abc@gmail.com";
        String username = "abc des";
        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .email(email)
                .password("Abcxa1!")
                .firstName("abc")
                .lastName("des")
                .username(username)
                .phone("1234 56 78")
                .build();

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.existsByUsername(username)).thenReturn(false);

        //when
        underTest.addUser(request);

        //then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userArgumentCaptor.capture());

        User capturedUser = userArgumentCaptor.getValue();

        assertThat(capturedUser.getId()).isNull();
        assertThat(capturedUser.getUsername()).isEqualTo(request.getUsername());
        assertThat(capturedUser.getEmail()).isEqualTo(request.getEmail());
        assertThat(capturedUser.getPassword()).isEqualTo(request.getPassword());
        assertThat(capturedUser.getFirstName()).isEqualTo(request.getFirstName());
        assertThat(capturedUser.getLastName()).isEqualTo(request.getLastName());
        assertThat(capturedUser.getPhone()).isEqualTo(request.getPhone());
    }

    @Test
    public void test_save_user_throws_duplicate_resource_exception_same_email() {
        //given
        String email = "abc@gmail.com";
        String username = "abc des";
        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .email(email)
                .password("Abcxa1!")
                .firstName("abc")
                .lastName("des")
                .username(username)
                .phone("1234 56 78")
                .build();
        when(userRepository.existsByEmail(email)).thenReturn(true);

        //when
        assertThatThrownBy(() -> underTest.addUser(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already taken");

        //then
        verify(userRepository, never()).save(any());
    }

    @Test
    public void test_save_user_throws_duplicate_resource_exception_same_username() {
        //given
        String email = "abc@gmail.com";
        String username = "abc des";
        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .email(email)
                .password("Abcxa1!")
                .firstName("abc")
                .lastName("des")
                .username(username)
                .phone("1234 56 78")
                .build();
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.existsByUsername(username)).thenReturn(true);

        //when
        assertThatThrownBy(() -> underTest.addUser(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("username already taken");

        //then
        verify(userRepository, never()).save(any());
    }

    @Test
    public void test_get_user_by_email_throws_resource_not_found_exception() {
        //given
        String email = "zxc@gmail.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> underTest.getByEmail(email))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("user with email [%s] doesn't exists".formatted(email));
    }

    @Test
    public void test_get_user_by_email_success() {
        //given
        String email = "zxc@gmail.com";
        User expected = User.builder()
                .id(1L)
                .email(email)
                .firstName("ab")
                .lastName("c")
                .username("ab c")
                .phone("1234 56 78")
                .password("zxc").build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(expected));

        //when
        User actual = underTest.getByEmail(email);

        //then
        assertThat(actual).isEqualTo(expected);
        assertThat(actual.getEmail()).isEqualTo(email);
    }

    @Test
    public void test_get_user_by_username_throws_resource_not_found_exception() {
        //given
        String username = "zxc vb";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> underTest.getByUsername(username))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("user with username [%s] doesn't exists".formatted(username));
    }

    @Test
    public void test_get_user_by_username_success() {
        //given
        String username = "zxc vb";
        User expected = User.builder()
                .id(1L)
                .email("zxc@gmail.com")
                .firstName("ab")
                .lastName("c")
                .username(username)
                .phone("1234 56 78")
                .password("zxc").build();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(expected));

        //when
        User actual = underTest.getByUsername(username);

        //then
        assertThat(actual).isEqualTo(expected);
        assertThat(actual.getUsername()).isEqualTo(username);
    }
}