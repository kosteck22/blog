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
        User user = userService.get(id);

        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<User> addUser(@Valid @RequestBody UserRegistrationRequest userRegistrationRequest) {
        User savedUser = userService.addUser(userRegistrationRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }
}
