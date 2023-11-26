package com.example.blog.post;

import com.example.blog.entity.Post;
import com.example.blog.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("post-jpa")
public interface PostRepositoryJpa extends PostRepository, JpaRepository<Post, Long> {
    boolean existsByTitle(String title);
    Optional<Post> findByTitle(String title);
    Page<Post> findByTagsIn(List<Tag> tags, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.user.id IN :ids")
    Page<Post> findByUsersIn(@Param("ids") List<Long> usersIds, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.category.id IN :ids")
    Page<Post> findByCategoriesIn(@Param("ids") List<Long> categoriesIds, Pageable pageable);
}
