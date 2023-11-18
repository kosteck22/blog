package com.example.blog.tag;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class TagRequest {
    @Size(min = 2, max = 20, message = "Size of name must be between 2 and 20")
    private String name;
}
