package com.example.blog.comment;

import com.example.blog.auth.AuthorizationService;
import com.example.blog.entity.Comment;
import com.example.blog.exception.RequestValidationException;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.entity.Post;
import com.example.blog.post.PostRepository;
import com.example.blog.security.UserPrincipal;
import com.example.blog.entity.User;
import com.example.blog.user.UserRetrievalService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRetrievalService userRetrievalService;
    private final AuthorizationService authorizationService;

    public CommentService(CommentRepository commentRepository,
                          PostRepository postRepository,
                          UserRetrievalService userRetrievalService,
                          AuthorizationService authorizationService) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRetrievalService = userRetrievalService;
        this.authorizationService = authorizationService;
    }

    public Page<Comment> getCommentsAsPage(Long postId, Pageable pageable) {
        checkIfPostWithGivenIdExists(postId);

        return commentRepository.findAllInPost(postId, pageable);
    }

    public Comment getById(Long postId, Long commentId) {
        Post post = getPostById(postId);
        Comment comment = getCommentById(commentId);

        commentBelongToPost(postId, comment, post);

        return comment;
    }

    @Transactional
    public Comment save(Long postId, CommentRequest request, UserPrincipal currentUser) {
        Post post = getPostById(postId);
        User user = getUser(currentUser);

        Comment comment = Comment.builder()
                .body(request.getBody())
                .user(user)
                .post(post).build();

        return commentRepository.save(comment);
    }

    @Transactional
    public Comment update(Long postId, Long commentId, CommentRequest request, UserPrincipal currentUser) {
        Post post = getPostById(postId);
        Comment comment = getCommentById(commentId);

        commentBelongToPost(postId, comment, post);
        hasAuthorizationForUpdateOrDeleteEntity(comment, currentUser);

        comment.setBody(request.getBody());

        return commentRepository.save(comment);
    }

    @Transactional
    public void delete(Long postId, Long commentId, UserPrincipal currentUser) {
        Post post = getPostById(postId);
        Comment comment = getCommentById(commentId);
        commentBelongToPost(postId, comment, post);
        hasAuthorizationForUpdateOrDeleteEntity(comment, currentUser);

        commentRepository.delete(comment);
    }

    private void checkIfPostWithGivenIdExists(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("Post with id [%d] does not exists".formatted(postId));
        }
    }

    private User getUser(UserPrincipal currentUser) {
        return userRetrievalService.getUserByEmail(currentUser.getEmail());
    }

    private void commentBelongToPost(Long postId, Comment comment, Post post) {
        if (commentDoesNotBelongToPost(comment, post)) {
            throw new RequestValidationException("Comment does not belong to post with id [%d]".formatted(postId));
        }
    }

    private Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment with id [%d] does not exist".formatted(commentId)));
    }

    private Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post with id [%d] does not exist".formatted(postId)));
    }

    private boolean commentDoesNotBelongToPost(Comment comment, Post post) {
        return !post.getId().equals(comment.getPost().getId());
    }

    private void hasAuthorizationForUpdateOrDeleteEntity(Comment comment, UserPrincipal currentUser) {
        authorizationService.hasAuthorizationForUpdateOrDeleteEntity(comment, currentUser);
    }
}
