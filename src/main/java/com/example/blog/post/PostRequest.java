package com.example.blog.post;

import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PostRequest {
    @Size(min = 5, max = 64, message = "title size must be between 5 and 64")
    private String title;

    @Size(min = 10, max = 1024, message = "body size must be between 10 and 1024")
    private String body;
}
