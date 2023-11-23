package com.example.blog.post;

import com.example.blog.comment.Comment;
import com.example.blog.comment.CommentController;
import com.example.blog.security.CurrentUser;
import com.example.blog.security.UserPrincipal;
import com.example.blog.tag.TagController;
import jakarta.validation.Valid;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
    public ResponseEntity<PagedModel<PostModel>> getPostsAsPage(@PageableDefault(size = 5) Pageable pageable) {
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
    public ResponseEntity<PagedModel<PostModel>> getPostsByTag(@PathVariable("id") Long tagId,
                                                               @PageableDefault(size = 5) Pageable pageable) {
        Page<Post> postPage = postService.getPostsByTagId(tagId, pageable);

        if (postPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(PagedModel.empty());
        }

        return ResponseEntity.ok(pagedResourcesAssembler.toModel(postPage, postModelAssembler));
    }

    @GetMapping("{id}")
    public ResponseEntity<PostModel> getById(@PathVariable("id") Long id) {
        Post post = postService.getPostById(id);

        return ResponseEntity.ok(detailedPostModelAssembler.toModel(post));
    }

    @PostMapping
    public ResponseEntity<PostModel> save(@Valid @RequestBody PostRequest request,
                                          @CurrentUser UserPrincipal currentUser) {
        Post post = postService.save(request, currentUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(detailedPostModelAssembler.toModel(post));
    }

    @PutMapping("{id}")
    public ResponseEntity<PostModel> update(@PathVariable("id") Long id,
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

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        StringTrimmerEditor editor = new StringTrimmerEditor(true);
        binder.registerCustomEditor(String.class, editor);
    }
}
