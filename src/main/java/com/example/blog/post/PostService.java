package com.example.blog.post;

import com.example.blog.category.Category;
import com.example.blog.category.CategoryRepository;
import com.example.blog.exception.DuplicateResourceException;
import com.example.blog.exception.RequestValidationException;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.tag.Tag;
import com.example.blog.tag.TagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag with id [%d] does not exists".formatted(tagId)));

        return postRepository.findByTagsIn(List.of(tag), pageable);
    }


    public Page<Post> getPostsByCategoryId(Long categoryId, Pageable pageable) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category with id [%d] does not exists".formatted(categoryId)));

        return postRepository.findByCategoriesIn(List.of(category.getId()), pageable);
    }

    public Post save(PostRequest request) {
        String title = request.getTitle();
        if (postRepository.existsByTitle(title)) {
            throw new DuplicateResourceException("Post with title [%s] already exists".formatted(title));
        }

        Post post = Post.builder()
                .title(title)
                .body(request.getBody()).build();

        return postRepository.save(post);
    }


    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post with id [%d] does not exist".formatted(id)));
    }

    public void delete(Long id) {
        if (!postRepository.existsById(id)) {
            throw new ResourceNotFoundException("Post with id [%d] does not exist".formatted(id));
        }

        postRepository.deleteById(id);
    }

    public Post update(Long id, PostRequest request) {
        Post post = getPostById(id);

        if (titleAlreadyTaken(post.getId(), request.getTitle())) {
            throw new RequestValidationException("Title [%s] already taken".formatted(request.getTitle()));
        }

        post.setTitle(request.getTitle());
        post.setBody(request.getBody());

        return postRepository.save(post);
    }

    private boolean titleAlreadyTaken(Long postId, String title) {
        Optional<Post> postWithRequestTitle = postRepository.findByTitle(title);

        return postWithRequestTitle.filter(value -> !value.getId().equals(postId)).isPresent();
    }
}