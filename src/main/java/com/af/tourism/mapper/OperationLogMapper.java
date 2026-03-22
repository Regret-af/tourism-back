package com.af.tourism.mapper;

import com.af.tourism.pojo.entity.OperationLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 操作日志 Mapper。
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {

    List<OperationLog> selectRecentByUserId(@Param("userId") Long userId, @Param("limit") Integer limit);
}
