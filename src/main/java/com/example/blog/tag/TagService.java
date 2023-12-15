package com.example.blog.tag;

import com.example.blog.entity.Tag;
import com.example.blog.exception.DuplicateResourceException;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.entity.Post;
import com.example.blog.post.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        Post post = getPostById(postId);

        return tagRepository.findByPostsIn(List.of(post), pageable);
    }

    public Tag getTagById(Long tagId) {
        return tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag with id [%d] not found".formatted(tagId)));
    }

    public Tag save(TagRequest request) {
        validateRequest(request);
        Tag tag = Tag.builder()
                .name(request.getName()).build();

        return tagRepository.save(tag);
    }

    public Tag update(Long tagId, TagRequest request) {
        validateRequest(request);
        Tag tag = getTagById(tagId);
        tag.setName(request.getName());

        return tagRepository.save(tag);
    }

    @Transactional
    public void delete(Long tagId) {
        Tag tag = getTagById(tagId);
        removeAssociationWithPosts(tag);

        tagRepository.delete(tag);
    }

    private static void removeAssociationWithPosts(Tag tag) {
        for (Post post : tag.getPosts()) {
            post.removeTag(tag);
        }
    }

    private Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post with id [%d] does not exist".formatted(postId)));
    }

    private void validateRequest(TagRequest request) {
        validateTagName(request.getName());
    }

    private void validateTagName(String requestName) {
        if (tagRepository.existsByName(requestName)) {
            throw new DuplicateResourceException("Tag with name [%s] already exists".formatted(requestName));
        }
    }
}
