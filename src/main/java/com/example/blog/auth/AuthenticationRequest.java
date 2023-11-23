package com.example.blog.auth;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
public class AuthenticationRequest {
    @NotNull
    private String username;

    @NotNull
    private String password;
}
