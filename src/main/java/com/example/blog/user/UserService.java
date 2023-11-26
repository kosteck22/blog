package com.example.blog.user;

import com.example.blog.entity.Comment;
import com.example.blog.comment.CommentRepository;
import com.example.blog.entity.User;
import com.example.blog.exception.DuplicateResourceException;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.role.AppRoles;
import com.example.blog.entity.Role;
import com.example.blog.role.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final CommentRepository commentRepository;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       RoleRepository roleRepository,
                       CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.commentRepository = commentRepository;
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("user with id [%d] not found".formatted(id)));
    }

    public Page<Comment> getCommentsForCurrentUser(Long userId, Pageable pageable) {
        return commentRepository.findAllInUser(userId, pageable);
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

    public User addUser(UserRegistrationRequest userRegistrationRequest) {
        validateUserRegistrationRequest(userRegistrationRequest);
        Role userRole = getUserRole();
        User user = buildUser(userRegistrationRequest, userRole);

        return userRepository.save(user);
    }

    public User addAdminRole(Long userId) {
        User userToPromote = getById(userId);
        ensureUserNotAdminAlready(userToPromote);
        Role adminRole = getAdminRole();
        
        userToPromote.addRole(adminRole);

        return userRepository.save(userToPromote);
    }

    public User removeAdminRole(Long userId) {
        User userToDegraded = getById(userId);
        ensureUserAdminAlready(userToDegraded);
        Role adminRole = getAdminRole();

        userToDegraded.removeRole(adminRole);

        return userRepository.save(userToDegraded);
    }

    private Role getUserRole() {
        return roleRepository.findByName(AppRoles.ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException("Admin role not found in db"));
    }

    private Role getAdminRole() {
        return roleRepository.findByName(AppRoles.ROLE_ADMIN)
                .orElseThrow(() -> new ResourceNotFoundException("Admin role not found in db"));
    }

    private User buildUser(UserRegistrationRequest userRegistrationRequest, Role userRole) {
        return User.builder()
                .email(userRegistrationRequest.getEmail())
                .username(userRegistrationRequest.getUsername())
                .password(passwordEncoder.encode(userRegistrationRequest.getPassword()))
                .phone(userRegistrationRequest.getPhone())
                .firstName(userRegistrationRequest.getFirstName())
                .lastName(userRegistrationRequest.getLastName())
                .roles(Collections.singleton(userRole)).build();
    }

    private void validateUserRegistrationRequest(UserRegistrationRequest userRegistrationRequest) {
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
    }

    private void ensureUserNotAdminAlready(User userToPromote) {
        if (userToPromote.getRoles().stream().anyMatch(r -> r.getName().equals(AppRoles.ROLE_ADMIN))) {
            throw new DuplicateResourceException("User with id [%d] already has admin role"
                    .formatted(userToPromote.getId()));
        }
    }

    private void ensureUserAdminAlready(User userToDegraded) {
        userToDegraded.getRoles()
                .stream()
                .filter(r -> r.getName().equals(AppRoles.ROLE_ADMIN))
                .findFirst()
                .orElseThrow(() -> new DuplicateResourceException("User with id [%d] does not have admin role"
                        .formatted(userToDegraded.getId())));
    }
}
