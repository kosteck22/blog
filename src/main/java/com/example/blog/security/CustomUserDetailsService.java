package com.example.blog.security;

import com.example.blog.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String emailOrUsername) throws UsernameNotFoundException {
        return userRepository.findUserByEmailOrUsername(emailOrUsername)
                .map(u -> new UserPrincipal(u.getId(), u.getEmail(), u.getUsername(), u.getPassword(), u.getRoles()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with given username or email [%s]".formatted(emailOrUsername)));
    }
}
