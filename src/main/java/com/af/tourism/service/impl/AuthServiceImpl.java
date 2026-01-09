package com.af.tourism.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.af.tourism.common.ErrorCode;
import com.af.tourism.converter.AuthConverter;
import com.af.tourism.exception.BusinessException;
import com.af.tourism.mapper.UserMapper;
import com.af.tourism.pojo.dto.LoginDTO;
import com.af.tourism.pojo.dto.RegisterDTO;
import com.af.tourism.pojo.entity.User;
import com.af.tourism.pojo.vo.LoginVO;
import com.af.tourism.pojo.vo.UserVO;
import com.af.tourism.securitylite.JwtService;
import com.af.tourism.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9_]{3,20}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).{8,32}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final AuthConverter authConverter;

    @Value("${app.jwt.expire-seconds:86400}")
    private long expireSeconds;

    public AuthServiceImpl(UserMapper userMapper, JwtService jwtService, AuthConverter authConverter) {
        this.userMapper = userMapper;
        this.jwtService = jwtService;
        this.authConverter = authConverter;
    }

    @Override
    public LoginVO login(LoginDTO request) {
        // 1.获取数据
        String account = request.getAccount();
        String password = request.getPassword();

        // 2.进行参数校验
        if (!StringUtils.hasText(account) || !StringUtils.hasText(password)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "账号或密码错误");
        }
        User user = userMapper.selectByAccount(account);
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "账号或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(ErrorCode.USER_DISABLED, "用户已禁用");
        }
        if (!passwordMatches(password, user.getPassword())) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "账号或密码错误");
        }

        // 3.生成JWT令牌
        String token = jwtService.generateToken(user.getId());

        // 4.返回数据
        LoginVO response = authConverter.toLoginVO(user, token, "Bearer", expireSeconds);

        return response;
    }

    @Override
    public UserVO register(RegisterDTO request) {
        // 1.获取数据
        String username = request.getUsername();
        String email = request.getEmail();
        String password = request.getPassword();
        String confirmPassword = request.getConfirmPassword();

        // 2.再次校验数据
        if (!StringUtils.hasText(username) || !StringUtils.hasText(email) || !StringUtils.hasText(password)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "参数错误");
        }
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "用户名格式不正确");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "邮箱格式不正确");
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "密码格式不正确");
        }
        if (confirmPassword != null && !confirmPassword.equals(password)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "两次密码不一致");
        }

        if (userMapper.selectByUsername(username) != null) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "用户名已存在");
        }
        if (userMapper.selectByEmail(email) != null) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "该邮箱已注册");
        }

        // 3.组合用户信息
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(encryptPassword(password));
        user.setStatus(1);

        // 4.执行插入
        userMapper.insert(user);

        return authConverter.toUserVO(user);
    }

    private boolean passwordMatches(String rawPassword, String storedPassword) {
        if (storedPassword == null) {
            return false;
        }
        return storedPassword.equals(encryptPassword(rawPassword));
    }

    private String encryptPassword(String rawPassword) {
        return DigestUtil.sha256Hex(rawPassword);
    }
}
