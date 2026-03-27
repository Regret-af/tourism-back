package com.af.tourism.service;

import com.af.tourism.pojo.dto.UserProfileUpdateDTO;
import com.af.tourism.pojo.entity.User;
import com.af.tourism.pojo.vo.UserPublicVO;
import com.af.tourism.pojo.vo.UserVO;

import java.util.List;

public interface UserService {

    /**
     * 通过用户id进行查询
     * @param id 用户id
     * @return 用户信息
     */
    User findById(Long id);

    /**
     * 获取当前用户信息
     * @param userId 用户id
     * @return 用户返回信息
     */
    UserVO getCurrentUserProfile(Long userId);

    /**
     * 查询用户角色列表
     * @param userId 用户id
     * @return 用户角色列表
     */
    List<String> listRoleCodes(Long userId);

    /**
     * 更新当前用户资料
     * @param userId 用户 id
     * @param profileUpdateDTO 用户信息
     * @return 更新后的用户信息
     */
    UserPublicVO updateUserProfile(Long userId, UserProfileUpdateDTO profileUpdateDTO);
}
