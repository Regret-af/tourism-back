package com.af.tourism.service.impl.admin;

import com.af.tourism.common.ErrorCode;
import com.af.tourism.common.enums.RoleCode;
import com.af.tourism.common.enums.UserStatus;
import com.af.tourism.exception.BusinessException;
import com.af.tourism.mapper.RoleMapper;
import com.af.tourism.mapper.UserMapper;
import com.af.tourism.pojo.dto.admin.UserOptionQueryDTO;
import com.af.tourism.pojo.dto.admin.UserQueryDTO;
import com.af.tourism.pojo.entity.User;
import com.af.tourism.pojo.vo.admin.UserDetailForAdminVO;
import com.af.tourism.pojo.vo.admin.UserForAdminVO;
import com.af.tourism.pojo.vo.admin.UserOptionForAdminVO;
import com.af.tourism.pojo.vo.admin.UserStatsForAdminVO;
import com.af.tourism.pojo.vo.common.PageResponse;
import com.af.tourism.security.util.SecurityUtils;
import com.af.tourism.service.admin.AdminUserService;
import com.af.tourism.service.cache.CacheClearSupport;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminUserServiceImpl implements AdminUserService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;

    private final CacheClearSupport cacheClearSupport;

    /**
     * 获取用户列表
     * @param queryDTO 查询参数
     * @return 符合条件的用户列表
     */
    @Override
    public PageResponse<UserForAdminVO> listUsers(UserQueryDTO queryDTO) {
        // 1.开启分页查询
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 2.进行查询
        List<UserForAdminVO> list = userMapper.selectUserList(queryDTO);
        PageInfo<UserForAdminVO> pageInfo = new PageInfo<>(list);

        // 3.封装返回值
        PageResponse<UserForAdminVO> response = new PageResponse<>();
        response.setList(list);
        response.setPageNum(pageInfo.getPageNum());
        response.setPageSize(pageInfo.getPageSize());
        response.setTotal(pageInfo.getTotal());

        return response;
    }

    /**
     * 获取用户下拉选项
     * @param queryDTO 查询参数
     * @return 用户下拉选项列表
     */
    @Override
    public List<UserOptionForAdminVO> listUserOptions(UserOptionQueryDTO queryDTO) {
        return userMapper.selectUserOptions(queryDTO);
    }

    /**
     * 获取用户详情
     * @param userId 用户 id
     * @return 用户详情
     */
    @Override
    public UserDetailForAdminVO getUserDetail(Long userId) {
        // 1.查询用户基础详情信息
        UserDetailForAdminVO detail = userMapper.selectUserDetailById(userId);
        if (detail == null) {
            log.warn("用户不存在，userId={}", userId);
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }

        // 2.查询用户统计信息
        UserStatsForAdminVO stats = userMapper.selectUserStatsById(userId);

        // 3.进行组装
        if (stats != null) {
            detail.setDiaryCount(stats.getDiaryCount());
            detail.setCommentCount(stats.getCommentCount());
            detail.setUploadCount(stats.getUploadCount());
        } else {
            detail.setDiaryCount(0L);
            detail.setCommentCount(0L);
            detail.setUploadCount(0L);
        }

        return detail;
    }

    /**
     * 修改用户状态
     * @param userId 用户 id
     * @param status 用户状态
     */
    @Override
    public void updateUserStatus(Long userId, UserStatus status) {
        // 1.查询用户是否存在
        User targetUser = userMapper.selectById(userId);
        if (targetUser == null) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "该用户不存在");
        }

        // 2. 校验状态是否合法
        if (status == null) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "用户状态不合法");
        }

        // 3. 禁止修改自己
        Long currentUserId = SecurityUtils.requireCurrentUserId();
        if (Objects.equals(currentUserId, userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "不能修改自己的状态");
        }

        // 4.查询状态是否正确，保证幂等性
        Integer targetStatus = status.getValue();
        if (Objects.equals(targetUser.getStatus(), targetStatus)) {
            return;
        }

        // 5.查询当前用户是否有权限修改用户状态
        int currentUserMaxRoleLevel = getUserMaxRoleLevel(currentUserId);
        int targetUserMaxRoleLevel = getUserMaxRoleLevel(userId);

        if (currentUserMaxRoleLevel <= targetUserMaxRoleLevel) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "只能修改权限低于自己的用户状态");
        }

        // 6.更新状态
        targetUser.setStatus(targetStatus);
        userMapper.updateById(targetUser);

        // 7.清除缓存中对应用户的信息
        cacheClearSupport.clearAuthUserContext(userId);
    }

    /**
     * 获取用户当前启用角色中的最大权限等级
     */
    private int getUserMaxRoleLevel(Long userId) {
        List<String> roleCodes = roleMapper.selectRoleCodesByUserId(userId);
        if (roleCodes == null || roleCodes.isEmpty()) {
            return 0;
        }

        return roleCodes.stream()
                .map(RoleCode::fromCode)
                .filter(Objects::nonNull)
                .mapToInt(RoleCode::getLevel)
                .max()
                .orElse(0);
    }
}
