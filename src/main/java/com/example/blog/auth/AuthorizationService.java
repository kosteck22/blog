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
        if (isUserAuthorized(entity, currentUser)) {
            return;
        }
        throw new CustomAuthorizationException("You don't have permission to make this request");
    }

    private boolean isUserAuthorized(UserOwnedEntity entity, UserPrincipal currentUser) {
        return isEntityBelongToCurrentUser(entity, currentUser) ||
                isUserAdmin(currentUser);
    }

    private boolean isEntityBelongToCurrentUser(UserOwnedEntity entity, UserPrincipal currentUser) {
        return currentUser.getId().equals(getUserIdFromEntity(entity));
    }

    private boolean isUserAdmin(UserPrincipal currentUser) {
        return currentUser.getAuthorities().contains(new SimpleGrantedAuthority(AppRoles.ROLE_ADMIN.toString()));
    }

    private Long getUserIdFromEntity(UserOwnedEntity entity) {
        return entity.getUser().getId();
    }
}
