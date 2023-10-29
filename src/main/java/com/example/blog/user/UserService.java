package com.example.blog.user;

import com.example.blog.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User get(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id [%d] not found".formatted(id)));
    }

    public User save(User user) {
        return userRepository.save(user);
    }
}
