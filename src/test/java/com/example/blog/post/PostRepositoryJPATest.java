package com.example.blog.post;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostRepositoryJPATest {
    private final PostRepository underTest;

    public PostRepositoryJPATest(@Qualifier("post-jpa") PostRepository postRepository) {
        this.underTest = postRepository;
    }

    @Test
    public void test_save_post_success() {
        //given
        String title = "This is title 20";
        String body = "This is body 20";
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

    @Test
    public void test_exists_by_id_success() {
        //given
        String title = "This is title 30";
        String body = "This is body 20";

        Post post = Post.builder()
                .title(title)
                .body(body).build();

        underTest.save(post);

        Long id = underTest.findAll()
                .stream()
                .filter(p -> p.getTitle().equals(title))
                .map(Post::getId)
                .findFirst()
                .orElseThrow();

        //when
        boolean actual = underTest.existsById(id);

        //then
        assertThat(actual).isTrue();
    }

    @Test
    public void test_exists_by_id_fails_when_id_not_present() {
        //given
        Long id = -1L;

        //when
        boolean result = underTest.existsById(id);

        //then
        assertThat(result).isFalse();
    }

    @Test
    public void test_exists_by_title_success() {
        //given
        String title = "This is title 30";
        String body = "This is body 20";

        Post post = Post.builder()
                .title(title)
                .body(body).build();

        underTest.save(post);

        //when
        boolean actual = underTest.existsByTitle(title);

        //then
        assertThat(actual).isTrue();
    }

    @Test
    public void test_exists_by_title_fails_when_id_not_present() {
        //given
        String title = "title example";

        //when
        boolean result = underTest.existsByTitle(title);

        //then
        assertThat(result).isFalse();
    }
}