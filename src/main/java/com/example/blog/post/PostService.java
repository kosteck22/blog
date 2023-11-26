package com.example.blog.post;

import com.example.blog.auth.AuthorizationService;
import com.example.blog.entity.Category;
import com.example.blog.category.CategoryRepository;
import com.example.blog.entity.Post;
import com.example.blog.exception.DuplicateResourceException;
import com.example.blog.exception.RequestValidationException;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.security.UserPrincipal;
import com.example.blog.entity.Tag;
import com.example.blog.tag.TagRepository;
import com.example.blog.entity.User;
import com.example.blog.user.UserRetrievalService;
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
    private final UserRetrievalService userRetrievalService;
    private final AuthorizationService authorizationService;

    public PostService(PostRepository postRepository,
                       TagRepository tagRepository,
                       CategoryRepository categoryRepository,
                       UserRetrievalService userRetrievalService,
                       AuthorizationService authorizationService) {
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
        this.categoryRepository = categoryRepository;
        this.userRetrievalService = userRetrievalService;
        this.authorizationService = authorizationService;
    }

    public Page<Post> getPostsAsPage(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post with id [%d] does not exist"
                        .formatted(id)));
    }

    public Page<Post> getPostsByTagId(Long tagId, Pageable pageable) {
        Tag tag = getTagById(tagId);
        return postRepository.findByTagsIn(Collections.singletonList(tag), pageable);
    }

    public Page<Post> getPostsByCategoryId(Long categoryId, Pageable pageable) {
        Category category = getCategoryById(categoryId);
        return postRepository.findByCategoriesIn(Collections.singletonList(category.getId()), pageable);
    }

    public Page<Post> getPostsByUserId(Long userId, Pageable pageable) {
        User user = getUserById(userId);
        return postRepository.findByUsersIn(Collections.singletonList(user.getId()), pageable);
    }

    @Transactional
    public Post save(PostRequest request, UserPrincipal currentUser) {
        validatePostRequest(request);
        User user = getUser(currentUser);
        Category category = getCategoryById(request.getCategoryId());
        Set<Tag> tags = getOrCreateTags(request.getTags());
        Post post = buildPost(request, category, tags, user);

        return postRepository.save(post);
    }

    @Transactional
    public Post update(Long id, PostRequest request, UserPrincipal currentUser) {
        Post post = getPostById(id);
        Category category = getCategoryById(request.getCategoryId());

        validateTitle(id, request.getTitle());
        hasAuthorizationForUpdateOrDeletePost(post, currentUser);

        Set<Tag> tags = getOrCreateTags(request.getTags());

        post.setTitle(request.getTitle());
        post.setCategory(category);
        post.setBody(request.getBody());
        post.setTags(tags);

        return postRepository.save(post);
    }

    public void delete(Long id, UserPrincipal currentUser) {
        Post post = getPostById(id);
        hasAuthorizationForUpdateOrDeletePost(post, currentUser);

        postRepository.delete(post);
    }

    private void validateTitle(Long id, String title) {
        if (titleAlreadyTaken(id, title)) {
            throw new RequestValidationException("Title [%s] already taken".formatted(title));
        }
    }

    private void hasAuthorizationForUpdateOrDeletePost(Post post, UserPrincipal currentUser) {
        authorizationService.hasAuthorizationForUpdateOrDeleteEntity(post, currentUser);
    }

    private static Post buildPost(PostRequest request, Category category, Set<Tag> tags, User user) {
        return Post.builder()
                .title(request.getTitle())
                .category(category)
                .user(user)
                .tags(tags)
                .body(request.getBody()).build();
    }

    private boolean titleAlreadyTaken(Long postId, String title) {
        return postRepository.findByTitle(title)
                .filter(existingPost -> !existingPost.getId().equals(postId))
                .isPresent();
    }

    private User getUser(UserPrincipal currentUser) {
        return userRetrievalService.getUserByEmail(currentUser.getEmail());
    }

    private User getUserById(Long userId) {
        return userRetrievalService.getUserById(userId);
    }

    private Tag getTagById(Long tagId) {
        return tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag with id [%d] does not exists".formatted(tagId)));
    }

    private Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category with id [%d] does not exists".formatted(categoryId)));
    }

    private Set<Tag> getOrCreateTags(List<String> tagNames) {
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