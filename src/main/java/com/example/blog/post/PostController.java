package com.example.blog.post;

import com.example.blog.entity.Post;
import com.example.blog.security.CurrentUser;
import com.example.blog.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;
    private final PostModelAssembler postModelAssembler;
    private final DetailedPostModelAssembler detailedPostModelAssembler;
    private final PagedResourcesAssembler<Post> pagedResourcesAssembler;


    public PostController(PostService postService,
                          PostModelAssembler postModelAssembler,
                          DetailedPostModelAssembler detailedPostModelAssembler,
                          PagedResourcesAssembler<Post> pagedResourcesAssembler) {
        this.postService = postService;
        this.postModelAssembler = postModelAssembler;
        this.detailedPostModelAssembler = detailedPostModelAssembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @GetMapping
    public ResponseEntity<PagedModel<PostResponse>> getPostsAsPage(@PageableDefault(size = 5) Pageable pageable) {
        Page<Post> postPage = postService.getPostsAsPage(pageable);

        if (postPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(PagedModel.empty());
        }

        return ResponseEntity.ok(pagedResourcesAssembler.toModel(postPage, postModelAssembler));
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<?> getPostsByCategory(@PathVariable("id") Long categoryId,
                                                @PageableDefault(size = 5) Pageable pageable) {
        Page<Post> postPage = postService.getPostsByCategoryId(categoryId, pageable);

        if (postPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(PagedModel.empty());
        }

        return ResponseEntity.ok(pagedResourcesAssembler.toModel(postPage, postModelAssembler));
    }

    @GetMapping("/tag/{id}")
    public ResponseEntity<PagedModel<PostResponse>> getPostsByTag(@PathVariable("id") Long tagId,
                                                                  @PageableDefault(size = 5) Pageable pageable) {
        Page<Post> postPage = postService.getPostsByTagId(tagId, pageable);

        if (postPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(PagedModel.empty());
        }

        return ResponseEntity.ok(pagedResourcesAssembler.toModel(postPage, postModelAssembler));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<PagedModel<PostResponse>> getPostsByUser(@PathVariable("id") Long userId,
                                                                   @PageableDefault(size = 5) Pageable pageable) {
        Page<Post> postPage = postService.getPostsByUserId(userId, pageable);

        if (postPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(PagedModel.empty());
        }

        return ResponseEntity.ok(pagedResourcesAssembler.toModel(postPage, postModelAssembler));
    }

    @GetMapping("{id}")
    public ResponseEntity<PostResponse> getById(@PathVariable("id") Long id) {
        Post post = postService.getPostById(id);

        return ResponseEntity.ok(detailedPostModelAssembler.toModel(post));
    }

    @PostMapping
    public ResponseEntity<PostResponse> save(@Valid @RequestBody PostRequest request,
                                             @CurrentUser UserPrincipal currentUser) {
        Post post = postService.save(request, currentUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(detailedPostModelAssembler.toModel(post));
    }

    @PutMapping("{id}")
    public ResponseEntity<PostResponse> update(@PathVariable("id") Long id,
                                               @Valid @RequestBody PostRequest request,
                                               @CurrentUser UserPrincipal currentUser) {
        Post post = postService.update(id, request, currentUser);

        return ResponseEntity.ok(detailedPostModelAssembler.toModel(post));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id,
                                         @CurrentUser UserPrincipal currentUser) {
        postService.delete(id, currentUser);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
}
