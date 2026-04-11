package com.af.tourism.service.impl.admin;

import com.af.tourism.common.enums.AttractionStatus;
import com.af.tourism.common.enums.DiaryCommentStatus;
import com.af.tourism.common.enums.DiaryDeletedStatus;
import com.af.tourism.common.enums.DiaryStatus;
import com.af.tourism.common.enums.UserStatus;
import com.af.tourism.mapper.AttractionCategoryMapper;
import com.af.tourism.mapper.AttractionMapper;
import com.af.tourism.mapper.DiaryCommentMapper;
import com.af.tourism.mapper.DiaryMapper;
import com.af.tourism.mapper.UserMapper;
import com.af.tourism.pojo.entity.Attraction;
import com.af.tourism.pojo.entity.AttractionCategory;
import com.af.tourism.pojo.entity.DiaryComment;
import com.af.tourism.pojo.entity.TravelDiary;
import com.af.tourism.pojo.entity.User;
import com.af.tourism.pojo.vo.admin.DashboardOverviewVO;
import com.af.tourism.service.admin.AdminDashboardService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 管理端统计看板服务实现
 */
@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final UserMapper userMapper;
    private final AttractionMapper attractionMapper;
    private final AttractionCategoryMapper attractionCategoryMapper;
    private final DiaryMapper diaryMapper;
    private final DiaryCommentMapper diaryCommentMapper;

    /**
     * 获取看板总览
     * @return 看板总览
     */
    @Override
    public DashboardOverviewVO getOverview() {
        DashboardOverviewVO vo = new DashboardOverviewVO();

        vo.setUserTotal(countUsers(null));
        vo.setUserEnabledCount(countUsers(UserStatus.ENABLED.getCode()));
        vo.setUserDisabledCount(countUsers(UserStatus.DISABLED.getCode()));

        vo.setAttractionTotal(countAttractions(null));
        vo.setAttractionOnlineCount(countAttractions(AttractionStatus.ONLINE.getValue()));
        vo.setAttractionOfflineCount(countAttractions(AttractionStatus.OFFLINE.getValue()));

        vo.setDiaryTotal(countDiariesByStatus(null));
        vo.setDiaryOnlineCount(countDiariesByStatus(DiaryStatus.PUBLIC.getValue()));
        vo.setDiaryOfflineCount(countDiariesByStatus(DiaryStatus.OFFLINE.getValue()));
        vo.setDiaryPendingReviewCount(countDiariesByStatus(DiaryStatus.PENDING.getValue()));
        vo.setDiaryRejectedCount(countDiariesByStatus(DiaryStatus.REJECTED.getValue()));
        vo.setDiaryDeletedCount(countDiariesByDeleted(DiaryDeletedStatus.DELETED.getValue()));

        return vo;
    }

    /**
     * 指定状态的用户数量
     * @param status 状态
     * @return 用户数量
     */
    private Long countUsers(Integer status) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        return userMapper.selectCount(wrapper);
    }

    /**
     * 指定状态的景点数量
     * @param status 状态
     * @return 景点数量
     */
    private Long countAttractions(Integer status) {
        LambdaQueryWrapper<Attraction> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Attraction::getStatus, status);
        }
        return attractionMapper.selectCount(wrapper);
    }

    /**
     * 指定状态的日记数量
     * @param status 状态
     * @return 日记数量
     */
    private Long countDiariesByStatus(Integer status) {
        LambdaQueryWrapper<TravelDiary> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(TravelDiary::getStatus, status);
        }
        return diaryMapper.selectCount(wrapper);
    }

    /**
     * 删除/未删除的日记数量
     * @param isDeleted 是否删除
     * @return 日记数量
     */
    private Long countDiariesByDeleted(Integer isDeleted) {
        LambdaQueryWrapper<TravelDiary> wrapper = new LambdaQueryWrapper<>();
        if (isDeleted != null) {
            wrapper.eq(TravelDiary::getIsDeleted, isDeleted);
        }
        return diaryMapper.selectCount(wrapper);
    }
}
