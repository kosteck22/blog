package com.example.blog.config;


import com.example.blog.exception.ApiError;
import com.example.blog.user.UserResponse;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Kamil",
                        email = "kamilostafil@gmail.com"
                ),
                description = "OpenApi documentation for Blog Rest API",
                title = "Blog Rest API",
                version = "1.0",
                termsOfService = "Terms of service"
        ),
        servers = @Server(
                description = "Local ENV",
                url = "http://localhost:8102"
        ),
        security = @SecurityRequirement(
            name = "bearerAuth"
        )
)
@SecuritySchemes({
        @SecurityScheme(
                name = "bearerAuth",
                description = "JWT auth description",
                scheme = "bearer",
                type = SecuritySchemeType.HTTP,
                bearerFormat = "JWT",
                in = SecuritySchemeIn.HEADER
        )
}
)
public class OpenApiConfig {
    public static final String RESPONSE_404 = "response404";
    public static final String RESPONSE_409 = "response409";
    public static final String RESPONSE_401 = "response401";
    public static final String RESPONSE_400 = "response400";

    @ApiResponse(
            responseCode = "404",
            description = "Not Found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class)
            ),
            ref = RESPONSE_404
    )
    public void response404() {
        // This method is just for reference
    }

    @ApiResponse(
            responseCode = "409",
            description = "Conflict",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class)
            ),
            ref = RESPONSE_409
    )
    public void response409() {
        // This method is just for reference
    }

    @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class)
            ),
            ref = RESPONSE_401
    )
    public void response401() {
        // This method is just for reference
    }

    @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class)
            ),
            ref = RESPONSE_401
    )
    public void response400() {
        // This method is just for reference
    }
}
