package com.af.tourism.service.impl.admin;

import com.af.tourism.common.ErrorCode;
import com.af.tourism.exception.BusinessException;
import com.af.tourism.mapper.OperationLogMapper;
import com.af.tourism.pojo.dto.admin.OperationLogQueryDTO;
import com.af.tourism.pojo.vo.admin.OperationLogDetailForAdminVO;
import com.af.tourism.pojo.vo.admin.OperationLogForAdminVO;
import com.af.tourism.pojo.vo.common.PageResponse;
import com.af.tourism.service.admin.AdminOperationLogService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 管理端操作日志服务实现
 */
@Service
@RequiredArgsConstructor
public class AdminOperationLogServiceImpl implements AdminOperationLogService {

    private final OperationLogMapper operationLogMapper;

    /**
     * 操作日志列表
     * @param queryDTO 查询参数
     * @return 操作日志分页列表
     */
    @Override
    public PageResponse<OperationLogForAdminVO> listOperationLogs(OperationLogQueryDTO queryDTO) {
        // 1.开启分页查询
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 2.进行查询
        List<OperationLogForAdminVO> list = operationLogMapper.selectAdminOperationLogs(queryDTO);
        PageInfo<OperationLogForAdminVO> pageInfo = new PageInfo<>(list);

        // 3.填充返回值
        PageResponse<OperationLogForAdminVO> response = new PageResponse<>();
        response.setList(list);
        response.setPageNum(pageInfo.getPageNum());
        response.setPageSize(pageInfo.getPageSize());
        response.setTotal(pageInfo.getTotal());

        return response;
    }

    /**
     * 操作日志详情
     * @param id 日志 id
     * @return 操作日志详情
     */
    @Override
    public OperationLogDetailForAdminVO getOperationLogDetail(Long id) {
        OperationLogDetailForAdminVO detail = operationLogMapper.selectAdminOperationLogDetailById(id);
        if (detail == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "操作日志不存在");
        }
        return detail;
    }
}
