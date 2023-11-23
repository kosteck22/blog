package com.example.blog.user;

import com.example.blog.DTOMapper;
import jakarta.persistence.Column;
import org.springframework.stereotype.Component;

@Component
public class UserMapper implements DTOMapper<User, UserModel> {
    @Override
    public UserModel apply(User user) {
        return null;
    }
}
