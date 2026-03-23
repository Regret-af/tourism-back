package com.af.tourism.service.helper;

import com.af.tourism.common.ErrorCode;
import com.af.tourism.exception.BusinessException;
import com.af.tourism.mapper.UserMapper;
import com.af.tourism.pojo.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 检查用户状态服务实现。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserCheckService {

    private final UserMapper userMapper ;

    /**
     * 查询用户账号是否可以正常使用
     * @param userId 用户 id
     * @return 用户实体
     */
    public User requireActiveUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            log.warn("用户不存在，userId={}", userId);
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            log.warn("用户账号状态异常，userId={}, status={}", userId, user.getStatus());
            throw new BusinessException(ErrorCode.FORBIDDEN, "账号不可用");
        }

        return user;
    }
}
