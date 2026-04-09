package com.af.tourism.controller.admin;

import com.af.tourism.common.ApiResponse;
import com.af.tourism.pojo.dto.admin.*;
import com.af.tourism.pojo.vo.admin.AttractionCategoryForAdminVO;
import com.af.tourism.pojo.vo.common.PageResponse;
import com.af.tourism.service.admin.AdminAttractionCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 编辑景点分类
     * @param id 分类 id
     * @param request 编辑请求
     * @return 编辑后的景点分类详情
     */
    @PutMapping("/attraction-categories/{id}")
    public ApiResponse<AttractionCategoryForAdminVO> updateCategory(
            @PathVariable("id") @Min(value = 1, message = "id不能小于1") Long id,
            @Valid @RequestBody AttractionCategoryUpdateDTO request) {
        return ApiResponse.ok(adminAttractionCategoryService.updateCategory(id, request));
    }

    /**
     * 修改景点分类状态
     * @param id 分类 id
     * @param request 状态修改请求
     * @return 修改后的景点分类详情
     */
    @PatchMapping("/attraction-categories/{id}/status")
    public ApiResponse<AttractionCategoryForAdminVO> updateCategoryStatus(
            @PathVariable("id") @Min(value = 1, message = "id不能小于1") Long id,
            @Valid @RequestBody AttractionCategoryStatusUpdateDTO request) {
        return ApiResponse.ok(adminAttractionCategoryService.updateCategoryStatus(id, request));
    }

    /**
     * 修改景点分类排序
     * @param id 分类 id
     * @param request 排序修改请求
     * @return 修改后的景点分类详情
     */
    @PatchMapping("/attraction-categories/{id}/sort-order")
    public ApiResponse<AttractionCategoryForAdminVO> updateCategorySortOrder(
            @PathVariable("id") @Min(value = 1, message = "id不能小于1") Long id,
            @Valid @RequestBody AttractionCategorySortOrderUpdateDTO request) {
        return ApiResponse.ok(adminAttractionCategoryService.updateCategorySortOrder(id, request));
    }
}
