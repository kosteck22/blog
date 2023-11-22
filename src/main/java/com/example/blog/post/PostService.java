package com.example.blog.post;

import com.example.blog.category.Category;
import com.example.blog.category.CategoryRepository;
import com.example.blog.exception.CustomAuthorizationException;
import com.example.blog.exception.DuplicateResourceException;
import com.example.blog.exception.RequestValidationException;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.role.AppRoles;
import com.example.blog.security.UserPrincipal;
import com.example.blog.tag.Tag;
import com.example.blog.tag.TagRepository;
import com.example.blog.user.User;
import com.example.blog.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository,
                       TagRepository tagRepository,
                       CategoryRepository categoryRepository,
                       UserRepository userRepository) {
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
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
    public Post save(PostRequest request, UserPrincipal currentUser) {
        validatePostRequest(request);
        User user = getUserByEmail(currentUser.getEmail());
        Category category = getCategoryById(request.getCategoryId());
        Set<Tag> tags = getOrCreateTags(request.getTags());
        Post post = buildPost(request, category, tags, user);

        return postRepository.save(post);
    }

    @Transactional
    public Post update(Long id, PostRequest request, UserPrincipal currentUser) {
        Post existingPost = getPostById(id);
        Category category = getCategoryById(request.getCategoryId());

        if (titleAlreadyTaken(id, request.getTitle())) {
            throw new RequestValidationException("Title [%s] already taken".formatted(request.getTitle()));
        }

        hasAuthorizationForUpdateOrDeletePost(existingPost, currentUser);

        Set<Tag> tags = getOrCreateTags(request.getTags());

        existingPost.setTitle(request.getTitle());
        existingPost.setCategory(category);
        existingPost.setBody(request.getBody());
        existingPost.setTags(tags);

        return postRepository.save(existingPost);
    }

    public void delete(Long id, UserPrincipal currentUser) {
        Post post = getPostById(id);
        hasAuthorizationForUpdateOrDeletePost(post, currentUser);

        postRepository.delete(post);
    }

    private void hasAuthorizationForUpdateOrDeletePost(Post post, UserPrincipal currentUser) {
        if (!currentUser.getId().equals(post.getUser().getId()) ||
                !currentUser.getAuthorities().contains(new SimpleGrantedAuthority(AppRoles.ROLE_ADMIN.toString()))) {
            throw new CustomAuthorizationException("You don't have permission to make this request");
        }
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

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not logged in"));
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