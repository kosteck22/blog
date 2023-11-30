package com.example.blog.user;

import com.example.blog.DTOMapper;
import com.example.blog.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper implements DTOMapper<User, UserResponse> {
    @Override
    public UserResponse apply(User user) {

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone()).build();
    }
}