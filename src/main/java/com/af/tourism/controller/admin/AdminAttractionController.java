package com.af.tourism.controller.admin;

import com.af.tourism.common.ApiResponse;
import com.af.tourism.pojo.dto.admin.AdminAttractionCreateDTO;
import com.af.tourism.pojo.dto.admin.AdminAttractionQueryDTO;
import com.af.tourism.pojo.dto.admin.AdminAttractionStatusUpdateDTO;
import com.af.tourism.pojo.dto.admin.AdminAttractionUpdateDTO;
import com.af.tourism.pojo.vo.admin.AttractionDetailForAdminVO;
import com.af.tourism.pojo.vo.admin.AttractionForAdminVO;
import com.af.tourism.pojo.vo.admin.MapDetailVO;
import com.af.tourism.pojo.vo.admin.MapSuggestionVO;
import com.af.tourism.pojo.vo.common.PageResponse;
import com.af.tourism.service.admin.AdminAttractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 管理端景点接口
 */
@RestController
@Validated
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminAttractionController {

    private final AdminAttractionService adminAttractionService;

    /**
     * 景点列表
     * @param queryDTO 查询参数
     * @return 景点分页列表
     */
    @GetMapping("/attractions")
    public ApiResponse<PageResponse<AttractionForAdminVO>> listAttractions(@Valid AdminAttractionQueryDTO queryDTO) {
        return ApiResponse.ok(adminAttractionService.listAttractions(queryDTO));
    }

    /**
     * 景点详情
     * @param id 景点 id
     * @return 景点详情
     */
    @GetMapping("/attractions/{id}")
    public ApiResponse<AttractionDetailForAdminVO> getAttractionDetail(
            @PathVariable("id") @Min(value = 1, message = "id不能小于1") Long id) {
        return ApiResponse.ok(adminAttractionService.getAttractionDetail(id));
    }

    /**
     * 新增景点
     * @param request 新增请求
     * @return 新增后的景点详情
     */
    @PostMapping("/attractions")
    public ApiResponse<AttractionDetailForAdminVO> createAttraction(@Valid @RequestBody AdminAttractionCreateDTO request) {
        return ApiResponse.ok(adminAttractionService.createAttraction(request));
    }

    /**
     * 编辑景点
     * @param id 景点 id
     * @param request 编辑请求
     * @return 编辑后的景点详情
     */
    @PutMapping("/attractions/{id}")
    public ApiResponse<AttractionDetailForAdminVO> updateAttraction(
            @PathVariable("id") @Min(value = 1, message = "id不能小于1") Long id,
            @Valid @RequestBody AdminAttractionUpdateDTO request) {
        return ApiResponse.ok(adminAttractionService.updateAttraction(id, request));
    }

    /**
     * 修改景点状态
     * @param id 景点 id
     * @param request 状态修改请求
     * @return 成功信息
     */
    @PatchMapping("/attractions/{id}/status")
    public ApiResponse<Void> updateAttractionStatus(
            @PathVariable("id") @Min(value = 1, message = "id不能小于1") Long id,
            @Valid @RequestBody AdminAttractionStatusUpdateDTO request) {
        adminAttractionService.updateAttractionStatus(id, request);
        return ApiResponse.ok();
    }

    /**
     * 百度地点搜索
     * @param keyword 关键词
     * @return 地点信息列表
     */
    @GetMapping("/attractions/poi-search")
    public ApiResponse<List<MapSuggestionVO>> getMapSuggestion(@Valid @NotBlank String keyword) {
        return ApiResponse.ok(adminAttractionService.getMapSuggestion(keyword));
    }

    /**
     * 百度地点详情回填
     * @param uid 百度UID
     * @return 地点详细信息
     */
    @GetMapping("/attractions/poi-fill")
    public ApiResponse<MapDetailVO> getMapDetail(@Valid @NotBlank String uid) {
        return ApiResponse.ok(adminAttractionService.getMapDetail(uid));
    }
}
