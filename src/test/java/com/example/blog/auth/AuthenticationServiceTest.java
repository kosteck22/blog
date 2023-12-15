package com.example.blog.auth;

import com.example.blog.entity.Role;
import com.example.blog.entity.User;
import com.example.blog.exception.DuplicateResourceException;
import com.example.blog.exception.InvalidUsernameOrPasswordException;
import com.example.blog.role.RoleRepository;
import com.example.blog.security.JwtTokenProvider;
import com.example.blog.user.UserRegistrationRequest;
import com.example.blog.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    private AuthenticationService underTest;

    @BeforeEach
    public void setUp() {
        underTest = new AuthenticationService(
                authenticationManager,
                userRepository,
                roleRepository,
                passwordEncoder,
                tokenProvider);
    }

    @Test
    public void test_register_user_should_success() {
        //given
        String email = "qwe@gmail.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);

        String username = "qwe";
        when(userRepository.existsByUsername(username)).thenReturn(false);

        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .email(email)
                .password("Qqwejkd1123J@1")
                .username(username)
                .firstName("qwe")
                .lastName("qwe")
                .phone("123456789").build();

        Role mockedRole = mock(Role.class);
        when(roleRepository.findByName(any())).thenReturn(Optional.of(mockedRole));

        when(userRepository.count()).thenReturn(2L);

        String passwordHash = "%jdksa#;f;123@(;s@)9";
        when(passwordEncoder.encode(request.getPassword())).thenReturn(passwordHash);

        //when
        underTest.registerUser(request);

        //then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userArgumentCaptor.capture());

        User capturedUser = userArgumentCaptor.getValue();

        assertThat(capturedUser.getId()).isNull();
        assertThat(capturedUser.getPassword()).isEqualTo(passwordHash);
        assertThat(capturedUser.getEmail()).isEqualTo(email);
        assertThat(capturedUser.getUsername()).isEqualTo(username);
        assertThat(capturedUser.getFirstName()).isEqualTo(request.getFirstName());
        assertThat(capturedUser.getLastName()).isEqualTo(request.getLastName());
        assertThat(capturedUser.getPhone()).isEqualTo(request.getPhone());
        assertThat(capturedUser.getRoles()).isEqualTo(Collections.singleton(mockedRole));
    }

    @Test
    public void test_register_user_should_success_first_user_with_many_roles() {
        //given
        String email = "qwe@gmail.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);

        String username = "qwe";
        when(userRepository.existsByUsername(username)).thenReturn(false);

        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .email(email)
                .password("Qqwejkd1123J@1")
                .username(username)
                .firstName("qwe")
                .lastName("qwe")
                .phone("123456789").build();

        Role mockedUserRole = mock(Role.class);
        Role mockedAdminRole = mock(Role.class);
        Role mockedSuperAdminRole = mock(Role.class);
        when(roleRepository.findAll()).thenReturn(List.of(mockedUserRole, mockedAdminRole, mockedSuperAdminRole));

        when(userRepository.count()).thenReturn(0L);

        String passwordHash = "%jdksa#;f;123@(;s@)9";
        when(passwordEncoder.encode(request.getPassword())).thenReturn(passwordHash);

        //when
        underTest.registerUser(request);

        //then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userArgumentCaptor.capture());

        User capturedUser = userArgumentCaptor.getValue();

        assertThat(capturedUser.getId()).isNull();
        assertThat(capturedUser.getPassword()).isEqualTo(passwordHash);
        assertThat(capturedUser.getEmail()).isEqualTo(email);
        assertThat(capturedUser.getUsername()).isEqualTo(username);
        assertThat(capturedUser.getFirstName()).isEqualTo(request.getFirstName());
        assertThat(capturedUser.getLastName()).isEqualTo(request.getLastName());
        assertThat(capturedUser.getPhone()).isEqualTo(request.getPhone());
        assertThat(capturedUser.getRoles().size()).isEqualTo(3);
        assertThat(capturedUser.getRoles()).contains(mockedUserRole, mockedAdminRole, mockedSuperAdminRole);
    }
    @Test
    public void test_register_user_should_throw_409_conflict_duplicate_email() {
        //given
        String email = "qwe@gmail.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        String username = "qwe";

        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .email(email)
                .password("Qqwejkd1123J@1")
                .username(username)
                .firstName("qwe")
                .lastName("qwe")
                .phone("123456789").build();

        //when
        //then
        assertThatThrownBy(() -> underTest.registerUser(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already taken");
    }

    @Test
    public void test_register_user_should_throw_409_conflict_duplicate_username() {
        //given
        String email = "qwe@gmail.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);

        String username = "qwe";
        when(userRepository.existsByUsername(username)).thenReturn(true);

        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .email(email)
                .password("Qqwejkd1123J@1")
                .username(username)
                .firstName("qwe")
                .lastName("qwe")
                .phone("123456789").build();

        //when
        //then
        assertThatThrownBy(() -> underTest.registerUser(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("username already taken");
    }

    @Test
    public void test_login_should_success() {
        //given
        AuthenticationRequest request = AuthenticationRequest.builder()
                .username("qwer")
                .password("Pqwe1!we").build();

        Authentication mockAuthentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mockAuthentication);

        String token = "jaskdjask1kj132knws.dsajikj14.dnask1";
        when(tokenProvider.generateToken(mockAuthentication)).thenReturn(token);

        //when
        AuthResponse result = underTest.login(request);

        //then
        assertThat(result).isNotNull();
        assertThat(result.authorizationToken()).isEqualTo("Bearer " + token);
    }

    @Test
    public void test_login_should_throw_authentication_exception() {
        //given
        AuthenticationRequest request = AuthenticationRequest.builder()
                .username("qwer")
                .password("Pqwe1!we").build();
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(BadCredentialsException.class);

        //when
        //then
        assertThatThrownBy(() -> underTest.login(request)).isInstanceOf(InvalidUsernameOrPasswordException.class)
                .hasMessage("Invalid username/password supplied");
    }
}