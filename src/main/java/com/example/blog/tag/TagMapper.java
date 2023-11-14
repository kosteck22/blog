package com.example.blog.tag;

import com.example.blog.DTOMapper;
import org.springframework.stereotype.Component;

@Component
public class TagMapper implements DTOMapper<Tag, TagModel> {
    @Override
    public TagModel apply(Tag tag) {

        return TagModel.builder()
                .id(tag.getId())
                .name(tag.getName())
                .build();
    }
}