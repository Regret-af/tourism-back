package com.af.tourism.controller;

import com.af.tourism.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康探针接口。
 */
@RestController
@RequestMapping("/api/v1")
public class PingController {

    /**
     * 检测服务状态
     * @return 服务存活且连通
     */
    @GetMapping("/ping")
    public ApiResponse<String> ping() {
        return ApiResponse.ok("ok");
    }
}
