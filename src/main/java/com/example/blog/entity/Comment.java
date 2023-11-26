package com.example.blog.entity;

import com.example.blog.audit.UserDateAudit;
import com.example.blog.user.UserOwnedEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "comments")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class Comment extends UserDateAudit implements UserOwnedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String body;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
}
