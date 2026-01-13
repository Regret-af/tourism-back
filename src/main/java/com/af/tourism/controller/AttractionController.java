package com.af.tourism.controller;

import com.af.tourism.common.ApiResponse;
import com.af.tourism.common.ErrorCode;
import com.af.tourism.pojo.vo.AttractionCardVO;
import com.af.tourism.pojo.vo.PageResponse;
import com.af.tourism.service.AttractionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 景点推荐列表接口
 */
@RestController
@RequestMapping("/api/v1")
public class AttractionController {

    private final AttractionService attractionService;

    public AttractionController(AttractionService attractionService) {
        this.attractionService = attractionService;
    }

    /**
     * 按条件查找景点信息
     * @param page 页码（从 1 开始）
     * @param size 每页条数（最大 50）
     * @param q 关键词
     * @param location 地点筛选
     * @param priceLevel 消费等级
     * @param tags 多标签（逗号分隔，如 摄影,徒步）
     * @param sort 排序字段
     * @param algo 推荐算法标识（预留扩展，当前 time_desc=按创建时间倒序）
     * @param scene 场景标识（用于推荐/埋点统计）(暂时未用到)
     * @return 景点信息列表
     */
    @GetMapping("/attractions")
    public ApiResponse<PageResponse<AttractionCardVO>> listAttractions(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                                       @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
                                                                       @RequestParam(value = "q", required = false) String q,
                                                                       @RequestParam(value = "location", required = false) String location,
                                                                       @RequestParam(value = "priceLevel", required = false) Integer priceLevel,
                                                                       @RequestParam(value = "tags", required = false) String tags,
                                                                       @RequestParam(value = "sort", required = false) String sort,
                                                                       @RequestParam(value = "algo", required = false) String algo,
                                                                       @RequestParam(value = "scene", required = false) String scene) {

        if (page < 1 || size < 1) {
            return ApiResponse.fail(ErrorCode.PARAM_INVALID, "参数有误");
        }

        PageResponse<AttractionCardVO> data = attractionService.listAttractions(page, size, q, location, priceLevel, tags, sort);
        return ApiResponse.ok(data);
    }
}
