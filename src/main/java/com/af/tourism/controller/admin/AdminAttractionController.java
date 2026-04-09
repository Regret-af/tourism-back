package com.af.tourism.controller.admin;

import com.af.tourism.common.ApiResponse;
import com.af.tourism.pojo.dto.admin.AdminAttractionQueryDTO;
import com.af.tourism.pojo.vo.admin.AttractionForAdminVO;
import com.af.tourism.pojo.vo.common.PageResponse;
import com.af.tourism.service.admin.AdminAttractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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
}
