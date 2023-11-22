package com.example.blog.post;

import com.example.blog.audit.DateAudit;
import com.example.blog.audit.UserDateAudit;
import com.example.blog.category.Category;
import com.example.blog.comment.Comment;
import com.example.blog.tag.Tag;
import com.example.blog.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "posts")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Post extends UserDateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String title;

    @Column(nullable = false)
    private String body;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "post", orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(name = "post_tag",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    public Set<Tag> getTags() {
        return tags == null ? tags = new HashSet<>() : tags;
    }

    public List<Comment> getComments() {
        return comments == null ? comments = new ArrayList<>() : comments;
    }

    public void addTag(Tag tag) {
        getTags().add(tag);
        tag.getPosts().add(this);
    }

    public void removeTag(Tag tag) {
        getTags().remove(tag);
        tag.getPosts().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return Objects.equals(id, post.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
