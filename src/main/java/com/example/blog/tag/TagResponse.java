package com.example.blog.tag;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
@Relation(itemRelation = "tag", collectionRelation = "tags")
public class TagResponse extends RepresentationModel<TagResponse> {
    private Long id;
    private String name;
}
