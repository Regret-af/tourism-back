package com.af.tourism.securitylite;

import com.af.tourism.exception.UnauthorizedException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 读取 Authorization Bearer Token，解析后写入 AuthContext。
 * 对未携带 Token 的请求不直接拦截，业务接口自行校验登录。
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final String BEARER_PREFIX = "Bearer ";
    private final JwtService jwtService;

    public AuthInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String header = request.getHeader("Authorization");
        if (!StringUtils.hasText(header)) {
            return true;
        }
        if (!header.startsWith(BEARER_PREFIX)) {
            throw new UnauthorizedException("Authorization 头格式错误");
        }
        String token = header.substring(BEARER_PREFIX.length()).trim();
        if (!StringUtils.hasText(token)) {
            throw new UnauthorizedException("Token 不能为空");
        }
        Long userId = jwtService.parseUserId(token);
        AuthContext.setUserId(userId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AuthContext.clear();
    }
}
