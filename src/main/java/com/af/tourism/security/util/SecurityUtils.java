package com.af.tourism.security.util;

import com.af.tourism.exception.UnauthorizedException;
import com.af.tourism.security.model.SecurityUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Spring Security 上下文工具类。
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static SecurityUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof SecurityUser) {
            return (SecurityUser) principal;
        }
        return null;
    }

    public static Long getCurrentUserId() {
        SecurityUser currentUser = getCurrentUser();
        return currentUser != null ? currentUser.getUserId() : null;
    }

    public static Long requireCurrentUserId() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            throw new UnauthorizedException("未登录");
        }
        return userId;
    }
}
