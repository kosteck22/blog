package com.example.blog.comment;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;

@Getter @Setter
@Builder
@Relation(itemRelation = "comment", collectionRelation = "comments")
public class CommentModel extends RepresentationModel<CommentModel> {
    private Long id;
    private String body;
    private LocalDateTime createdDate;
}
