package com.example.blog;

import com.example.blog.role.AppRoles;
import com.example.blog.role.Role;
import com.example.blog.role.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;

    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


    @Override
    public void run(String... args) throws Exception {
        /*Role user = Role.builder()
                .name(AppRoles.ROLE_USER)
                .description("User can create new posts and comments.").build();

        Role admin = Role.builder()
                .name(AppRoles.ROLE_ADMIN)
                .description("Admin can remove other users, posts, comments, tags, add categories").build();

        roleRepository.save(user);
        roleRepository.save(admin);*/
    }
}
