package com.example.blog.tag;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
@Relation(itemRelation = "tag", collectionRelation = "tags")
public class TagModel extends RepresentationModel<TagModel> {
    private Long id;
    private String name;
}
