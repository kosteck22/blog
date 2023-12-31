package com.example.blog.comment;

import com.example.blog.DTOMapper;
import com.example.blog.entity.Comment;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

@Component
public class CommentMapper implements DTOMapper<Comment, CommentResponse> {
    @Override
    public CommentResponse apply(Comment comment) {
        LocalDateTime createdAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(comment.getCreatedDate()),
                TimeZone.getDefault().toZoneId());

        return  CommentResponse.builder()
                    .id(comment.getId())
                    .body(comment.getBody())
                    .createdDate(createdAt).build();
    }
}
