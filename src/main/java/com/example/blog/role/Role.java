package com.example.blog.role;

import com.example.blog.role.AppRoles;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.NaturalId;

@Entity
@Table(name = "roles")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NaturalId
    @Enumerated(value = EnumType.STRING)
    @Column(length = 40, nullable = false, unique = true)
    private AppRoles name;

    @Column(length = 150)
    private String description;
}
