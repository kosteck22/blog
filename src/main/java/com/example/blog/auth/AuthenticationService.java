package com.example.blog.auth;

import com.example.blog.exception.DuplicateResourceException;
import com.example.blog.exception.InvalidUsernameOrPasswordException;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.role.AppRoles;
import com.example.blog.entity.Role;
import com.example.blog.role.RoleRepository;
import com.example.blog.security.JwtTokenProvider;
import com.example.blog.entity.User;
import com.example.blog.user.UserRegistrationRequest;
import com.example.blog.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthenticationService {

    private final Logger LOGGER = LoggerFactory.getLogger(AuthenticationService.class);

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthenticationService(AuthenticationManager authenticationManager,
                                 UserRepository userRepository,
                                 RoleRepository roleRepository,
                                 PasswordEncoder passwordEncoder,
                                 JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public void registerUser(UserRegistrationRequest signupRequest) {
        validateUserRegistrationRequest(signupRequest);
        Role role = getUserRole();

        User user = User.builder()
                .email(signupRequest.getEmail())
                .username(signupRequest.getUsername())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .phone(signupRequest.getPhone())
                .firstName(signupRequest.getFirstName())
                .lastName(signupRequest.getLastName()).build();

        user.addRole(role);

        userRepository.save(user);
    }

    public AuthResponse login(AuthenticationRequest request) {
        try {
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authenticate);
            String token = tokenProvider.generateToken(authenticate);

            return new AuthResponse("Bearer " + token);
        } catch (AuthenticationException ex) {
            LOGGER.error(ex.getMessage());
            throw new InvalidUsernameOrPasswordException("Invalid username/password supplied");
        }
    }

    private void validateUserRegistrationRequest(UserRegistrationRequest signupRequest) {
        //check if email exist
        String email = signupRequest.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("email already taken");
        }

        //check if username exist
        String username = signupRequest.getUsername();
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateResourceException("username already taken");
        }
    }

    private Role getUserRole() {
        return roleRepository.findByName(AppRoles.ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
    }
}
