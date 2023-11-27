package com.example.blog.user;

import com.example.blog.entity.User;

import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findUserByEmailOrUsername(String emailOrUsername);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    long count();
}
