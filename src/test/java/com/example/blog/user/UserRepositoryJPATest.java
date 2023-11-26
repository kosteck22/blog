package com.example.blog.user;

import com.example.blog.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryJPATest {

    private final UserRepository userRepository;

    public UserRepositoryJPATest(@Qualifier("user-jpa") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Test
    public void test_create_user() {
        //given
        User user = User.builder()
                .email("abc@gmail.com")
                .firstName("ab")
                .lastName("c")
                .password("zxc")
                .username("ab c")
                .phone("1234-56-78").build();

        //when
        User savedUser = userRepository.save(user);

        //then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
    }

    @Test
    public void test_exist_by_email_return_true() {
        //given
        String email = "zxc@gmail.com";
        User user = User.builder()
                .email(email)
                .firstName("zxc")
                .lastName("vb")
                .password("zxc123")
                .username("zxc vb")
                .phone("1234-56-78").build();
        userRepository.save(user);

        //when
        boolean result = userRepository.existsByEmail(email);

        //then
        assertThat(result).isTrue();
    }

    @Test
    public void test_exists_by_email_return_false() {
        //given
        String email = "zxc@gmail.com";

        //when
        boolean result = userRepository.existsByEmail(email);

        //then
        assertThat(result).isFalse();
    }
    @Test
    public void test_exist_by_username_return_true() {
        //given
        String username = "zxc vb";
        User user = User.builder()
                .email("zxc@gmail.com")
                .firstName("zxc")
                .lastName("vb")
                .password("zxc123")
                .username(username)
                .phone("1234-56-78").build();
        userRepository.save(user);

        //when
        boolean result = userRepository.existsByUsername(username);

        //then
        assertThat(result).isTrue();
    }

    @Test
    public void test_exists_by_username_return_false() {
        //given
        String username = "zxc@gmail.com";

        //when
        boolean result = userRepository.existsByUsername(username);

        //then
        assertThat(result).isFalse();
    }

    @Test
    public void test_find_by_email_return_optional_with_user() {
        //given
        String email = "zxc@gmail.com";
        User user = User.builder()
                .email(email)
                .firstName("zxc")
                .lastName("vb")
                .password("zxc123")
                .username("zxc vb")
                .phone("1234-56-78").build();
        userRepository.save(user);

        //when
        Optional<User> result = userRepository.findByEmail(email);

        //then
        assertThat(result).isNotEmpty();

        User userFromDb = result.get();
        assertThat(userFromDb).isInstanceOf(User.class);
        assertThat(userFromDb.getId()).isNotNull();
        assertThat(userFromDb.getEmail()).isEqualTo(email);
    }

    @Test
    public void test_find_by_email_return_empty_optional() {
        //given
        String email = "zxc@gmail.com";

        //when
        Optional<User> result = userRepository.findByEmail(email);

        //then
        assertThat(result).isEmpty();
    }

    @Test
    public void test_find_by_username_return_optional_with_user() {
        //given
        String username = "zxc vb";
        User user = User.builder()
                .email("zxc@gmail.com")
                .firstName("zxc")
                .lastName("vb")
                .password("zxc123")
                .username(username)
                .phone("1234-56-78").build();
        userRepository.save(user);

        //when
        Optional<User> result = userRepository.findByUsername(username);

        //then
        assertThat(result).isNotEmpty();

        User userFromDb = result.get();
        assertThat(userFromDb).isInstanceOf(User.class);
        assertThat(userFromDb.getId()).isNotNull();
        assertThat(userFromDb.getUsername()).isEqualTo(username);
    }

    @Test
    public void test_find_by_username_return_empty_optional() {
        //given
        String username = "zxc vb";

        //when
        Optional<User> result = userRepository.findByUsername(username);

        //then
        assertThat(result).isEmpty();
    }
}