package com.example.blog.tag;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.domain.Page;

@NoRepositoryBean
public interface TagRepository {
    Tag save(Tag tag);
    Page<Tag> findAll(Pageable pageable);
}
