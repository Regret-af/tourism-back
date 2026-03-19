package com.af.tourism.service.impl;

import com.af.tourism.common.ErrorCode;
import com.af.tourism.exception.BusinessException;
import com.af.tourism.mapper.UserMapper;
import com.af.tourism.pojo.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 检查用户状态服务实现。
 */
@Service
@RequiredArgsConstructor
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
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "账号不可用");
        }

        return user;
    }
}
