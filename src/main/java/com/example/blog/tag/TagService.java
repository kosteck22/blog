package com.example.blog.tag;

import com.example.blog.exception.DuplicateResourceException;
import com.example.blog.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Tag update(Long tagId, TagRequest request) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag with id [%d] not found".formatted(tagId)));

        String requestName = request.getName();

        if (tagRepository.existsByName(requestName)) {
            throw new DuplicateResourceException("Tag with name [%s] already exists".formatted(requestName));
        }

        tag.setName(requestName);

        return tagRepository.save(tag);
    }
}
