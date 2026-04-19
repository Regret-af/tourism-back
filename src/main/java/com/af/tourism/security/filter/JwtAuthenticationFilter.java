package com.af.tourism.security.filter;

import com.af.tourism.common.constants.AuthConstants;
import com.af.tourism.exception.UnauthorizedException;
import com.af.tourism.security.model.SecurityUser;
import com.af.tourism.security.service.JwtService;
import com.af.tourism.security.service.SecurityUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT 认证过滤器。
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final SecurityUserDetailsService securityUserDetailsService;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 1.获取 Authorization 请求头
        String header = request.getHeader(AuthConstants.AUTHORIZATION_HEADER);

        // 2.请求头为空直接放行
        if (!StringUtils.hasText(header)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 3.如果请求头格式是否正确
            if (!header.startsWith(AuthConstants.BEARER_PREFIX)) {
                throw new UnauthorizedException("Authorization头格式错误");
            }

            // 4.获取 token 并进行校验
            String token = header.substring(AuthConstants.BEARER_PREFIX.length()).trim();
            if (!StringUtils.hasText(token)) {
                throw new UnauthorizedException("Token不能为空");
            }

            // 5.解析 token 获取用户 id 并获取用户信息
            Long userId = jwtService.parseUserId(token);
            SecurityUser securityUser = securityUserDetailsService.loadUserByUserId(userId);

            // 6.构建 Spring Security 的认证对象
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(securityUser, null, securityUser.getAuthorities());

            // 7.将认证对象存入 SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            // 8.放行
            filterChain.doFilter(request, response);
        } catch (UnauthorizedException ex) { // token 解析或格式错误
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(request, response, new BadCredentialsException(ex.getMessage(), ex));
        } catch (Exception ex) { // 其他异常
            log.warn("JWT认证失败，uri={}, method={}", request.getRequestURI(), request.getMethod(), ex);
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(request, response, new BadCredentialsException("未登录或Token无效", ex));
        }
    }
}
