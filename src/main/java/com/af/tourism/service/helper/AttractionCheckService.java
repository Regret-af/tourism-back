package com.af.tourism.service.helper;

import com.af.tourism.common.ErrorCode;
import com.af.tourism.exception.BusinessException;
import com.af.tourism.mapper.AttractionMapper;
import com.af.tourism.pojo.entity.Attraction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 检查景点状态服务实现。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AttractionCheckService {

    private final AttractionMapper attractionMapper;

    /**
     * 校验景点是否存在
     * @param attractionId 景点 id
     * @return 景点实体
     */
    public Attraction requireAttraction(Long attractionId) {
        Attraction attraction = attractionMapper.selectById(attractionId);

        if (attraction == null) {
            log.warn("景点不存在，attractionId={}", attractionId);
            throw new BusinessException(ErrorCode.NOT_FOUND, "景点不存在");
        }
        if (attraction.getStatus() == null || attraction.getStatus() != 1) {
            log.warn("景点状态异常，attractionId={} status={}", attractionId, attraction.getStatus());
            throw new BusinessException(ErrorCode.NOT_FOUND, "景点状态异常");
        }
        return attraction;
    }
}
