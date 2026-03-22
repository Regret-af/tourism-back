package com.af.tourism.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.af.tourism.common.ErrorCode;
import com.af.tourism.converter.AuthConverter;
import com.af.tourism.exception.BusinessException;
import com.af.tourism.mapper.RoleMapper;
import com.af.tourism.mapper.UserMapper;
import com.af.tourism.mapper.UserRoleMapper;
import com.af.tourism.pojo.dto.LoginDTO;
import com.af.tourism.pojo.dto.RegisterDTO;
import com.af.tourism.pojo.entity.Role;
import com.af.tourism.pojo.entity.User;
import com.af.tourism.pojo.entity.UserRole;
import com.af.tourism.pojo.vo.LoginVO;
import com.af.tourism.pojo.vo.RegisterVO;
import com.af.tourism.securitylite.JwtService;
import com.af.tourism.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private static final String DEFAULT_TOKEN_TYPE = "Bearer";
    private static final String DEFAULT_ROLE_CODE = "USER";
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9_]{3,20}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).{8,32}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthConverter authConverter;

    /**
     * 用户登录
     * @param request 登录请求参数
     * @return 登录响应
     */
    @Override
    public LoginVO login(LoginDTO request) {
        // 1.获取参数
        String email = request.getEmail();
        String password = request.getPassword();

        // 2.验证登录请求参数格式
        validateLoginRequest(email, password);

        // 3.根据邮箱查询用户
        User user = userMapper.selectByEmail(email);
        // 3.1.查询失败或密码错误
        if (user == null || !passwordEncoder.matches(password, user.getPasswordHash())) {
            log.warn("登录失败，邮箱或密码错误");
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "邮箱或密码错误");
        }
        // 3.2.用户状态异常或不可用
        if (user.getStatus() != null && user.getStatus() == 0) {
            log.warn("登录失败，账号状态异常，id={}", user.getId());
            throw new BusinessException(ErrorCode.USER_DISABLED, "账号已禁用");
        }

        // 4.封装返回值
        // 4.1.查询该用户角色
        List<String> roles = roleMapper.selectRoleCodesByUserId(user.getId());
        // 4.2.为该用户生成JWT令牌
        String token = jwtService.generateToken(user.getId());

        return authConverter.toLoginVO(user, roles, token, DEFAULT_TOKEN_TYPE, jwtService.getExpireSeconds());
    }

    /**
     * 用户注册
     * @param request 注册请求参数
     * @return 注册响应
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RegisterVO register(RegisterDTO request) {
        // 1.验证注册请求参数
        validateRegisterRequest(request);

        // 2.查询参数合法性
        if (userMapper.selectByEmail(request.getEmail()) != null) {
            log.warn("注册失败，该邮箱已注册，email={}", request.getEmail());
            throw new BusinessException(ErrorCode.CONFLICT, "邮箱已注册");
        }
        if (userMapper.selectByUsername(request.getUsername()) != null) {
            log.warn("注册失败，该用户名已存在 username={}", request.getUsername());
            throw new BusinessException(ErrorCode.CONFLICT, "用户名已存在");
        }

        // 3.用户成功注册，插入数据库
        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setNickname(generateDefaultNickname());
        user.setStatus(1);

        try {
            userMapper.insert(user);
        } catch (DuplicateKeyException ex) {
            log.warn("注册失败，邮箱或用户名已存在，email={}, username={}", request.getEmail(), request.getUsername());
            throw new BusinessException(ErrorCode.CONFLICT, "邮箱或用户名已存在");
        }

        // 4.将用户与角色进行关联
        // 4.1.查看默认角色是否存在
        Role defaultRole = roleMapper.selectByRoleCode(DEFAULT_ROLE_CODE);
        if (defaultRole == null) {
            log.error("默认角色不存在，注册功能无法使用，请尽快创建默认角色或更改代码");
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "默认角色不存在");
        }

        // 4.2.为新注册用户分配默认角色
        UserRole userRole = new UserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(defaultRole.getId());
        try {
            userRoleMapper.insert(userRole);
        } catch (DuplicateKeyException ex) {
            log.warn("注册失败，用户角色关联关系已存在，userId={}， roleId={}", user.getId(), defaultRole.getId());
            throw new BusinessException(ErrorCode.CONFLICT, "用户角色关联已存在");
        }

        return authConverter.toRegisterVO(user);
    }

    // 验证登录请求参数
    private void validateLoginRequest(String email, String password) {
        if (!StringUtils.hasText(email) || !StringUtils.hasText(password)) {
            log.warn("登录失败，参数错误，email={}", email);
            throw new BusinessException(ErrorCode.PARAM_INVALID, "参数错误");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            log.warn("登录失败，邮箱格式不正确，email={}", email);
            throw new BusinessException(ErrorCode.PARAM_INVALID, "邮箱格式不正确");
        }
    }

    // 验证注册请求参数
    private void validateRegisterRequest(RegisterDTO request) {
        if (!StringUtils.hasText(request.getEmail())
                || !StringUtils.hasText(request.getUsername())
                || !StringUtils.hasText(request.getPassword())) {
            log.warn("注册失败，参数错误，email={}, username={}", request.getEmail(), request.getUsername()
            );
            throw new BusinessException(ErrorCode.PARAM_INVALID, "参数错误");
        }
        if (!EMAIL_PATTERN.matcher(request.getEmail()).matches()) {
            log.warn("注册失败，邮箱格式不正确，email={}", request.getEmail());
            throw new BusinessException(ErrorCode.PARAM_INVALID, "邮箱格式不正确");
        }
        if (!USERNAME_PATTERN.matcher(request.getUsername()).matches()) {
            log.warn("注册失败，用户名格式不正确，username={}", request.getUsername());
            throw new BusinessException(ErrorCode.PARAM_INVALID, "用户名格式不正确");
        }
        if (!PASSWORD_PATTERN.matcher(request.getPassword()).matches()) {
            log.warn("注册失败，密码格式不正确");
            throw new BusinessException(ErrorCode.PARAM_INVALID, "密码格式不正确");
        }
    }

    // 生成随机用户名
    private String generateDefaultNickname() {
        return "用户" + RandomUtil.randomNumbers(6);
    }
}
