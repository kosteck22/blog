package com.example.blog.tag;

import com.example.blog.entity.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

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
}