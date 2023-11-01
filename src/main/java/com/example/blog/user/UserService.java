package com.example.blog.user;

import com.example.blog.exception.DuplicateResourceException;
import com.example.blog.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("user with id [%d] not found".formatted(id)));
    }

    public User addUser(UserRegistrationRequest userRegistrationRequest) {
        //check if email exist
        String email = userRegistrationRequest.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("email already taken");
        }

        //check if username exist
        String username = userRegistrationRequest.getUsername();
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateResourceException("username already taken");
        }

        User user = User.builder()
                .email(email)
                .username(username)
                .password(userRegistrationRequest.getPassword())
                .phone(userRegistrationRequest.getPhone())
                .firstName(userRegistrationRequest.getFirstName())
                .lastName(userRegistrationRequest.getLastName()).build();

        return userRepository.save(user);
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "user with email [%s] doesn't exists".formatted(email)));
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "user with username [%s] doesn't exists".formatted(username)));
    }
}
