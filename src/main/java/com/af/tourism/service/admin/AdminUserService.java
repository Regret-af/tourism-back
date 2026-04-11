package com.af.tourism.service.admin;

import com.af.tourism.common.enums.UserStatus;
import com.af.tourism.pojo.dto.admin.UserOptionQueryDTO;
import com.af.tourism.pojo.dto.admin.UserQueryDTO;
import com.af.tourism.pojo.vo.admin.UserDetailForAdminVO;
import com.af.tourism.pojo.vo.admin.UserForAdminVO;
import com.af.tourism.pojo.vo.admin.UserOptionForAdminVO;
import com.af.tourism.pojo.vo.common.PageResponse;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

public interface AdminUserService {

    /**
     * 获取用户列表
     * @param queryDTO 查询参数
     * @return 符合条件的用户列表
     */
    PageResponse<UserForAdminVO> listUsers(@Valid UserQueryDTO queryDTO);

    /**
     * 获取用户下拉选项
     * @param queryDTO 查询参数
     * @return 用户下拉选项列表
     */
    List<UserOptionForAdminVO> listUserOptions(@Valid UserOptionQueryDTO queryDTO);

    /**
     * 获取用户详情
     * @param userId 用户 id
     * @return 用户详情
     */
    UserDetailForAdminVO getUserDetail(Long userId);

    /**
     * 修改用户状态
     * @param userId 用户 id
     * @param status 用户状态
     */
    void updateUserStatus(@Min(value = 1, message = "userId不能小于1") Long userId, UserStatus status);
}
