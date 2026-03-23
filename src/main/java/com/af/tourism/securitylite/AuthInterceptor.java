package com.af.tourism.securitylite;

import com.af.tourism.common.constants.AuthConstants;
import com.af.tourism.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 读取 Authorization Bearer Token，解析后写入 AuthContext。
 * 对未携带 Token 的请求不直接拦截，由业务接口自行判断是否要求登录。
 */
@Component
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtService jwtService;

    public AuthInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    // 执行接口前拦截
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String header = request.getHeader(AuthConstants.AUTHORIZATION_HEADER);
        // 1.若为空，证明未登录，直接放行，业务接口自行判断
        if (!StringUtils.hasText(header)) {
            return true;
        }
        // 2.判断请求头格式
        if (!header.startsWith(AuthConstants.BEARER_PREFIX)) {
            log.warn("请求Authorization头格式错误，uri={}, method={}", request.getRequestURI(), request.getMethod());
            throw new UnauthorizedException("Authorization头格式错误");
        }

        // 3.校验 token
        String token = header.substring(AuthConstants.BEARER_PREFIX.length()).trim();
        if (!StringUtils.hasText(token)) {
            log.warn("请求token为空，uri={}, method={}", request.getRequestURI(), request.getMethod());
            throw new UnauthorizedException("Token不能为空");
        }

        // 4.解析 token，并放入上下文
        AuthContext.setCurrentUser(jwtService.parseLoginUser(token));

        // 5.放行
        return true;
    }

    // 由于线程复用，执行结束必须清除上下文
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AuthContext.clear();
    }
}
