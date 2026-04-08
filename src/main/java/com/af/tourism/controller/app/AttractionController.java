package com.af.tourism.controller.app;

import com.af.tourism.common.ApiResponse;
import com.af.tourism.pojo.dto.AttractionQueryDTO;
import com.af.tourism.pojo.vo.*;
import com.af.tourism.service.AttractionCategoryService;
import com.af.tourism.service.AttractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * 景点相关接口。
 */
@RestController
@Validated
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AttractionController {

    private final AttractionCategoryService attractionCategoryService;
    private final AttractionService attractionService;

    /**
     * 景点分类列表
     * @return 景点分类列表项
     */
    @GetMapping("/attraction-categories")
    public ApiResponse<List<AttractionCategoryVO>> listCategories() {
        return ApiResponse.ok(attractionCategoryService.listCategories());
    }

    /**
     * 景点列表，覆盖列表、搜索、分类筛选
     * @param queryDTO 列表、搜索、分类筛选参数
     * @return 景点列表
     */
    @GetMapping("/attractions")
    public ApiResponse<PageResponse<AttractionCardVO>> listAttractions(@Valid AttractionQueryDTO queryDTO) {
        return ApiResponse.ok(attractionService.listAttractions(queryDTO));
    }

    /**
     * 景点详情
     * @param attractionId 景点id
     * @return 景点详情信息
     */
    @GetMapping("/attractions/{attractionId}")
    public ApiResponse<AttractionDetailVO> getAttractionDetail(@PathVariable("attractionId") Long attractionId) {
        return ApiResponse.ok(attractionService.getAttractionDetail(attractionId));
    }

    /**
     * 查询景点天气信息、未来天气信息与预警信息
     * @param attractionId 景点 id
     * @return 景点天气信息
     */
    @GetMapping("/attractions/{id}/weather")
    public ApiResponse<AttractionWeatherVO> getAttractionWeather(
            @PathVariable("id") @Min(value = 1, message = "id不能小于1") Long attractionId) {
        return ApiResponse.ok(attractionService.getAttractionWeather(attractionId));
    }
}
