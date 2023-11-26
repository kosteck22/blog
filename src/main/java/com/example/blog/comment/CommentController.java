package com.example.blog.comment;

import com.example.blog.entity.Comment;
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

@RestController
@RequestMapping("/api/v1/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;
    private final CommentModelAssembler commentModelAssembler;
    private final PagedResourcesAssembler<Comment> pagedResourcesAssembler;

    public CommentController(CommentService commentService, CommentModelAssembler commentModelAssembler, PagedResourcesAssembler<Comment> pagedResourcesAssembler) {
        this.commentService = commentService;
        this.commentModelAssembler = commentModelAssembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @GetMapping
    public ResponseEntity<PagedModel<CommentModel>> getCommentsForPostAsPage(
            @PathVariable("postId") Long postId,
            @PageableDefault(size = 5) Pageable pageable
    ) {
        Page<Comment> commentPage = commentService.getCommentsAsPage(postId, pageable);

        if (commentPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(PagedModel.empty());
        }

        return ResponseEntity.ok(pagedResourcesAssembler.toModel(commentPage, commentModelAssembler));
    }

    @GetMapping("{id}")
    public ResponseEntity<CommentModel> get(@PathVariable("postId") Long postId,
                                            @PathVariable("id") Long commentId) {
        Comment comment = commentService.getById(postId, commentId);

        return ResponseEntity.ok(commentModelAssembler.toModel(comment));
    }

    @PostMapping
    public ResponseEntity<CommentModel> save(@PathVariable("postId") Long postId,
                                             @Valid @RequestBody CommentRequest request,
                                             @CurrentUser UserPrincipal currentUser) {
        Comment comment = commentService.save(postId, request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentModelAssembler.toModel(comment));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable("postId") Long postId,
                                    @PathVariable("id") Long commentId,
                                    @CurrentUser UserPrincipal currentUser) {
        commentService.delete(postId, commentId, currentUser);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @PutMapping("{id}")
    public ResponseEntity<CommentModel> update(@PathVariable("postId") Long postId,
                                               @PathVariable("id") Long commentId,
                                               @Valid @RequestBody CommentRequest request,
                                               @CurrentUser UserPrincipal currentUser) {
        Comment comment = commentService.update(postId, commentId, request, currentUser);

        return ResponseEntity.ok(commentModelAssembler.toModel(comment));
    }
}
