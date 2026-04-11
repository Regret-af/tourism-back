package com.af.tourism.mapper;

import com.af.tourism.pojo.dto.admin.OperationLogQueryDTO;
import com.af.tourism.pojo.entity.OperationLog;
import com.af.tourism.pojo.vo.admin.OperationLogDetailForAdminVO;
import com.af.tourism.pojo.vo.admin.OperationLogForAdminVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 操作日志 Mapper
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {

    /**
     * 查询最近几条操作日志
     * @param userId 用户 id
     * @param limit 条数
     * @return 操作日志列表
     */
    List<OperationLog> selectRecentByUserId(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 操作日志列表
     * @param queryDTO 查询参数
     * @return 操作日志分页列表
     */
    List<OperationLogForAdminVO> selectAdminOperationLogs(@Param("queryDTO") OperationLogQueryDTO queryDTO);

    /**
     * 操作日志详情
     * @param id 日志 id
     * @return 操作日志详情
     */
    OperationLogDetailForAdminVO selectAdminOperationLogDetailById(@Param("id") Long id);
}
