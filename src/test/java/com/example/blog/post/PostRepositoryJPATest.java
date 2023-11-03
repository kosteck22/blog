package com.example.blog.post;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
class PostRepositoryJPATest {
    private final PostRepository underTest;

    public PostRepositoryJPATest(@Qualifier("jpa") PostRepository postRepository) {
        this.underTest = postRepository;
    }

    @Test
    public void test_save_post_success() {
        //given
        String title = "This is title";
        String body = "This is body";
        Post post = Post.builder()
                .title(title)
                .body(body).build();

        //when
        Post result = underTest.save(post);

        //then
        assertThat(result).isInstanceOf(Post.class);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getId()).isGreaterThan(0);
        assertThat(result.getTitle()).isEqualTo(title);
        assertThat(result.getBody()).isEqualTo(body);
    }
}