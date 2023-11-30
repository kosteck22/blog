package com.example.blog.tag;

import com.example.blog.DTOMapper;
import com.example.blog.entity.Tag;
import org.springframework.stereotype.Component;

@Component
public class TagMapper implements DTOMapper<Tag, TagResponse> {
    @Override
    public TagResponse apply(Tag tag) {
        return TagResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                .build();
    }
}