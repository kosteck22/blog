package com.example.blog.comment;

import com.example.blog.audit.DateAudit;
import com.example.blog.audit.UserDateAudit;
import com.example.blog.post.Post;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "comments")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class Comment extends UserDateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String body;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
}
