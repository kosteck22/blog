package com.example.blog.category;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
public class CategoryRequest {

    @NotNull
    @Size(min = 3, max = 32, message = "Name size must be between 3 and 32")
    private String name;
}
