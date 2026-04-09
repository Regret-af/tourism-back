package com.af.tourism.service.helper;

import com.af.tourism.common.ErrorCode;
import com.af.tourism.common.enums.UserStatus;
import com.af.tourism.exception.BusinessException;
import com.af.tourism.mapper.UserMapper;
import com.af.tourism.pojo.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthHelperService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 验证用户登录信息与状态
     * @param email 邮箱
     * @param password 密码
     * @return 用户实体
     */
    public User validateUser(String email, String password) {
        User user = userMapper.selectByEmail(email);
        // 3.1.查询失败或密码错误
        if (user == null || !passwordEncoder.matches(password, user.getPasswordHash())) {
            log.warn("登录失败，邮箱或密码错误");
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "邮箱或密码错误");
        }
        // 3.2.用户状态异常或不可用
        if (!UserStatus.isEnabled(user.getStatus())) {
            log.warn("登录失败，账号状态异常，id={}", user.getId());
            throw new BusinessException(ErrorCode.USER_DISABLED, "账号已禁用");
        }
        return user;
    }

    /**
     * 验证登录请求参数
     * @param email 邮箱
     * @param password 密码
     */
    public void validateLoginRequest(String email, String password) {
        if (!StringUtils.hasText(email) || !StringUtils.hasText(password)) {
            log.warn("登录失败，参数错误，email={}", email);
            throw new BusinessException(ErrorCode.PARAM_INVALID, "参数错误");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            log.warn("登录失败，邮箱格式不正确，email={}", email);
            throw new BusinessException(ErrorCode.PARAM_INVALID, "邮箱格式不正确");
        }
    }
}
