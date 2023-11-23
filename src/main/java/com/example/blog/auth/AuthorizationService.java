package com.example.blog.auth;

import com.example.blog.exception.CustomAuthorizationException;
import com.example.blog.role.AppRoles;
import com.example.blog.security.UserPrincipal;
import com.example.blog.user.UserOwnedEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    public void hasAuthorizationForUpdateOrDeleteEntity(UserOwnedEntity entity, UserPrincipal currentUser) {
        if (!currentUser.getId().equals(getUserId(entity)) ||
                !currentUser.getAuthorities().contains(new SimpleGrantedAuthority(AppRoles.ROLE_ADMIN.toString()))) {
            throw new CustomAuthorizationException("You don't have permission to make this request");
        }
    }

    private Long getUserId(UserOwnedEntity entity) {
        return entity.getUser().getId();
    }
}
