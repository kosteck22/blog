package com.example.blog.tag;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {
    private final TagRepository tagRepository;

    public TagService(@Qualifier("tag-jpa") TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public Page<Tag> getTagsAsPage(Pageable pageable) {
        return tagRepository.findAll(pageable);
    }
}
