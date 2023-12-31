package com.example.blog.user;

import com.example.blog.entity.User;
import com.example.blog.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserRetrievalService {

    private final UserRepository userRepository;

    public UserRetrievalService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with email [%s] not found".formatted(email)));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id [%d] not found".formatted(id)));
    }
}
