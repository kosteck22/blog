package com.example.blog.post;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;
    private final PostModelAssembler postModelAssembler;
    private final PagedResourcesAssembler<Post> pagedResourcesAssembler;


    public PostController(PostService postService, PostModelAssembler postModelAssembler, PagedResourcesAssembler<Post> pagedResourcesAssembler) {
        this.postService = postService;
        this.postModelAssembler = postModelAssembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @GetMapping
    public ResponseEntity<PagedModel<PostModel>> fetchPostWithPagination(@PageableDefault(size = 5) Pageable pageable) {
        Page<Post> postPage = postService.fetchPostDataAsPage(pageable);

        if (postPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(PagedModel.empty());
        }

        return ResponseEntity.ok(pagedResourcesAssembler.toModel(postPage, postModelAssembler));
    }

    @GetMapping("{id}")
    public ResponseEntity<PostModel> getById(@PathVariable("id") Long id) {
        Post post = postService.getById(id);
        return ResponseEntity.ok(postModelAssembler.toModel(post));
    }

    @PostMapping
    public ResponseEntity<Post> save(@Valid @RequestBody PostRequest request) {
        Post post = postService.save(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(post);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id) {
        postService.delete(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
}
