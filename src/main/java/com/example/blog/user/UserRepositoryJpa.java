package com.example.blog.user;

import com.example.blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("user-jpa")
public interface UserRepositoryJpa extends UserRepository, JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    @Query("SELECT u FROM User u WHERE u.email=?1 OR u.username=?1")
    Optional<User> findUserByEmailOrUsername(String emailOrUsername);
}
