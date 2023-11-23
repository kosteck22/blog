package com.example.blog.post;

import com.example.blog.category.Category;
import com.example.blog.tag.Tag;
import com.example.blog.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@NoRepositoryBean
public interface PostRepository {
    Post save(Post post);
    Page<Post> findAll(Pageable pageable);
    Page<Post> findByTagsIn(List<Tag> tags, Pageable pageable);
    Page<Post> findByCategoriesIn(List<Long> categoriesIds, Pageable pageable);
    Page<Post> findByUsersIn(List<Long> usersIds, Pageable pageable);
    List<Post> findAll();
    boolean existsByTitle(String title);
    boolean existsById(Long id);
    Optional<Post> findById(Long id);
    Optional<Post> findByTitle(String title);
    void deleteById(Long id);
    void delete(Post post);

}
