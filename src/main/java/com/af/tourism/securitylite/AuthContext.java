package com.af.tourism.securitylite;

/**
 * 登录上下文，基于 ThreadLocal 保存简单的用户标识。
 */
public class AuthContext {

    private static final ThreadLocal<Long> USER_HOLDER = new ThreadLocal<>();

    public static void setUserId(Long userId) {
        USER_HOLDER.set(userId);
    }

    public static Long getUserId() {
        return USER_HOLDER.get();
    }

    public static void clear() {
        USER_HOLDER.remove();
    }
}
