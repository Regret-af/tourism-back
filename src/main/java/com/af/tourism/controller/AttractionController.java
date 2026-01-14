package com.af.tourism.controller;

import com.af.tourism.common.ApiResponse;
import com.af.tourism.common.ErrorCode;
import com.af.tourism.pojo.dto.AttractionQueryDTO;
import com.af.tourism.pojo.vo.AttractionCardVO;
import com.af.tourism.pojo.vo.PageResponse;
import com.af.tourism.service.AttractionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 景点推荐列表接口
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/attractions")
public class AttractionController {

    private final AttractionService attractionService;

    public AttractionController(AttractionService attractionService) {
        this.attractionService = attractionService;
    }

    /**
     * 按条件查找景点信息
     * @param queryDTO 查询参数
     * @return 景点信息列表
     */
    @GetMapping
    public ApiResponse<PageResponse<AttractionCardVO>> listAttractions(AttractionQueryDTO queryDTO) {

        log.info("开始查询景点信息:{}", queryDTO);

        // 1. 进行参数校验
        if (queryDTO.getPage() < 1 || queryDTO.getSize() < 1) {
            return ApiResponse.fail(ErrorCode.PARAM_INVALID, "参数有误");
        }

        // 2. 进行查询
        PageResponse<AttractionCardVO> data = attractionService.listAttractions(queryDTO);

        // 3. 返回数据
        return ApiResponse.ok(data);
    }
}
