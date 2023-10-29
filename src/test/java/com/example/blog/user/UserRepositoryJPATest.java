package com.example.blog.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryJPATest {

    private final UserRepository userRepository;

    public UserRepositoryJPATest(@Qualifier("jpa") UserRepository userRepository) {
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
}