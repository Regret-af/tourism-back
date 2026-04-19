package com.af.tourism.security.service;

import com.af.tourism.common.ErrorCode;
import com.af.tourism.exception.BusinessException;
import com.af.tourism.security.model.SecurityUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

/**
 * 基于 Spring Security 认证器的登录认证服务。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityAuthenticationService {

    private final AuthenticationManager authenticationManager;

    public SecurityUser authenticate(String email, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            return (SecurityUser) authentication.getPrincipal();
        } catch (DisabledException ex) {
            log.warn("登录失败，账号已禁用，email={}", email);
            throw new BusinessException(ErrorCode.USER_DISABLED, "账号已禁用");
        } catch (BadCredentialsException ex) {
            log.warn("登录失败，邮箱或密码错误，email={}", email);
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "邮箱或密码错误");
        } catch (AuthenticationException ex) {
            log.warn("登录认证失败，email={}", email, ex);
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "登录认证失败");
        }
    }
}
