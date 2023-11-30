package com.example.blog.user;

import com.example.blog.config.OpenApiConfig;
import com.example.blog.entity.Comment;
import com.example.blog.comment.CommentResponse;
import com.example.blog.comment.CommentModelAssembler;
import com.example.blog.entity.User;
import com.example.blog.exception.ApiError;
import com.example.blog.security.CurrentUser;
import com.example.blog.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @Operation(
            description = "Get user info by id",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/hal+json",
                                    schema = @Schema(implementation = UserResponse.class))
                    ),
                    @ApiResponse(ref = OpenApiConfig.RESPONSE_404)
            }
    )
    public ResponseEntity<UserResponse> getUser(@PathVariable("id") Long id) {
        User user = userService.getById(id);

        return ResponseEntity.ok(userModelAssembler.toModel(user));
    }

    @GetMapping("me")
    @Operation(
            description = "Logged user information's",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/hal+json",
                                    schema = @Schema(implementation = UserResponse.class))
                    ),
                    @ApiResponse(ref = OpenApiConfig.RESPONSE_401)
            }
    )
    public ResponseEntity<UserResponse> getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        User user = userService.getByEmail(currentUser.getEmail());

        return ResponseEntity.ok(userModelAssembler.toModel(user));
    }

    @GetMapping("me/comments")
    @Operation(
            description = "Create new user",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/hal+json",
                                    schema = @Schema(implementation = UserResponse.class))
                    ),
                    @ApiResponse(ref = OpenApiConfig.RESPONSE_401)
            }
    )
    public ResponseEntity<PagedModel<CommentResponse>> getCommentsForCurrentUser(@CurrentUser UserPrincipal currentUser,
                                                                                 @PageableDefault(size = 5) Pageable pageable) {
        Page<Comment> commentsPage = userService.getCommentsForCurrentUser(currentUser.getId(), pageable);

        if (commentsPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(PagedModel.empty());
        }

        return ResponseEntity.ok(pagedResourcesAssembler.toModel(commentsPage, commentModelAssembler));
    }

    @PostMapping
    @Operation(
            description = "Create new user",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/hal+json",
                                    schema = @Schema(implementation = UserResponse.class))
                    ),
                    @ApiResponse(ref = OpenApiConfig.RESPONSE_400),
                    @ApiResponse(ref = OpenApiConfig.RESPONSE_409),
                    @ApiResponse(ref = OpenApiConfig.RESPONSE_404),
                    @ApiResponse(ref = OpenApiConfig.RESPONSE_401)
            }
    )
    public ResponseEntity<UserResponse> addUser(@Valid @RequestBody UserRegistrationRequest userRegistrationRequest) {
        User user = userService.addUser(userRegistrationRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(userModelAssembler.toModel(user));
    }

    @PutMapping("{id}/promote-to-admin")
    @Operation(
           description = "Promote the user to the position of admin",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/hal+json",
                                    schema = @Schema(implementation = UserResponse.class))
                    ),
                    @ApiResponse(ref = OpenApiConfig.RESPONSE_404),
                    @ApiResponse(ref = OpenApiConfig.RESPONSE_409),
                    @ApiResponse(ref = OpenApiConfig.RESPONSE_401)
            }
    )
    public ResponseEntity<UserResponse> addAdminRole(@PathVariable("id") Long userId) {
        User user = userService.addAdminRole(userId);

        return ResponseEntity.ok(userModelAssembler.toModel(user));
    }

    @PutMapping("{id}/remove-admin-role")
    @Operation(
            description = "Strip user from admin position",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/hal+json",
                                    schema = @Schema(implementation = UserResponse.class))
                    ),
                    @ApiResponse(ref = OpenApiConfig.RESPONSE_404),
                    @ApiResponse(ref = OpenApiConfig.RESPONSE_409),
                    @ApiResponse(ref = OpenApiConfig.RESPONSE_401)
            }
    )
    public ResponseEntity<UserResponse> removeAdminRole(@PathVariable("id") Long userId) {
        User user = userService.removeAdminRole(userId);

        return ResponseEntity.ok(userModelAssembler.toModel(user));
    }

    @GetMapping("/identities/email/{email}")
    @Operation(
            description = "Getting user by email (if response status is 404 it means that email is unique and free to take)",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/hal+json",
                                    schema = @Schema(implementation = UserResponse.class))
                    ),
                    @ApiResponse(ref = OpenApiConfig.RESPONSE_404)
            }
    )
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable("email") String email) {
        User user = userService.getByEmail(email);

        return ResponseEntity.ok(userModelAssembler.toModel(user));
    }

    @GetMapping("/identities/username/{username}")
    @Operation(
            description = "Getting user by username (if response status is 404 it means that username is unique and free to take)",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/hal+json",
                                    schema = @Schema(implementation = UserResponse.class))
                    ),
                    @ApiResponse(ref = OpenApiConfig.RESPONSE_404)
            }
    )
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable("username") String username) {
        User user = userService.getByUsername(username);

        return ResponseEntity.ok(userModelAssembler.toModel(user));
    }
}