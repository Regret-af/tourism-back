package com.af.tourism.securitylite;

import com.af.tourism.exception.UnauthorizedException;
import com.af.tourism.security.SecurityUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 登录上下文兼容层。
 * 当前已迁移到底层读取 SecurityContextHolder，保留原有调用方式以降低改造成本。
 */
public final class AuthContext {

    private AuthContext() {
    }

    public static void setCurrentUser(LoginUser currentUser) {
        if (currentUser == null || currentUser.getUserId() == null) {
            SecurityContextHolder.clearContext();
            return;
        }
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(currentUser, null, null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    public static LoginUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof SecurityUser) {
            SecurityUser securityUser = (SecurityUser) principal;
            return new LoginUser(securityUser.getUserId());
        }
        if (principal instanceof LoginUser) {
            return (LoginUser) principal;
        }
        return null;
    }

    public static boolean isLoggedIn() {
        LoginUser currentUser = getCurrentUser();
        return currentUser != null && currentUser.getUserId() != null;
    }

    public static Long getCurrentUserId() {
        LoginUser currentUser = getCurrentUser();
        return currentUser != null ? currentUser.getUserId() : null;
    }

    public static Long requireCurrentUserId() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            throw new UnauthorizedException("未登录");
        }
        return userId;
    }

    public static void clear() {
        SecurityContextHolder.clearContext();
    }
}
