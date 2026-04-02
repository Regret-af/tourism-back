package com.af.tourism.service.impl;

import com.af.tourism.common.ErrorCode;
import com.af.tourism.exception.BusinessException;
import com.af.tourism.mapper.AttractionMapper;
import com.af.tourism.pojo.dto.AttractionQueryDTO;
import com.af.tourism.pojo.vo.AttractionCardVO;
import com.af.tourism.pojo.vo.AttractionDetailVO;
import com.af.tourism.pojo.vo.PageResponse;
import com.af.tourism.service.AttractionService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 景点服务实现。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AttractionServiceImpl implements AttractionService {

    private final AttractionMapper attractionMapper;

    /**
     * 景点列表，覆盖列表、搜索、分类筛选
     * @param queryDTO 列表、搜索、分类筛选参数
     * @return 景点列表
     */
    @Override
    public PageResponse<AttractionCardVO> listAttractions(AttractionQueryDTO queryDTO) {
        // 1.开启分页查询
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 2.查询景点信息
        log.debug("查询景点列表，pageNum={}, pageSize={}, keyword={}, categoryId={}",
                queryDTO.getPageNum(),
                queryDTO.getPageSize(),
                queryDTO.getKeyword(),
                queryDTO.getCategoryId());
        List<AttractionCardVO> list = attractionMapper.selectAttractions(queryDTO);
        PageInfo<AttractionCardVO> pageInfo = new PageInfo<>(list);
        log.debug("查询景点信息完成，总记录数: {}", pageInfo.getTotal());

        // 3.封装返回信息
        PageResponse<AttractionCardVO> response = new PageResponse<>();
        response.setList(list);
        response.setPageNum(pageInfo.getPageNum());
        response.setPageSize(pageInfo.getPageSize());
        response.setTotal(pageInfo.getTotal());
        return response;
    }

    /**
     * 景点详情
     * @param attractionId 景点id
     * @return 景点详情信息
     */
    @Override
    public AttractionDetailVO getAttractionDetail(Long attractionId) {
        // 1.查询景点信息
        AttractionDetailVO detailVO = attractionMapper.selectAttractionDetail(attractionId);
        // 2.若为空，抛出异常
        if (detailVO == null) {
            log.warn("景点不存在，attractionId={}", attractionId);
            throw new BusinessException(ErrorCode.NOT_FOUND, "景点不存在");
        }

        // 3.封装telephoneList，对telephone字段进行数据清洗
        detailVO.setTelephoneList(Arrays.asList(detailVO.getTelephone().split(",")));

        return detailVO;
    }
}
