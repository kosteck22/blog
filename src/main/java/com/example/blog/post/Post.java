package com.example.blog.post;

import com.example.blog.comment.Comment;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "posts")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String title;

    @Column(nullable = false)
    private String body;

    @OneToMany(mappedBy = "post", orphanRemoval = true)
    private List<Comment> comments;
}
