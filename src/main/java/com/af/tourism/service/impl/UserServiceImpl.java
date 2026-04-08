package com.af.tourism.service.impl;

import com.af.tourism.common.ErrorCode;
import com.af.tourism.converter.AuthConverter;
import com.af.tourism.converter.UserConverter;
import com.af.tourism.exception.BusinessException;
import com.af.tourism.mapper.RoleMapper;
import com.af.tourism.mapper.UserMapper;
import com.af.tourism.pojo.dto.app.UserPasswordUpdateDTO;
import com.af.tourism.pojo.dto.app.UserProfileUpdateDTO;
import com.af.tourism.pojo.entity.User;
import com.af.tourism.pojo.vo.app.UserPublicVO;
import com.af.tourism.pojo.vo.common.UserVO;
import com.af.tourism.service.UserService;
import com.af.tourism.service.helper.UserCheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).{8,32}$");

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;

    private final UserCheckService userCheckService;
    private final PasswordEncoder passwordEncoder;

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

    /**
     * 修改当前用户密码
     * @param userId 用户 id
     * @param passwordUpdateDTO 新旧密码
     * @return 操作结果
     */
    @Override
    public Boolean updatePassword(Long userId, UserPasswordUpdateDTO passwordUpdateDTO) {
        // 1.获取用户，判断用户是否异常
        User user = userCheckService.requireActiveUser(userId);

        // 2.判断旧密码是否正确
        if (!passwordEncoder.matches(passwordUpdateDTO.getCurrentPassword(), user.getPasswordHash())) {
            log.info("修改密码失败，旧密码不正确");
            throw new BusinessException(ErrorCode.PARAM_INVALID, "旧密码不正确");
        }

        // 3.判断新旧密码是否相同
        if (passwordUpdateDTO.getNewPassword().equals(passwordUpdateDTO.getCurrentPassword())) {
            log.info("修改密码失败，新旧密码不能相同");
            throw new BusinessException(ErrorCode.PARAM_INVALID, "新旧密码不能相同");
        }

        // 4.判断新密码是否符合格式要求
        if (!PASSWORD_PATTERN.matcher(passwordUpdateDTO.getNewPassword()).matches()) {
            log.info("修改密码失败，新密码不符合格式要求");
            throw new BusinessException(ErrorCode.PARAM_INVALID, "新密码不符合格式要求");
        }

        // 5.修改新密码
        user.setPasswordHash(passwordEncoder.encode(passwordUpdateDTO.getNewPassword()));
        userMapper.updateById(user);

        return true;
    }
}
