package com.example.blog.tag;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.domain.Page;

import java.util.Optional;

@NoRepositoryBean
public interface TagRepository {
    Tag save(Tag tag);
    Page<Tag> findAll(Pageable pageable);
    Optional<Tag> findById(Long id);
    Optional<Tag> findByName(String name);
    boolean existsByName(String name);
    void delete(Tag tag);
}
