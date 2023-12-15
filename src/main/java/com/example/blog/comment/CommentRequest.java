package com.example.blog.comment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Objects;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CommentRequest {

    @NotNull
    @Size(min = 10, max = 1024, message = "body size must be between 10 and 1024")
    private String body;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentRequest that = (CommentRequest) o;
        return Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(body);
    }
}
