package com.af.tourism.controller.admin;

import com.af.tourism.common.ApiResponse;
import com.af.tourism.pojo.vo.admin.AdminMetaOptionsVO;
import com.af.tourism.service.admin.AdminMetaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端通用元数据接口
 */
@RestController
@RequestMapping("/api/v1/admin/meta")
@RequiredArgsConstructor
public class AdminMetaController {

    private final AdminMetaService adminMetaService;

    @GetMapping("/options")
    public ApiResponse<AdminMetaOptionsVO> getOptions() {
        return ApiResponse.ok(adminMetaService.getOptions());
    }
}
