package com.af.tourism.service.impl.app;

import cn.hutool.core.util.RandomUtil;
import com.af.tourism.common.ErrorCode;
import com.af.tourism.common.constants.AuthConstants;
import com.af.tourism.common.enums.RoleCode;
import com.af.tourism.common.enums.UserStatus;
import com.af.tourism.converter.AuthConverter;
import com.af.tourism.exception.BusinessException;
import com.af.tourism.mapper.RoleMapper;
import com.af.tourism.mapper.UserMapper;
import com.af.tourism.mapper.UserRoleMapper;
import com.af.tourism.pojo.dto.common.LoginDTO;
import com.af.tourism.pojo.dto.app.RegisterDTO;
import com.af.tourism.pojo.entity.Role;
import com.af.tourism.pojo.entity.User;
import com.af.tourism.pojo.entity.UserRole;
import com.af.tourism.pojo.vo.common.LoginVO;
import com.af.tourism.pojo.vo.app.RegisterVO;
import com.af.tourism.pojo.vo.common.UserVO;
import com.af.tourism.securitylite.JwtService;
import com.af.tourism.service.app.AuthService;
import com.af.tourism.service.helper.AuthHelperService;
import com.af.tourism.service.helper.UserCheckService;
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

    private static final RoleCode DEFAULT_ROLE_CODE = RoleCode.USER;
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9_]{3,20}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).{8,32}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final JwtService jwtService;

    private final AuthHelperService authHelperService;

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;

    private final AuthConverter authConverter;

    private final PasswordEncoder passwordEncoder;

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
        authHelperService.validateLoginRequest(email, password);

        // 3.验证用户信息与状态
        User user = authHelperService.validateUser(email, password);

        // 4.封装返回值
        // 4.1.查询该用户角色
        List<String> roles = roleMapper.selectRoleCodesByUserId(user.getId());
        // 4.2.为该用户生成JWT令牌
        String token = jwtService.generateToken(user.getId());

        return authConverter.toLoginVO(user, roles, token, AuthConstants.TOKEN_TYPE, jwtService.getExpireSeconds());
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
        user.setStatus(UserStatus.ENABLED.getCode());
        user.setAvatarUrl("https://tourism-1358828693.cos.ap-beijing.myqcloud.com/users/photo-default-avatar.jpg");

        try {
            userMapper.insert(user);
        } catch (DuplicateKeyException ex) {
            log.warn("注册失败，邮箱或用户名已存在，email={}, username={}", request.getEmail(), request.getUsername());
            throw new BusinessException(ErrorCode.CONFLICT, "邮箱或用户名已存在");
        }

        // 4.将用户与角色进行关联
        // 4.1.查看默认角色是否存在
        Role defaultRole = roleMapper.selectByRoleCode(DEFAULT_ROLE_CODE.name());
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
