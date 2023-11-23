package com.example.blog.user;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationRequest {

    @Email
    @NotBlank
    private String email;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Minimum eight characters, " +
                    "at least one uppercase letter, " +
                    "one lowercase letter, " +
                    "one number and one special character")
    private String password;

    @NotBlank
    @Size(min = 3, max = 64, message = "Size must be between 3 and 64")
    private String username;

    @NotBlank
    @Size(min = 2, max = 64, message = "Size must be between 2 and 64")
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 64, message = "Size must be between 2 and 64")
    private String lastName;

    @NotBlank
    @Size(min = 7, max = 20, message = "Size must be between 7 and 20")
    private String phone;
}
