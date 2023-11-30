package com.example.blog.category;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Getter @Setter
@Builder
@Relation(itemRelation = "category", collectionRelation = "categories")
public class CategoryResponse extends RepresentationModel<CategoryResponse> {
    private Long id;
    private String name;
}
