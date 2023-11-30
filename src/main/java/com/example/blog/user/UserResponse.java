package com.example.blog.user;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Getter @Setter
@Builder
@Relation(itemRelation = "user", collectionRelation = "users")
public class UserResponse extends RepresentationModel<UserResponse> {
    private Long id;
    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private String phone;
}
