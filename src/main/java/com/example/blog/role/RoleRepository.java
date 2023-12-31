package com.example.blog.role;

import com.example.blog.entity.Role;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface RoleRepository {
    Optional<Role> findByName(AppRoles name);
    Role save(Role role);
    List<Role> findAll();
}
