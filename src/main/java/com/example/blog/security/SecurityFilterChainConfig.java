package com.example.blog.security;

import com.example.blog.role.AppRoles;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityFilterChainConfig {

    private final JwtAuthenticationTokenFilter jwtFilter;

    public SecurityFilterChainConfig(JwtAuthenticationTokenFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable);

        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/categories",
                                "/api/v1/tags",
                                "/api/v1/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/posts/*/comments",
                                "/api/v1/posts").hasAnyRole("USER")
                        .requestMatchers(HttpMethod.PUT,
                                "/api/v1/categories/*",
                                "/api/v1/tags/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,
                                "/api/v1/posts/*/comments/*",
                                "/api/v1/posts/*").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/v1/categories/*",
                                "/api/v1/tags/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/v1/posts/*/comments/*",
                                "/api/v1/posts/*").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT,
                                "/api/v1/users/*/promote-to-admin",
                                "/api/v1/users/*/remove-admin-role").hasRole("SUPER_ADMIN")
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/users/me",
                                "/api/v1/users/me/comments").hasRole("USER")
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage())
                        ))
                );

        return http.build();
    }
}
