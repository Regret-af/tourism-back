package com.af.tourism.controller.admin;

import com.af.tourism.common.ApiResponse;
import com.af.tourism.pojo.dto.admin.AttractionCategoryQueryDTO;
import com.af.tourism.pojo.vo.admin.AttractionCategoryForAdminVO;
import com.af.tourism.pojo.vo.common.PageResponse;
import com.af.tourism.service.admin.AdminAttractionCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 管理端景点分类接口
 */
@RestController
@Validated
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminAttractionCategoryController {

    private final AdminAttractionCategoryService adminAttractionCategoryService;

    /**
     * 景点分类列表
     * @param queryDTO 查询参数
     * @return 景点分类分页列表
     */
    @GetMapping("/attraction-categories")
    public ApiResponse<PageResponse<AttractionCategoryForAdminVO>> listCategories(
            @Valid AttractionCategoryQueryDTO queryDTO) {
        return ApiResponse.ok(adminAttractionCategoryService.listCategories(queryDTO));
    }
}
