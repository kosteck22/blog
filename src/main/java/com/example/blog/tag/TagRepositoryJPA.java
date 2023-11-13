package com.example.blog.tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("tag-jpa")
public interface TagRepositoryJPA extends TagRepository, JpaRepository<Tag, Long> {
}
