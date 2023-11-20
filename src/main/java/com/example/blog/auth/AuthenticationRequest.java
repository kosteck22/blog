package com.example.blog.auth;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
public class AuthenticationRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
