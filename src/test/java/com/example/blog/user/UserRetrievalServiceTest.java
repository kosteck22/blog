package com.example.blog.user;

import com.example.blog.entity.User;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class UserRetrievalServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserRetrievalService underTest;

    @BeforeEach
    public void setUp() {
        underTest = new UserRetrievalService(userRepository);
    }

    @Test
    public void test_get_user_by_email_returns_user() {
        //given
        String email = "qwe@gmail.com";
        User mockedUser = mock(User.class);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockedUser));

        //when
        User result = underTest.getUserByEmail(email);

        //then
        assertThat(result).isEqualTo(mockedUser);
    }

    @Test
    public void test_get_user_by_email_returns_empty_optional() {
        //given
        String email = "qwe@gmail.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> underTest.getUserByEmail(email))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User with email [%s] not found".formatted(email));
    }

    @Test
    public void test_get_user_by_id_returns_user() {
        //given
        long userId = 1L;
        User mockedUser = mock(User.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockedUser));

        //when
        User result = underTest.getUserById(userId);

        //then
        assertThat(result).isEqualTo(mockedUser);
    }

    @Test
    public void test_get_user_by_id_returns_empty_optional() {
        //given
        long userId = 2L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> underTest.getUserById(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User with id [%s] not found".formatted(userId));
    }
}