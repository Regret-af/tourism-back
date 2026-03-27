package com.af.tourism.service.impl;

import com.af.tourism.common.ErrorCode;
import com.af.tourism.converter.AuthConverter;
import com.af.tourism.converter.UserConverter;
import com.af.tourism.exception.BusinessException;
import com.af.tourism.mapper.RoleMapper;
import com.af.tourism.mapper.UserMapper;
import com.af.tourism.pojo.dto.UserProfileUpdateDTO;
import com.af.tourism.pojo.entity.User;
import com.af.tourism.pojo.vo.UserPublicVO;
import com.af.tourism.pojo.vo.UserVO;
import com.af.tourism.service.UserService;
import com.af.tourism.service.helper.UserCheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;

    private final UserCheckService userCheckService;

    private final AuthConverter authConverter;
    private final UserConverter userConverter;

    /**
     * 通过用户id进行查询
     * @param id 用户id
     * @return 用户信息
     */
    @Override
    public User findById(Long id) {
        return userMapper.selectById(id);
    }

    /**
     * 获取当前用户信息
     * @param userId 用户id
     * @return 用户返回信息
     */
    @Override
    public UserVO getCurrentUserProfile(Long userId) {
        User user = findById(userId);
        if (user == null) {
            log.warn("用户不存在，userId={}", userId);
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        return authConverter.toUserVO(user, listRoleCodes(userId));
    }

    /**
     * 查询用户角色列表
     * @param userId 用户id
     * @return 用户角色列表
     */
    @Override
    public List<String> listRoleCodes(Long userId) {
        return roleMapper.selectRoleCodesByUserId(userId);
    }

    /**
     * 更新当前用户资料
     * @param userId 用户 id
     * @param profileUpdateDTO 用户信息
     * @return 更新后的用户信息
     */
    @Override
    public UserPublicVO updateUserProfile(Long userId, UserProfileUpdateDTO profileUpdateDTO) {
        // 1.查看用户是否存在且状态是否正常
        User user = userCheckService.requireActiveUser(userId);

        // 2.更新用户资料
        user.setNickname(profileUpdateDTO.getNickname());
        user.setAvatarUrl(profileUpdateDTO.getAvatarUrl());
        userMapper.updateById(user);

        return userConverter.toUserPublicVO(user);
    }
}
