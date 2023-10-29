package com.example.blog.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("jpa")
public interface UserRepositoryJPA extends UserRepository, JpaRepository<User, Long> {
}
