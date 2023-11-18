package com.example.blog.category;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Getter @Setter
@Builder
@Relation(itemRelation = "category", collectionRelation = "categories")
public class CategoryModel extends RepresentationModel<CategoryModel> {
    private Long id;
    private String name;
}
