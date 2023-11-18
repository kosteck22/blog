package com.example.blog.comment;

import com.example.blog.exception.RequestValidationException;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.post.Post;
import com.example.blog.post.PostRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentService(@Qualifier("comment-jpa") CommentRepository commentRepository,
                          @Qualifier("post-jpa") PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    public Page<Comment> fetchCommentDataForPostAsPage(Long postId, Pageable pageable) {
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("Post with id [%d] does not exists".formatted(postId));
        }

        return commentRepository.findAllInPost(postId, pageable);
    }

    public Comment save(Long postId, CommentRequest request) {
        Post post = getPostById(postId);

        Comment comment = Comment.builder()
                .body(request.getBody())
                .post(post).build();

        return commentRepository.save(comment);
    }

    public void delete(Long postId, Long commentId) {
        Post post = getPostById(postId);
        Comment comment = getCommentById(commentId);

        if (commentDoesNotBelongToPost(comment, post)) {
            throw new RequestValidationException("Comment does not belong to post with id [%d]".formatted(postId));
        }

        commentRepository.delete(comment);
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

    public Comment getById(Long postId, Long commentId) {
        Post post = getPostById(postId);
        Comment comment = getCommentById(commentId);

        if (commentDoesNotBelongToPost(comment, post)) {
            throw new RequestValidationException("Comment does not belong to post with id [%d]".formatted(postId));
        }

        return comment;
    }

    public Comment update(Long postId, Long commentId, CommentRequest request) {
        Post post = getPostById(postId);
        Comment comment = getCommentById(commentId);

        if (commentDoesNotBelongToPost(comment, post)) {
            throw new RequestValidationException("Comment does not belong to post with id [%d]".formatted(postId));
        }

        comment.setBody(request.getBody());

        return commentRepository.save(comment);
    }
}
