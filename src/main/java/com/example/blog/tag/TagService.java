package com.example.blog.tag;

import com.example.blog.exception.DuplicateResourceException;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.post.Post;
import com.example.blog.post.PostRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TagService {
    private final TagRepository tagRepository;

    private final PostRepository postRepository;

    public TagService(TagRepository tagRepository, PostRepository postRepository) {
        this.tagRepository = tagRepository;
        this.postRepository = postRepository;
    }

    public Page<Tag> getTagsAsPage(Pageable pageable) {
        return tagRepository.findAll(pageable);
    }

    public Page<Tag> getTagsForPostAsPage(Long postId, Pageable pageable) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post with id [%d] does not exist".formatted(postId)));

        return tagRepository.findByPostsIn(List.of(post), pageable);
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
        Tag tag = get(tagId);

        String requestName = request.getName();

        if (tagRepository.existsByName(requestName)) {
            throw new DuplicateResourceException("Tag with name [%s] already exists".formatted(requestName));
        }

        tag.setName(requestName);

        return tagRepository.save(tag);
    }

    public Tag get(Long tagId) {
        return tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag with id [%d] not found".formatted(tagId)));
    }

    public void delete(Long tagId) {
        Tag tag = get(tagId);

        tagRepository.delete(tag);
    }
}
