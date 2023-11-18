package com.example.blog.post;

import com.example.blog.category.CategoryModel;
import com.example.blog.tag.TagModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.List;

@Getter @Setter
@Builder
@Relation(itemRelation = "post", collectionRelation = "posts")
public class PostModel extends RepresentationModel<PostModel> {
    private Long id;
    private String title;
    private String body;
    private CategoryModel category;
}
