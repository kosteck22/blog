package com.example.blog.tag;

import com.example.blog.post.PostController;
import com.example.blog.post.PostModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class TagModelAssembler extends RepresentationModelAssemblerSupport<Tag, TagModel> {
    private final TagMapper mapper;

    public TagModelAssembler(TagMapper mapper) {
        super(TagController.class, TagModel.class);
        this.mapper = mapper;
    }

    @Override
    public TagModel toModel(Tag tag) {
        return mapper.apply(tag);
    }
}
