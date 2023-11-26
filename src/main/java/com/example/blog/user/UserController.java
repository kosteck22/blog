package com.example.blog.user;

import com.example.blog.entity.Comment;
import com.example.blog.comment.CommentModel;
import com.example.blog.comment.CommentModelAssembler;
import com.example.blog.entity.User;
import com.example.blog.security.CurrentUser;
import com.example.blog.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    private final UserModelAssembler userModelAssembler;
    private final PagedResourcesAssembler<Comment> pagedResourcesAssembler;
    private final CommentModelAssembler commentModelAssembler;

    public UserController(UserService userService,
                          UserModelAssembler userModelAssembler,
                          PagedResourcesAssembler<Comment> pagedResourcesAssembler,
                          CommentModelAssembler commentModelAssembler) {
        this.userService = userService;
        this.userModelAssembler = userModelAssembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
        this.commentModelAssembler = commentModelAssembler;
    }

    @GetMapping("{id}")
    public ResponseEntity<UserModel> getUser(@PathVariable("id") Long id) {
        User user = userService.getById(id);

        return ResponseEntity.ok(userModelAssembler.toModel(user));
    }

    @GetMapping("me")
    public ResponseEntity<UserModel> getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        User user = userService.getByEmail(currentUser.getEmail());

        return ResponseEntity.ok(userModelAssembler.toModel(user));
    }

    @GetMapping("me/comments")
    public ResponseEntity<PagedModel<CommentModel>> getCommentsForCurrentUser(@CurrentUser UserPrincipal currentUser,
                                                                              @PageableDefault(size = 5) Pageable pageable) {
        Page<Comment> commentsPage = userService.getCommentsForCurrentUser(currentUser.getId(), pageable);

        if (commentsPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(PagedModel.empty());
        }

        return ResponseEntity.ok(pagedResourcesAssembler.toModel(commentsPage, commentModelAssembler));
    }

    @PostMapping
    public ResponseEntity<UserModel> addUser(@Valid @RequestBody UserRegistrationRequest userRegistrationRequest) {
        User user = userService.addUser(userRegistrationRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(userModelAssembler.toModel(user));
    }

    @PutMapping("{id}/promote-to-admin")
    public ResponseEntity<UserModel> addAdminRole(@PathVariable("id") Long userId) {
        User user = userService.addAdminRole(userId);

        return ResponseEntity.ok(userModelAssembler.toModel(user));
    }

    @PutMapping("{id}/remove-admin-role")
    public ResponseEntity<UserModel> removeAdminRole(@PathVariable("id") Long userId) {
        User user = userService.removeAdminRole(userId);

        return ResponseEntity.ok(userModelAssembler.toModel(user));
    }

    @GetMapping("/identities/email/{email}")
    public ResponseEntity<UserModel> getUserByEmail(@PathVariable("email") String email) {
        User user = userService.getByEmail(email);

        return ResponseEntity.ok(userModelAssembler.toModel(user));
    }

    @GetMapping("/identities/username/{username}")
    public ResponseEntity<UserModel> getUserByUsername(@PathVariable("username") String username) {
        User user = userService.getByUsername(username);

        return ResponseEntity.ok(userModelAssembler.toModel(user));
    }
}