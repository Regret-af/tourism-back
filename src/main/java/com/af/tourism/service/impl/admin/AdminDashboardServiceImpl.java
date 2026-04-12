package com.af.tourism.service.impl.admin;

import com.af.tourism.common.enums.AttractionStatus;
import com.af.tourism.common.enums.DiaryCommentStatus;
import com.af.tourism.common.enums.DiaryDeletedStatus;
import com.af.tourism.common.enums.DiaryStatus;
import com.af.tourism.common.enums.UserStatus;
import com.af.tourism.pojo.dto.admin.DashboardRangeQueryDTO;
import com.af.tourism.mapper.AttractionCategoryMapper;
import com.af.tourism.mapper.AttractionMapper;
import com.af.tourism.mapper.DiaryCommentMapper;
import com.af.tourism.mapper.DiaryMapper;
import com.af.tourism.mapper.OperationLogMapper;
import com.af.tourism.mapper.UserMapper;
import com.af.tourism.pojo.entity.Attraction;
import com.af.tourism.pojo.entity.AttractionCategory;
import com.af.tourism.pojo.entity.DiaryComment;
import com.af.tourism.pojo.entity.OperationLog;
import com.af.tourism.pojo.entity.TravelDiary;
import com.af.tourism.pojo.entity.User;
import com.af.tourism.pojo.vo.admin.AttractionCategoryDistributionVO;
import com.af.tourism.pojo.vo.admin.AttractionCategoryStatsForAdminVO;
import com.af.tourism.pojo.vo.admin.DashboardOverviewVO;
import com.af.tourism.pojo.vo.admin.DashboardTrendPointVO;
import com.af.tourism.pojo.vo.admin.DashboardTrendsVO;
import com.af.tourism.service.admin.AdminDashboardService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 管理端统计看板服务实现
 */
@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final UserMapper userMapper;
    private final AttractionMapper attractionMapper;
    private final AttractionCategoryMapper attractionCategoryMapper;
    private final DiaryMapper diaryMapper;
    private final DiaryCommentMapper diaryCommentMapper;
    private final OperationLogMapper operationLogMapper;

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

        vo.setCategoryTotal(attractionCategoryMapper.selectCount(new LambdaQueryWrapper<AttractionCategory>()));

        vo.setCommentTotal(countFirstLevelComments(null, null, null));
        vo.setCommentVisibleCount(countFirstLevelComments(DiaryCommentStatus.NORMAL.getValue(), null, null));
        vo.setCommentHiddenCount(countFirstLevelComments(DiaryCommentStatus.HIDDEN.getValue(), null, null));

        return vo;
    }

    /**
     * 获取趋势统计
     * @param queryDTO 查询参数
     * @return 趋势统计
     */
    @Override
    public DashboardTrendsVO getTrends(DashboardRangeQueryDTO queryDTO) {
        // 1.获取参数
        int days = "30d".equals(queryDTO.getRangeType()) ? 30 : 7;
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1L);

        // 2.填充数据
        DashboardTrendsVO vo = new DashboardTrendsVO();
        vo.setNewUsers(buildUserTrend(startDate, endDate));
        vo.setNewDiaries(buildDiaryTrend(startDate, endDate));
        vo.setNewComments(buildCommentTrend(startDate, endDate));

        return vo;
    }

    /**
     * 获取景点分类分布
     * @return 景点分类分布
     */
    @Override
    public List<AttractionCategoryDistributionVO> getAttractionCategoryDistribution() {
        // 1.获取景点分类信息
        List<AttractionCategory> categories = attractionCategoryMapper.selectList(
                new LambdaQueryWrapper<AttractionCategory>()
                        .orderByAsc(AttractionCategory::getSortOrder)
                        .orderByAsc(AttractionCategory::getId)
        );
        if (categories == null || categories.isEmpty()) {
            return Collections.emptyList();
        }

        // 2.获取景点分类 id并聚合成集合
        List<Long> categoryIds = categories.stream()
                .map(AttractionCategory::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // 3.批量查找分类下景点数量
        List<AttractionCategoryStatsForAdminVO> stats = attractionCategoryMapper.selectAttractionStatsByCategoryIds(categoryIds);
        Map<Long, Long> countMap = (stats == null ? Collections.<AttractionCategoryStatsForAdminVO>emptyList() : stats)
                .stream()
                .collect(Collectors.toMap(
                        AttractionCategoryStatsForAdminVO::getCategoryId,
                        item -> item.getAttractionCount() == null ? 0L : item.getAttractionCount(),
                        (left, right) -> right
                ));

        // 4.拼接返回值
        List<AttractionCategoryDistributionVO> result = new ArrayList<>();
        for (AttractionCategory category : categories) {
            AttractionCategoryDistributionVO item = new AttractionCategoryDistributionVO();
            item.setCategoryId(category.getId());
            item.setCategoryName(category.getName());
            item.setAttractionCount(countMap.getOrDefault(category.getId(), 0L));
            result.add(item);
        }

        return result;
    }

    /**
     * 获取操作日志活跃趋势
     * @param queryDTO 查询参数
     * @return 操作日志活跃趋势
     */
    @Override
    public List<DashboardTrendPointVO> getOperationLogTrends(DashboardRangeQueryDTO queryDTO) {
        int days = "30d".equals(queryDTO.getRangeType()) ? 30 : 7;
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1L);
        return buildOperationLogTrend(startDate, endDate);
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

    /**
     * 指定状态与时间范围内的一级评论数量
     * @param status 评论状态
     * @param start 开始时间
     * @param end 结束时间
     * @return 评论数量
     */
    private Long countFirstLevelComments(Integer status, LocalDateTime start, LocalDateTime end) {
        LambdaQueryWrapper<DiaryComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNull(DiaryComment::getParentId);
        if (status != null) {
            wrapper.eq(DiaryComment::getStatus, status);
        }
        if (start != null) {
            wrapper.ge(DiaryComment::getCreatedAt, start);
        }
        if (end != null) {
            wrapper.lt(DiaryComment::getCreatedAt, end);
        }
        return diaryCommentMapper.selectCount(wrapper);
    }

    /**
     * 构建用户增长趋势
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 用户增长趋势
     */
    private List<DashboardTrendPointVO> buildUserTrend(LocalDate startDate, LocalDate endDate) {
        List<DashboardTrendPointVO> result = new ArrayList<>();
        // 查找每天新用户数量
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            LocalDateTime start = date.atStartOfDay(); // 当天 00:00
            LocalDateTime end = date.plusDays(1).atStartOfDay(); // 第二天 00:00

            // 查找一天中新增用户数量
            Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                    .ge(User::getCreatedAt, start)
                    .lt(User::getCreatedAt, end));
            // 添加到返回值
            result.add(new DashboardTrendPointVO(date.format(DATE_FORMATTER), count));
        }

        return result;
    }

    /**
     * 构建日记增长趋势
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 日记增长趋势
     */
    private List<DashboardTrendPointVO> buildDiaryTrend(LocalDate startDate, LocalDate endDate) {
        List<DashboardTrendPointVO> result = new ArrayList<>();
        // 查找每天新日记数量
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.plusDays(1).atStartOfDay();

            // 查找一天中新增日记数量
            Long count = diaryMapper.selectCount(new LambdaQueryWrapper<TravelDiary>()
                    .ge(TravelDiary::getCreatedAt, start)
                    .lt(TravelDiary::getCreatedAt, end));
            // 添加到返回值
            result.add(new DashboardTrendPointVO(date.format(DATE_FORMATTER), count));
        }

        return result;
    }

    /**
     * 构建评论增长趋势
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 评论增长趋势
     */
    private List<DashboardTrendPointVO> buildCommentTrend(LocalDate startDate, LocalDate endDate) {
        List<DashboardTrendPointVO> result = new ArrayList<>();
        // 查找每天新评论数量
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.plusDays(1).atStartOfDay();

            // 查找一天中新增评论数量
            Long count = countFirstLevelComments(null, start, end);
            // 添加到返回值
            result.add(new DashboardTrendPointVO(date.format(DATE_FORMATTER), count));
        }

        return result;
    }

    /**
     * 构建操作日志活跃趋势
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 操作日志活跃趋势
     */
    private List<DashboardTrendPointVO> buildOperationLogTrend(LocalDate startDate, LocalDate endDate) {
        List<DashboardTrendPointVO> result = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.plusDays(1).atStartOfDay();
            Long count = operationLogMapper.selectCount(new LambdaQueryWrapper<OperationLog>()
                    .ge(OperationLog::getCreatedAt, start)
                    .lt(OperationLog::getCreatedAt, end));
            result.add(new DashboardTrendPointVO(date.format(DATE_FORMATTER), count));
        }

        return result;
    }
}
