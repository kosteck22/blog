package com.example.blog.post;

import com.example.blog.category.CategoryResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Getter @Setter
@Builder
@Relation(itemRelation = "post", collectionRelation = "posts")
public class PostResponse extends RepresentationModel<PostResponse> {
    private Long id;
    private String title;
    private String body;
    private CategoryResponse category;
}
