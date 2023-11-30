package com.example.blog.tag;

import com.example.blog.entity.Tag;
import com.example.blog.post.PostController;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TagModelAssembler extends RepresentationModelAssemblerSupport<Tag, TagResponse> {
    private final TagMapper mapper;

    public TagModelAssembler(TagMapper mapper) {
        super(TagController.class, TagResponse.class);
        this.mapper = mapper;
    }

    @Override
    @NonNull
    public TagResponse toModel(@NonNull Tag tag) {
        TagResponse tagModel = mapper.apply(tag);

        tagModel.add(
                linkTo(methodOn(TagController.class).get(tag.getId()))
                        .withSelfRel());

        tagModel.add(
                linkTo(methodOn(PostController.class)
                        .getPostsByTag(tagModel.getId(), null))
                        .withRel("posts"));

        return tagModel;
    }
}