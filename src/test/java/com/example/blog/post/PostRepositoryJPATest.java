package com.example.blog.post;

import com.example.blog.tag.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
class PostRepositoryJPATest {
    @Autowired
    private PostRepository underTest;

    @Autowired
    private TestEntityManager entityManager;

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

    @Test
    public void test_add_tags_to_post_success() {
        //given
        String tagName = "Multi languages";
        Tag tag1 = Tag.builder()
                .name(tagName).build();

        Tag tag2 = Tag.builder()
                .name("Search algorithms").build();

        Post post = Post.builder()
                .title("Post title 13")
                .body("Body of the post 13").build();

        Tag savedTag1 = entityManager.persist(tag1);
        Tag savedTag2 = entityManager.persist(tag2);

        post.addTag(savedTag1);
        post.addTag(savedTag2);

        //when
        Post result = underTest.save(post);

        //then
        assertThat(result).isInstanceOf(Post.class);
        assertThat(result.getId()).isGreaterThan(0);
        assertThat(result.getTags()).isNotEmpty();
        assertThat(result.getTags().contains(tag1)).isTrue();
        assertThat(result.getTags().contains(tag2)).isTrue();
        assertThat(result.getTags().size()).isEqualTo(2);
    }

    @Test
    public void test_remove_tag_from_post_success() {
        //given
        Tag tag = Tag.builder()
                .name("Players").build();

        Post post = Post.builder()
                .title("Post title 7")
                .body("Body of the post 7").build();

        Tag savedTag = entityManager.persist(tag);

        post.addTag(savedTag);
        Post postBeforeRemoving  = underTest.save(post);

        assertThat(postBeforeRemoving.getTags()).isNotEmpty();
        assertThat(postBeforeRemoving.getTags().contains(savedTag)).isTrue();
        assertThat(postBeforeRemoving.getTags().size()).isEqualTo(1);

        //when
        postBeforeRemoving.removeTag(savedTag);
        Post result = underTest.save(postBeforeRemoving);

        //then
        assertThat(result).isInstanceOf(Post.class);
        assertThat(result.getTags()).isEmpty();
    }
}