package com.example.blog.base;

import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RootEntity {
    private String categoriesUrl;
    private String tagsUrl;
    private String postsUrl;

}
