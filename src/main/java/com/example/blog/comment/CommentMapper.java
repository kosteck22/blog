package com.example.blog.comment;

import com.example.blog.DTOMapper;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

@Component
public class CommentMapper implements DTOMapper<Comment, CommentModel> {
    @Override
    public CommentModel apply(Comment comment) {
        LocalDateTime createdAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(comment.getCreatedDate()),
                TimeZone.getDefault().toZoneId());

        return  CommentModel.builder()
                    .id(comment.getId())
                    .body(comment.getBody())
                    .createdDate(createdAt).build();
    }
}
