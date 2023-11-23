package com.example.blog.user;

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
}
