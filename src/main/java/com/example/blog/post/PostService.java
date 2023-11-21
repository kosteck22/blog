package com.example.blog.post;

import com.example.blog.category.Category;
import com.example.blog.category.CategoryRepository;
import com.example.blog.exception.DuplicateResourceException;
import com.example.blog.exception.RequestValidationException;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.tag.Tag;
import com.example.blog.tag.TagRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final CategoryRepository categoryRepository;

    public PostService(PostRepository postRepository,
                       TagRepository tagRepository,
                       CategoryRepository categoryRepository) {
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
        this.categoryRepository = categoryRepository;
    }

    public Page<Post> getPostsAsPage(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    public Page<Post> getPostsByTagId(Long tagId, Pageable pageable) {
        Tag tag = getTagById(tagId);
        return postRepository.findByTagsIn(Collections.singletonList(tag), pageable);
    }

    public Page<Post> getPostsByCategoryId(Long categoryId, Pageable pageable) {
        Category category = getCategoryById(categoryId);
        return postRepository.findByCategoriesIn(Collections.singletonList(category.getId()), pageable);
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post with id [%d] does not exist"
                        .formatted(id)));
    }

    @Transactional
    public Post save(PostRequest request) {
        validatePostRequest(request);
        Category category = getCategoryById(request.getCategoryId());
        Set<Tag> tags = createOrUpdateTags(request.getTags());
        Post post = buildPost(request, category, tags);

        return postRepository.save(post);
    }

    @Transactional
    public Post update(Long id, PostRequest request) {
        Post existingPost = getPostById(id);
        Category category = getCategoryById(request.getCategoryId());

        if (titleAlreadyTaken(id, request.getTitle())) {
            throw new RequestValidationException("Title [%s] already taken".formatted(request.getTitle()));
        }
        Set<Tag> tags = createOrUpdateTags(request.getTags());

        existingPost.setTitle(request.getTitle());
        existingPost.setCategory(category);
        existingPost.setBody(request.getBody());
        existingPost.setTags(tags);

        return postRepository.save(existingPost);
    }

    public void delete(Long id) {
        if (!postRepository.existsById(id)) {
            throw new ResourceNotFoundException("Post with id [%d] does not exist"
                    .formatted(id));
        }
        postRepository.deleteById(id);
    }

    private static Post buildPost(PostRequest request, Category category, Set<Tag> tags) {
        return Post.builder()
                .title(request.getTitle())
                .category(category)
                .tags(tags)
                .body(request.getBody()).build();
    }

    private boolean titleAlreadyTaken(Long postId, String title) {
        return postRepository.findByTitle(title)
                .filter(existingPost -> !existingPost.getId().equals(postId))
                .isPresent();
    }

    private Tag getTagById(Long tagId) {
        return tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag with id [%d] does not exists".formatted(tagId)));
    }

    private Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category with id [%d] does not exists".formatted(categoryId)));
    }

    private Set<Tag> createOrUpdateTags(List<String> tagNames) {
        return tagNames.stream()
                .map(tagName -> tagRepository.findByName(tagName)
                        .orElseGet(() -> tagRepository.save(new Tag(tagName))))
                .collect(Collectors.toSet());
    }

    private void validatePostRequest(PostRequest request) {
        String title = request.getTitle();
        if (postRepository.existsByTitle(title)) {
            throw new DuplicateResourceException("Post with title [%s] already exists".formatted(title));
        }
    }
}