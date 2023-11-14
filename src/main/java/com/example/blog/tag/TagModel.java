package com.example.blog.tag;

import com.example.blog.post.Post;
import com.example.blog.post.PostModel;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.Set;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class TagModel extends RepresentationModel<TagModel> {
    private Long id;
    private String name;
}
