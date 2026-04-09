package com.af.tourism.service.impl.admin;

import cn.hutool.core.bean.BeanUtil;
import com.af.tourism.common.ErrorCode;
import com.af.tourism.exception.BusinessException;
import com.af.tourism.mapper.UserMapper;
import com.af.tourism.pojo.dto.admin.UserQueryDTO;
import com.af.tourism.pojo.vo.admin.UserDetailForAdminVO;
import com.af.tourism.pojo.vo.admin.UserForAdminVO;
import com.af.tourism.pojo.vo.admin.UserStatsForAdminVO;
import com.af.tourism.pojo.vo.common.PageResponse;
import com.af.tourism.service.admin.AdminUserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminUserServiceImpl implements AdminUserService {

    private final UserMapper userMapper;

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
}
