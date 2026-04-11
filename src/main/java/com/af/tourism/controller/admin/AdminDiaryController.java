package com.af.tourism.controller.admin;

import com.af.tourism.common.ApiResponse;
import com.af.tourism.pojo.dto.admin.AdminDiaryQueryDTO;
import com.af.tourism.pojo.vo.admin.DiaryForAdminVO;
import com.af.tourism.pojo.vo.common.PageResponse;
import com.af.tourism.service.admin.AdminDiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 管理端日记接口
 */
@RestController
@Validated
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminDiaryController {

    private final AdminDiaryService adminDiaryService;

    /**
     * 获取管理端日记列表
     *
     * @param queryDTO 查询参数
     * @return 日记分页列表
     */
    @GetMapping("/travel-diaries")
    public ApiResponse<PageResponse<DiaryForAdminVO>> listDiaries(@Valid AdminDiaryQueryDTO queryDTO) {
        return ApiResponse.ok(adminDiaryService.listDiaries(queryDTO));
    }
}
