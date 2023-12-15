package com.example.blog.auth;

import com.example.blog.entity.User;
import com.example.blog.exception.CustomAuthorizationException;
import com.example.blog.role.AppRoles;
import com.example.blog.security.UserPrincipal;
import com.example.blog.user.UserOwnedEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {
    @Mock
    private UserOwnedEntity userOwnedEntity;

    @Mock
    private UserPrincipal currentUser;

    private AuthorizationService underTest;

    private User user;

    @BeforeEach
    public void setUp() {
        underTest = new AuthorizationService();

        user = User.builder()
                .id(1L).build();
    }

    @Test
    public void test_has_auth_for_update_or_delete_entity_should_pass_because_current_user_owns_entity() {
        //given
        when(userOwnedEntity.getUser()).thenReturn(user);
        when(currentUser.getId()).thenReturn(1L);

        //when
        //then
        underTest.hasAuthorizationForUpdateOrDeleteEntity(userOwnedEntity, currentUser);
    }

    @Test
    public void test_has_auth_for_update_or_delete_entity_should_pass_because_current_user_is_admin() {
        //given
        when(userOwnedEntity.getUser()).thenReturn(user);
        when(currentUser.getId()).thenReturn(2L);
        doReturn(Collections.singleton(new SimpleGrantedAuthority(AppRoles.ROLE_ADMIN.name()))).when(currentUser).getAuthorities();

        //when
        //then
        underTest.hasAuthorizationForUpdateOrDeleteEntity(userOwnedEntity, currentUser);
    }

    @Test
    public void test_has_auth_for_update_or_delete_entity_should_throw_auth_exception() {
        //given
        when(userOwnedEntity.getUser()).thenReturn(user);
        when(currentUser.getId()).thenReturn(2L);
        doReturn(Collections.singleton(new SimpleGrantedAuthority(AppRoles.ROLE_USER.name()))).when(currentUser).getAuthorities();

        //when
        //then
        assertThatThrownBy(() -> underTest.hasAuthorizationForUpdateOrDeleteEntity(userOwnedEntity, currentUser))
                .isInstanceOf(CustomAuthorizationException.class)
                .hasMessage("You don't have permission to make this request");
    }
}