package com.example.blog.entity;

import com.example.blog.audit.UserDateAudit;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tags")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Tag extends UserDateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "tags")
    private Set<Post> posts;

    public Set<Post> getPosts() {
        return posts == null ? posts = new HashSet<>() : posts;
    }

    public Tag(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return id == tag.id && Objects.equals(name, tag.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
