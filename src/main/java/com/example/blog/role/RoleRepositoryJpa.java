package com.example.blog.role;

import com.example.blog.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepositoryJpa extends JpaRepository<Role, Long>, RoleRepository {
    Optional<Role> findByName(AppRoles name);
}
