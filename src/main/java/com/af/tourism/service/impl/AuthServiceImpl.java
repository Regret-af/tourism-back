package com.af.tourism.service.impl;

import com.af.tourism.common.ErrorCode;
import com.af.tourism.converter.AuthConverter;
import com.af.tourism.exception.BusinessException;
import com.af.tourism.mapper.UserMapper;
import com.af.tourism.pojo.dto.LoginDTO;
import com.af.tourism.pojo.entity.User;
import com.af.tourism.pojo.vo.LoginVO;
import com.af.tourism.securitylite.JwtService;
import com.af.tourism.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AuthServiceImpl implements AuthService {

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
        String account = request.getAccount();
        String password = request.getPassword();
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
        String token = jwtService.generateToken(user.getId());
        LoginVO response = authConverter.toLoginVO(user, token, "Bearer", expireSeconds);

        return response;
    }

    private boolean passwordMatches(String rawPassword, String storedPassword) {
        if (storedPassword == null) {
            return false;
        }
        return storedPassword.equals(rawPassword);
    }
}
