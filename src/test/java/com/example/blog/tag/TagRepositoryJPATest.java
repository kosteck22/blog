package com.example.blog.tag;

import com.example.blog.entity.Post;
import com.example.blog.entity.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.StatusResultMatchersExtensionsKt.isEqualTo;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TagRepositoryJPATest {

    @Autowired
    private TagRepository underTest;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void test_save_tag_success() {
        //given
        String tagName = "Sorting algorithms";
        Tag tag = Tag.builder()
                .name(tagName).build();

        //when
        Tag result = underTest.save(tag);

        //then
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(Tag.class);
        assertThat(result.getId()).isGreaterThan(0);
        assertThat(result.getName()).isEqualTo(tagName);
    }

    @Test
    public void test_find_by_posts_return_page_of_tags() {
        //given
        Post post = Post.builder()
                .title("post title 1")
                .body("post body 1").build();
        Tag tag1 = Tag.builder()
                .name("tag1").build();
        Tag tag2 = Tag.builder()
                .name("tag2").build();
        Tag tag3 = Tag.builder()
                .name("tag3").build();
        post.addTag(tag1);
        post.addTag(tag2);

        entityManager.persist(post);
        entityManager.persist(tag1);
        entityManager.persist(tag2);
        entityManager.persist(tag3);

        //when
        Page<Tag> tagPage = underTest.findByPostsIn(List.of(post), PageRequest.of(0, 5));

        //then
        assertThat(tagPage.getTotalElements()).isEqualTo(2);
        assertThat(tagPage.getContent().contains(tag1)).isTrue();
        assertThat(tagPage.getContent().contains(tag2)).isTrue();
        assertThat(tagPage.getContent().contains(tag3)).isFalse();
    }

    @Test
    public void test_find_orphaned_tags() {
        //given
        Post post = Post.builder()
                .title("post title 1")
                .body("post body 1").build();
        Tag tag1 = Tag.builder()
                .name("tag1").build();
        Tag tag2 = Tag.builder()
                .name("tag2").build();
        Tag tag3 = Tag.builder()
                .name("tag3").build();
        post.addTag(tag1);
        post.addTag(tag2);

        entityManager.persist(post);
        entityManager.persist(tag1);
        entityManager.persist(tag2);
        entityManager.persist(tag3);

        //when
        List<Tag> orphanedTags = underTest.findOrphanedTags();

        //then
        assertThat(orphanedTags.size()).isEqualTo(1);
        assertThat(orphanedTags.get(0)).isEqualTo(tag3);
    }
}