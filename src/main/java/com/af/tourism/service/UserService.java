package com.af.tourism.service;

import com.af.tourism.pojo.dto.admin.UserQueryDTO;
import com.af.tourism.pojo.dto.app.UserPasswordUpdateDTO;
import com.af.tourism.pojo.dto.app.UserProfileUpdateDTO;
import com.af.tourism.pojo.entity.User;
import com.af.tourism.pojo.vo.admin.UserForAdminVO;
import com.af.tourism.pojo.vo.app.UserPublicVO;
import com.af.tourism.pojo.vo.common.PageResponse;
import com.af.tourism.pojo.vo.common.UserVO;

import javax.validation.Valid;
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

    /**
     * 修改当前用户密码
     * @param userId 用户 id
     * @param passwordUpdateDTO 新旧密码
     * @return 操作结果
     */
    Boolean updatePassword(Long userId, @Valid UserPasswordUpdateDTO passwordUpdateDTO);

    /**
     * 获取用户列表
     * @param queryDTO 查询参数
     * @return 符合条件的用户列表
     */
    PageResponse<UserForAdminVO> listUsers(@Valid UserQueryDTO queryDTO);
}
