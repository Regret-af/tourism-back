package com.af.tourism.securitylite;

import com.af.tourism.exception.UnauthorizedException;

/**
 * 登录上下文，基于 ThreadLocal 保存当前登录用户。
 */
public final class AuthContext {

    private static final ThreadLocal<LoginUser> CURRENT_USER_HOLDER = new ThreadLocal<>();

    private AuthContext() {
    }

    public static void setCurrentUser(LoginUser currentUser) {
        CURRENT_USER_HOLDER.set(currentUser);
    }

    public static LoginUser getCurrentUser() {
        return CURRENT_USER_HOLDER.get();
    }

    // 判断是否登录
    public static boolean isLoggedIn() {
        LoginUser currentUser = CURRENT_USER_HOLDER.get();
        return currentUser != null && currentUser.getUserId() != null;
    }

    // 获取当前用户id (登录可选场景)
    public static Long getCurrentUserId() {
        LoginUser currentUser = CURRENT_USER_HOLDER.get();
        return currentUser != null ? currentUser.getUserId() : null;
    }

    // 获取当前用户id (必须登录场景)
    public static Long requireCurrentUserId() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            throw new UnauthorizedException("未登录");
        }
        return userId;
    }

    public static void clear() {
        CURRENT_USER_HOLDER.remove();
    }
}
