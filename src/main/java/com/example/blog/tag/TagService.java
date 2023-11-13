package com.example.blog.tag;

import com.example.blog.exception.DuplicateResourceException;
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

    public Tag save(TagRequest request) {
        if (tagRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Tag with name [%s] already exists".formatted(request.getName()));
        }

        Tag tag = Tag.builder()
                .name(request.getName()).build();

        return tagRepository.save(tag);
    }
}
