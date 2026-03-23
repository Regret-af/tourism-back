package com.af.tourism.controller;

import com.af.tourism.annotation.OperationLogRecord;
import com.af.tourism.common.ApiResponse;
import com.af.tourism.common.enums.OperationLogAction;
import com.af.tourism.common.enums.OperationLogModule;
import com.af.tourism.pojo.dto.DiaryQueryDTO;
import com.af.tourism.pojo.dto.TravelDiaryPublishDTO;
import com.af.tourism.pojo.vo.DiaryCardVO;
import com.af.tourism.pojo.vo.DiaryDetailVO;
import com.af.tourism.pojo.vo.PageResponse;
import com.af.tourism.pojo.vo.TravelDiaryPublishVO;
import com.af.tourism.securitylite.AuthContext;
import com.af.tourism.service.DiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 旅行日记相关接口。
 */
@RestController
@Validated
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    /**
     * 发布旅行日记
     * @param request 旅行日记信息
     * @return 返回值
     */
    @PostMapping("/travel-diaries")
    @OperationLogRecord(module = OperationLogModule.DIARY, action = OperationLogAction.CREATE_DIARY, description = "发布旅行日记", bizIdField = "data.id")
    public ApiResponse<TravelDiaryPublishVO> publishDiary(@Valid @RequestBody TravelDiaryPublishDTO request) {
        // 获取用户id，若 id 为空，直接抛出异常
        Long userId = AuthContext.requireCurrentUserId();
        return ApiResponse.ok(diaryService.publishDiary(request, userId));
    }

    /**
     * 旅行日记列表
     * @param queryDTO 请求参数
     * @return 日记列表
     */
    @GetMapping("/travel-diaries")
    public ApiResponse<PageResponse<DiaryCardVO>> listDiaries(@Valid DiaryQueryDTO queryDTO) {
        return ApiResponse.ok(diaryService.listDiaries(queryDTO));
    }

    /**
     * 旅行日记详情
     * @param diaryId 旅行日记 id
     * @return 旅行日记详细信息
     */
    @GetMapping("/travel-diaries/{diaryId}")
    public ApiResponse<DiaryDetailVO> getDiaryDetail(@PathVariable("diaryId") Long diaryId) {
        return ApiResponse.ok(diaryService.getDiaryDetail(diaryId));
    }
}
