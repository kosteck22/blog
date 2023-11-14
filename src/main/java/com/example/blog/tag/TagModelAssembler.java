package com.example.blog.tag;

import com.example.blog.post.PostController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TagModelAssembler extends RepresentationModelAssemblerSupport<Tag, TagModel> {
    private final TagMapper mapper;

    public TagModelAssembler(TagMapper mapper) {
        super(TagController.class, TagModel.class);
        this.mapper = mapper;
    }

    @Override
    @NonNull
    public TagModel toModel(@NonNull Tag tag) {
        TagModel tagModel = mapper.apply(tag);

        tagModel.add(
                linkTo(methodOn(TagController.class)
                        .get(tagModel.getId())).withSelfRel());
//        tagModel.add(
//                linkTo(methodOn(PostController.class)
//                        .getPostsByTag(tagModel.getId())).withRel("posts"));
        return tagModel;
    }
}