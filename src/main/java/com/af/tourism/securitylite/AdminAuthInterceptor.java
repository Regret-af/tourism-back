package com.af.tourism.securitylite;

import com.af.tourism.common.enums.RoleCode;
import com.af.tourism.exception.ForbiddenException;
import com.af.tourism.exception.UnauthorizedException;
import com.af.tourism.mapper.RoleMapper;
import com.af.tourism.service.helper.UserCheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 管理端统一鉴权拦截器。
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminAuthInterceptor implements HandlerInterceptor {

    private final UserCheckService userCheckService;
    private final RoleMapper roleMapper;

    // 执行接口前执行
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 1.获取当前用户并判断是否登录
        Long userId = AuthContext.getCurrentUserId();
        if (userId == null) {
            log.warn("管理端接口访问失败，未登录，uri={}, method={}", request.getRequestURI(), request.getMethod());
            throw new UnauthorizedException("未登录");
        }

        // 2.判断用户是否存在与可用
        userCheckService.requireActiveUser(userId);

        // 3.查询用户角色列表
        List<String> roleCodes = roleMapper.selectRoleCodesByUserId(userId);

        // 4.判断当前用户是否有管理权限
        if (roleCodes == null || !roleCodes.contains(RoleCode.ADMIN.getValue())) {
            log.warn("管理端接口访问失败，无管理员权限，userId={}, uri={}, method={}",
                    userId, request.getRequestURI(), request.getMethod());
            throw new ForbiddenException("无管理端访问权限");
        }

        return true;
    }
}
