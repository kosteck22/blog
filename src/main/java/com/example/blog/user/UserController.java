package com.example.blog.user;

import com.example.blog.security.CurrentUser;
import com.example.blog.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") Long id) {
        User user = userService.getById(id);

        return ResponseEntity.ok(user);
    }

    @GetMapping("me")
    public ResponseEntity<User> getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        User user = userService.getByEmail(currentUser.getEmail());

        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<User> addUser(@Valid @RequestBody UserRegistrationRequest userRegistrationRequest) {
        User savedUser = userService.addUser(userRegistrationRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @PutMapping("{id}/promote-to-admin")
    public ResponseEntity<User> addAdminRole(@PathVariable("id") Long userId) {
        User user = userService.addAdminRole(userId);

        return ResponseEntity.ok(user);
    }

    @PutMapping("{id}/remove-admin-role")
    public ResponseEntity<User> removeAdminRole(@PathVariable("id") Long userId) {
        User user = userService.removeAdminRole(userId);

        return ResponseEntity.ok(user);
    }

    @GetMapping("/identities/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable("email") String email) {
        User user = userService.getByEmail(email);

        return ResponseEntity.ok(user);
    }

    @GetMapping("/identities/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable("username") String username) {
        User user = userService.getByUsername(username);

        return ResponseEntity.ok(user);
    }
}