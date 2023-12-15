package com.example.blog.user;

import com.example.blog.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryJPATest {

    private final UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

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
        String email = "zxc123@gmail.com";
        User user = User.builder()
                .email(email)
                .firstName("zxc")
                .lastName("vb")
                .password("zxc123")
                .username("zxc vb")
                .phone("1234-56-78").build();
        entityManager.persist(user);

        //when
        boolean result = userRepository.existsByEmail(email);

        //then
        assertThat(result).isTrue();
    }

    @Test
    public void test_exists_by_email_return_false() {
        //given
        String email = "zxcgf@gmail.com";

        //when
        boolean result = userRepository.existsByEmail(email);

        //then
        assertThat(result).isFalse();
    }
    @Test
    public void test_exist_by_username_return_true() {
        //given
        String username = "zxcqwe";
        User user = User.builder()
                .email("zxc123@gmail.com")
                .firstName("zxc")
                .lastName("vb")
                .password("zxc123")
                .username(username)
                .phone("1234-56-78").build();
        entityManager.persist(user);

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
        String email = "zxcw@gmail.com";
        User user = User.builder()
                .email(email)
                .firstName("zxcw")
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
        String email = "zxcrq@gmail.com";

        //when
        Optional<User> result = userRepository.findByEmail(email);

        //then
        assertThat(result).isEmpty();
    }

    @Test
    public void test_find_by_username_return_optional_with_user() {
        //given
        String username = "zxcqwe";
        User user = User.builder()
                .email("zxcqwe@gmail.com")
                .firstName("zxc")
                .lastName("vb")
                .password("zxc123")
                .username(username)
                .phone("1234-56-78").build();
        entityManager.persist(user);

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

    @Test
    public void test_find_user_by_email_or_username_method_with_username_input() {
        //given
        User user = User.builder()
                .email("qwe@gmail.com")
                .username("qwe")
                .password("qweQ1!q!E")
                .firstName("qwe")
                .lastName("asd")
                .phone("123456789").build();
        User savedUser = entityManager.persist(user);

        //when
        Optional<User> result = userRepository.findUserByEmailOrUsername(user.getUsername());

        //then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(savedUser);
        assertThat(result.get().getId()).isNotNull();
    }

    @Test
    public void test_find_user_by_email_or_username_method_with_email_input() {
        //given
        User user = User.builder()
                .email("qwe@gmail.com")
                .username("qwe")
                .password("qweQ1!q!E")
                .firstName("qwe")
                .lastName("asd")
                .phone("123456789").build();
        User savedUser = entityManager.persist(user);

        //when
        Optional<User> result = userRepository.findUserByEmailOrUsername(user.getEmail());

        //then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(savedUser);
        assertThat(result.get().getId()).isNotNull();
    }
}