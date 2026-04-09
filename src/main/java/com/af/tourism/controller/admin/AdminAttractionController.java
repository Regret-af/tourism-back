package com.af.tourism.controller.admin;

import com.af.tourism.common.ApiResponse;
import com.af.tourism.pojo.dto.admin.AdminAttractionCreateDTO;
import com.af.tourism.pojo.dto.admin.AdminAttractionQueryDTO;
import com.af.tourism.pojo.vo.admin.AttractionDetailForAdminVO;
import com.af.tourism.pojo.vo.admin.AttractionForAdminVO;
import com.af.tourism.pojo.vo.common.PageResponse;
import com.af.tourism.service.admin.AdminAttractionService;
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
}
