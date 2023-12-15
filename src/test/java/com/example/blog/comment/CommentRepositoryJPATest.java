package com.example.blog.comment;

import com.example.blog.entity.Comment;
import com.example.blog.entity.Post;
import com.example.blog.entity.User;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CommentRepositoryJPATest {
    @Autowired
    private CommentRepository underTest;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void test_save_comment_success() {
        //given
        String body = "This is comment body 3";

        Post post = Post.builder()
                .id(1L).build();

        Comment comment = Comment.builder()
                .body(body).build();
        comment.setPost(post);

        //when
        Comment result = underTest.save(comment);

        //then
        assertThat(result).isInstanceOf(Comment.class);
        assertThat(result.getId()).isNotNull().isGreaterThan(0);
        assertThat(result.getBody()).isEqualTo(body);
        assertThat(result.getPost().getId()).isEqualTo(1L);
    }

    @Test
    public void test_delete_comment_success() {
        //given
        Comment comment = Comment.builder()
                .body("this is body of the comment").build();
        Comment persisted = entityManager.persist(comment);
        Long commentId = persisted.getId();

        //when
        underTest.delete(persisted);

        //then
        Comment afterDelete = entityManager.find(Comment.class, commentId);

        assertThat(commentId).isNotNull();
        assertThat(persisted).isNotNull();
        assertThat(afterDelete).isNull();;
    }

    @Test
    public void test_should_find_comment_by_id() {
        //given
        Comment comment = Comment.builder()
                .body("this is body of the comment").build();
        Comment persisted = entityManager.persist(comment);
        Long commentId = persisted.getId();

        //when
        Optional<Comment> commentById = underTest.findById(commentId);

        //then
        assertThat(commentById).isPresent();
        assertThat(commentById.get()).isInstanceOf(Comment.class);
        assertThat(commentById.get().getId()).isEqualTo(commentId);
        assertThat(commentById.get()).isEqualTo(persisted);
    }

    @Test
    public void test_should_not_find_comment_by_id() {
        //given
        Long commentId = 100L;

        //when
        Optional<Comment> commentById = underTest.findById(commentId);

        //then
        assertThat(commentById).isEmpty();
    }

    @Test
    public void test_should_find_comments_for_post_id() {
        //given
        Post post = Post.builder()
                .body("Post Body")
                .title("Post Title").build();
        Post savedPost = entityManager.persist(post);
        Comment comment1 = Comment.builder()
                .body("this is body of the comment")
                .post(savedPost).build();
        Comment comment2 = Comment.builder()
                .body("this is body of the comment")
                .post(savedPost).build();

        entityManager.persist(comment1);
        entityManager.persist(comment2);

        //when
        Page<Comment> result = underTest.findAllInPost(savedPost.getId(), PageRequest.of(0, 5));

        //then
        assertThat(result).isNotEmpty();
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent().get(0)).isEqualTo(comment1);
        assertThat(result.getContent().get(1)).isEqualTo(comment2);
    }

    @Test
    public void test_should_find_comments_for_user_id() {
        //given
        User user = User.builder()
                .email("qwe@gmail.com")
                .username("qwe")
                .firstName("qwe")
                .lastName("asd")
                .password("Qqqwejk1!JE")
                .phone("123456789").build();
        User savedUser = entityManager.persist(user);

        Comment comment1 = Comment.builder()
                .body("this is body of the comment")
                .user(savedUser).build();
        Comment comment2 = Comment.builder()
                .body("this is body of the comment")
                .user(savedUser).build();

        entityManager.persist(comment1);
        entityManager.persist(comment2);

        //when
        Page<Comment> result = underTest.findAllInUser(savedUser.getId(), PageRequest.of(0, 5));

        //then
        assertThat(result).isNotEmpty();
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent().get(0)).isEqualTo(comment1);
        assertThat(result.getContent().get(1)).isEqualTo(comment2);
    }
}