package com.af.tourism.service.impl.admin;

import com.af.tourism.common.ErrorCode;
import com.af.tourism.common.constants.AuthConstants;
import com.af.tourism.common.enums.RoleCode;
import com.af.tourism.converter.AuthConverter;
import com.af.tourism.exception.BusinessException;
import com.af.tourism.mapper.RoleMapper;
import com.af.tourism.pojo.dto.common.LoginDTO;
import com.af.tourism.pojo.entity.User;
import com.af.tourism.pojo.vo.common.LoginVO;
import com.af.tourism.pojo.vo.common.UserVO;
import com.af.tourism.security.converter.SecurityConverter;
import com.af.tourism.security.model.SecurityUser;
import com.af.tourism.security.service.JwtService;
import com.af.tourism.security.service.SecurityAuthenticationService;
import com.af.tourism.service.admin.AdminAuthService;
import com.af.tourism.service.helper.AuthHelperService;
import com.af.tourism.service.helper.UserCheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminAuthServiceImpl implements AdminAuthService {

    private final UserCheckService userCheckService;
    private final JwtService jwtService;

    private final AuthHelperService authHelperService;
    private final SecurityAuthenticationService securityAuthenticationService;

    private final RoleMapper roleMapper;

    private final AuthConverter authConverter;
    private final SecurityConverter securityConverter;

    /**
     * 管理员登录
     * @param request 登录参数
     * @return 登录信息响应
     */
    @Override
    public LoginVO adminLogin(LoginDTO request) {
        // 1.获取参数
        String email = request.getEmail();
        String password = request.getPassword();

        // 2.验证登录请求参数格式
        authHelperService.validateLoginRequest(email, password);

        // 3.使用 Spring Security 认证器完成账号密码校验
        SecurityUser securityUser = securityAuthenticationService.authenticate(email, password);

        List<String> roles = securityUser.getRoleCodes();

        // 4.判断是否有管理权限
        requireAdminRole(securityUser.getUserId(), roles);

        // 5.为该用户生成 token 令牌
        String token = jwtService.generateToken(securityUser.getUserId());

        return authConverter.toLoginVO(
                securityConverter.toUser(securityUser),
                roles,
                token,
                AuthConstants.TOKEN_TYPE,
                jwtService.getExpireSeconds()
        );
    }

    /**
     * 获取当前管理员信息
     * @param userId 当前登录管理员 id
     * @return 当前管理员信息
     */
    @Override
    public UserVO getCurrentAdminProfile(Long userId) {
        // 1.判断当前用户是否可用
        User user = userCheckService.requireActiveUser(userId);

        // 2.获取用户角色列表
        List<String> roles = roleMapper.selectRoleCodesByUserId(userId);

        // 3.判断用户是否有管理员权限
        requireAdminRole(userId, roles);

        return authConverter.toUserVO(user, roles);
    }

    /**
     * 管理员登出
     * @param authorizationHeader Authorization 请求头
     */
    @Override
    public void logout(String authorizationHeader) {
        jwtService.blacklist(authorizationHeader);
    }

    /**
     * 验证是否存在管理员权限
     * @param userId 用户 id
     * @param roles 用户角色
     */
    private void requireAdminRole(Long userId, List<String> roles) {
        if (roles == null || !roles.contains(RoleCode.ADMIN.getValue())) {
            log.warn("管理员登录失败，账号无管理员权限，userId={}", userId);
            throw new BusinessException(ErrorCode.FORBIDDEN, "无管理端访问权限");
        }
    }

}
