package com.example.blog.user;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserRestController {
    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") Long id) {
        User user = userService.getById(id);

        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<User> addUser(@Valid @RequestBody UserRegistrationRequest userRegistrationRequest) {
        User savedUser = userService.addUser(userRegistrationRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
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