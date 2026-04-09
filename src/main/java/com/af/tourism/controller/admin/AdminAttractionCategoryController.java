package com.af.tourism.controller.admin;

import com.af.tourism.common.ApiResponse;
import com.af.tourism.pojo.dto.admin.AttractionCategoryCreateDTO;
import com.af.tourism.pojo.dto.admin.AttractionCategoryQueryDTO;
import com.af.tourism.pojo.vo.admin.AttractionCategoryForAdminVO;
import com.af.tourism.pojo.vo.common.PageResponse;
import com.af.tourism.service.admin.AdminAttractionCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;

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

    /**
     * 景点分类详情
     * @param id 分类 id
     * @return 景点分类详情
     */
    @GetMapping("/attraction-categories/{id}")
    public ApiResponse<AttractionCategoryForAdminVO> getCategoryDetail(
            @PathVariable("id") @Min(value = 1, message = "id不能小于1") Long id) {
        return ApiResponse.ok(adminAttractionCategoryService.getCategoryDetail(id));
    }

    /**
     * 新增景点分类
     * @param request 新增请求
     * @return 新增后的景点分类详情
     */
    @PostMapping("/attraction-categories")
    public ApiResponse<AttractionCategoryForAdminVO> createCategory(
            @Valid @RequestBody AttractionCategoryCreateDTO request) {
        return ApiResponse.ok(adminAttractionCategoryService.createCategory(request));
    }
}
