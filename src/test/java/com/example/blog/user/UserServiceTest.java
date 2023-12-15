package com.example.blog.user;

import com.example.blog.comment.CommentRepository;
import com.example.blog.comment.CommentRequest;
import com.example.blog.entity.Comment;
import com.example.blog.entity.Role;
import com.example.blog.entity.User;
import com.example.blog.exception.DuplicateResourceException;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.role.AppRoles;
import com.example.blog.role.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private CommentRepository commentRepository;

    private UserService underTest;

    @BeforeEach
    public void setUp() {
        underTest = new UserService(
                userRepository,
                passwordEncoder,
                roleRepository,
                commentRepository);
    }

    @Test
    public void test_get_user_by_id_success() {
        //given
        Long id = 1L;
        User expected = User.builder()
                .id(id)
                .email("abc@gmail.com")
                .firstName("ab")
                .lastName("c")
                .username("ab c")
                .phone("1234 56 78")
                .password("zxc").build();
        when(userRepository.findById(id)).thenReturn(Optional.of(expected));

        //when
        User actual = underTest.getById(id);

        //then
        assertThat(actual).isEqualTo(expected);
        assertThat(actual.getId()).isEqualTo(id);
    }

    @Test
    public void test_get_user_by_id_throws_resource_not_found_exception() {
        //given
        Long id = 10L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> underTest.getById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("user with id [%d] not found".formatted(id));
    }

    @Test
    public void test_get_comments_for_user_return_page() {
        //given
        long userId = 1L;
        Pageable pageable = PageRequest.of(0, 2);
        Comment mockedComment1 = mock(Comment.class);
        Comment mockedComment2 = mock(Comment.class);
        Comment mockedComment3 = mock(Comment.class);
        List<Comment> comments = List.of(mockedComment1, mockedComment2, mockedComment3);
        Page<Comment> commentPage = new PageImpl<>(comments, pageable, comments.size());
        when(commentRepository.findAllInUser(userId, pageable)).thenReturn(commentPage);

        //when
        Page<Comment> commentsForCurrentUser = underTest.getCommentsForCurrentUser(userId, pageable);

        //then
        assertThat(commentsForCurrentUser).isNotEmpty();
        assertThat(commentsForCurrentUser).isEqualTo(commentPage);
    }

    @Test
    public void test_add_user_success() {
        //given
        String email = "abc@gmail.com";
        String username = "abc des";
        String hashedPassword = "ajsdk1;da'e1kdsql1;12$^((!@#SAD@@#DDSDAS";
        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .email(email)
                .password("Abcxa1!")
                .firstName("abc")
                .lastName("des")
                .username(username)
                .phone("1234 56 78")
                .build();
        Role mockedRole = mock(Role.class);
        User mockedUser = mock(User.class);

        when(passwordEncoder.encode(request.getPassword())).thenReturn(hashedPassword);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(roleRepository.findByName(AppRoles.ROLE_USER)).thenReturn(Optional.of(mockedRole));
        when(userRepository.save(any(User.class))).thenReturn(mockedUser);

        //when
        User result = underTest.addUser(request);

        //then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userArgumentCaptor.capture());

        User capturedUser = userArgumentCaptor.getValue();

        assertThat(capturedUser.getId()).isNull();
        assertThat(capturedUser.getUsername()).isEqualTo(request.getUsername());
        assertThat(capturedUser.getEmail()).isEqualTo(request.getEmail());
        assertThat(capturedUser.getPassword()).isEqualTo(hashedPassword);
        assertThat(capturedUser.getFirstName()).isEqualTo(request.getFirstName());
        assertThat(capturedUser.getLastName()).isEqualTo(request.getLastName());
        assertThat(capturedUser.getPhone()).isEqualTo(request.getPhone());
        assertThat(capturedUser.getRoles().contains(mockedRole)).isTrue();
        assertThat(result).isEqualTo(mockedUser);
    }

    @Test
    public void test_add_user_throws_duplicate_resource_exception_same_email() {
        //given
        String email = "abc@gmail.com";
        String username = "abc des";
        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .email(email)
                .password("Abcxa1!")
                .firstName("abc")
                .lastName("des")
                .username(username)
                .phone("1234 56 78")
                .build();
        when(userRepository.existsByEmail(email)).thenReturn(true);

        //when
        assertThatThrownBy(() -> underTest.addUser(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already taken");

        //then
        verify(userRepository, never()).save(any());
    }

    @Test
    public void test_save_user_throws_duplicate_resource_exception_same_username() {
        //given
        String email = "abc@gmail.com";
        String username = "abc des";
        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .email(email)
                .password("Abcxa1!")
                .firstName("abc")
                .lastName("des")
                .username(username)
                .phone("1234 56 78")
                .build();
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.existsByUsername(username)).thenReturn(true);

        //when
        assertThatThrownBy(() -> underTest.addUser(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("username already taken");

        //then
        verify(userRepository, never()).save(any());
    }

    @Test
    public void test_save_user_throws_resource_not_found_exception_role_does_not_exists() {
        //given
        String email = "abc@gmail.com";
        String username = "abc des";
        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .email(email)
                .password("Abcxa1!")
                .firstName("abc")
                .lastName("des")
                .username(username)
                .phone("1234 56 78")
                .build();
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(roleRepository.findByName(AppRoles.ROLE_USER)).thenReturn(Optional.empty());

        //when
        assertThatThrownBy(() -> underTest.addUser(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User role not found in db");

        //then
        verify(userRepository, never()).save(any());
    }

    @Test
    public void test_get_user_by_email_throws_resource_not_found_exception() {
        //given
        String email = "zxc@gmail.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> underTest.getByEmail(email))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("user with email [%s] doesn't exists".formatted(email));
    }

    @Test
    public void test_get_user_by_email_success() {
        //given
        String email = "zxc@gmail.com";
        User expected = User.builder()
                .id(1L)
                .email(email)
                .firstName("ab")
                .lastName("c")
                .username("ab c")
                .phone("1234 56 78")
                .password("zxc").build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(expected));

        //when
        User actual = underTest.getByEmail(email);

        //then
        assertThat(actual).isEqualTo(expected);
        assertThat(actual.getEmail()).isEqualTo(email);
    }

    @Test
    public void test_get_user_by_username_throws_resource_not_found_exception() {
        //given
        String username = "zxc vb";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> underTest.getByUsername(username))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("user with username [%s] doesn't exists".formatted(username));
    }

    @Test
    public void test_get_user_by_username_success() {
        //given
        String username = "zxc vb";
        User expected = User.builder()
                .id(1L)
                .email("zxc@gmail.com")
                .firstName("ab")
                .lastName("c")
                .username(username)
                .phone("1234 56 78")
                .password("zxc").build();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(expected));

        //when
        User actual = underTest.getByUsername(username);

        //then
        assertThat(actual).isEqualTo(expected);
        assertThat(actual.getUsername()).isEqualTo(username);
    }

    @Test
    public void test_add_admin_role_success() {
        //given
        Long userId = 1L;
        Role userRole = Role.builder()
                .name(AppRoles.ROLE_USER).build();
        Role adminRole = Role.builder()
                .name(AppRoles.ROLE_ADMIN).build();

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        User user = User.builder()
                .id(userId)
                .email("zxc@gmail.com")
                .firstName("abqw")
                .lastName("casd")
                .username("qwe")
                .phone("1234 56 78")
                .password("zxc1WSD1!dsa")
                .roles(roles).build();
        User mockedUser = mock(User.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(AppRoles.ROLE_ADMIN)).thenReturn(Optional.of(adminRole));
        when(userRepository.save(any(User.class))).thenReturn(mockedUser);

        //when
        User result = underTest.addAdminRole(userId);

        //then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());

        User userBeforeUpdate = userArgumentCaptor.getValue();

        assertThat(userBeforeUpdate).isEqualTo(user);
        assertThat(userBeforeUpdate.getRoles().contains(adminRole)).isTrue();
        assertThat(userBeforeUpdate.getRoles().contains(userRole)).isTrue();
        assertThat(result).isEqualTo(mockedUser);
    }

    @Test
    public void test_add_admin_role_throws_resource_not_found_for_user_id() {
        //given
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> underTest.addAdminRole(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("user with id [%d] not found".formatted(userId));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void test_add_admin_role_throws_duplicate_resource_exception_user_has_already_admin_role() {
        //given
        Long userId = 1L;
        Role userRole = Role.builder()
                .name(AppRoles.ROLE_USER).build();
        Role adminRole = Role.builder()
                .name(AppRoles.ROLE_ADMIN).build();
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        roles.add(adminRole);

        User user = User.builder()
                .id(userId)
                .email("zxc@gmail.com")
                .firstName("abqw")
                .lastName("casd")
                .username("qwe")
                .phone("1234 56 78")
                .password("zxc1WSD1!dsa")
                .roles(roles).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        //when
        //then
        assertThatThrownBy(() -> underTest.addAdminRole(userId))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("User with id [%d] already has admin role".formatted(userId));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void test_add_admin_role_throws_resource_not_found_exception_role_admin_not_found() {
        //given
        Long userId = 1L;
        Role userRole = Role.builder()
                .name(AppRoles.ROLE_USER).build();
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        User user = User.builder()
                .id(userId)
                .email("zxc@gmail.com")
                .firstName("abqw")
                .lastName("casd")
                .username("qwe")
                .phone("1234 56 78")
                .password("zxc1WSD1!dsa")
                .roles(roles).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(AppRoles.ROLE_ADMIN)).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> underTest.addAdminRole(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Admin role not found in db");

        verify(userRepository, never()).save(any(User.class));
    }
    @Test
    public void test_remove_admin_role_success() {
        //given
        Long userId = 1L;
        Role userRole = Role.builder()
                .name(AppRoles.ROLE_USER).build();
        Role adminRole = Role.builder()
                .name(AppRoles.ROLE_ADMIN).build();

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        roles.add(adminRole);

        User user = User.builder()
                .id(userId)
                .email("zxc@gmail.com")
                .firstName("abqw")
                .lastName("casd")
                .username("qwe")
                .phone("1234 56 78")
                .password("zxc1WSD1!dsa")
                .roles(roles).build();
        User mockedUser = mock(User.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(AppRoles.ROLE_ADMIN)).thenReturn(Optional.of(adminRole));
        when(userRepository.save(any(User.class))).thenReturn(mockedUser);

        //when
        User result = underTest.removeAdminRole(userId);

        //then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());

        User userBeforeUpdate = userArgumentCaptor.getValue();

        assertThat(userBeforeUpdate).isEqualTo(user);
        assertThat(userBeforeUpdate.getRoles().contains(adminRole)).isFalse();
        assertThat(userBeforeUpdate.getRoles().contains(userRole)).isTrue();
        assertThat(result).isEqualTo(mockedUser);
    }

    @Test
    public void test_remove_admin_role_throws_resource_not_found_for_user_id() {
        //given
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> underTest.removeAdminRole(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("user with id [%d] not found".formatted(userId));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void test_remove_admin_role_throws_duplicate_resource_exception_user_does_not_have_admin_role() {
        //given
        Long userId = 1L;
        Role userRole = Role.builder()
                .name(AppRoles.ROLE_USER).build();
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        User user = User.builder()
                .id(userId)
                .email("zxc@gmail.com")
                .firstName("abqw")
                .lastName("casd")
                .username("qwe")
                .phone("1234 56 78")
                .password("zxc1WSD1!dsa")
                .roles(roles).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        //when
        //then
        assertThatThrownBy(() -> underTest.removeAdminRole(userId))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("User with id [%d] does not have admin role".formatted(userId));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void test_remove_admin_role_throws_resource_not_found_exception_role_admin_not_found() {
        //given
        Long userId = 1L;
        Role userRole = Role.builder()
                .name(AppRoles.ROLE_USER).build();
        Role adminRole = Role.builder()
                .name(AppRoles.ROLE_ADMIN).build();
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        roles.add(adminRole);

        User user = User.builder()
                .id(userId)
                .email("zxc@gmail.com")
                .firstName("abqw")
                .lastName("casd")
                .username("qwe")
                .phone("1234 56 78")
                .password("zxc1WSD1!dsa")
                .roles(roles).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(AppRoles.ROLE_ADMIN)).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> underTest.removeAdminRole(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Admin role not found in db");

        verify(userRepository, never()).save(any(User.class));
    }
}