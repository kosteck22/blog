package com.example.blog.tag;

import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Objects;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TagRequest {
    @Size(min = 2, max = 20, message = "Size of name must be between 2 and 20")
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagRequest request = (TagRequest) o;
        return Objects.equals(name, request.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
