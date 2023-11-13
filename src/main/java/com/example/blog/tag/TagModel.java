package com.example.blog.tag;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class TagModel extends RepresentationModel<TagModel> {
    private Long id;
    private String name;
}
