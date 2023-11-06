package com.example.blog.post;

import com.example.blog.exception.DuplicateResourceException;
import com.example.blog.exception.RequestValidationException;
import com.example.blog.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
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

    public Page<Post> fetchPostDataAsPage(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    public Post getById(Long id) {
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
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post with id [%d] does not exist".formatted(id)));

        if (titleAlreadyTaken(post, request.getTitle())) {
            throw new RequestValidationException("Title [%s] already taken".formatted(request.getTitle()));
        }

        post.setTitle(request.getTitle());
        post.setBody(request.getBody());

        return postRepository.save(post);
    }

    private boolean titleAlreadyTaken(Post post, String title) {
        Optional<Post> postWithRequestTitle = postRepository.findByTitle(title);

        return postWithRequestTitle.filter(value -> !value.getId().equals(post.getId())).isPresent();
    }
}