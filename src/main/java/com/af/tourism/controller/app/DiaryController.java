package com.af.tourism.controller.app;

import com.af.tourism.annotation.OperationLogRecord;
import com.af.tourism.common.ApiResponse;
import com.af.tourism.common.enums.OperationLogAction;
import com.af.tourism.common.enums.OperationLogModule;
import com.af.tourism.pojo.dto.app.DiaryQueryDTO;
import com.af.tourism.pojo.dto.app.TravelDiaryPublishDTO;
import com.af.tourism.pojo.dto.app.TravelDiaryUpdateDTO;
import com.af.tourism.pojo.vo.app.DiaryCardVO;
import com.af.tourism.pojo.vo.app.DiaryDetailVO;
import com.af.tourism.pojo.vo.common.PageResponse;
import com.af.tourism.pojo.vo.common.OptionVO;
import com.af.tourism.pojo.vo.app.TravelDiaryPublishVO;
import com.af.tourism.security.util.SecurityUtils;
import com.af.tourism.service.app.DiaryCategoryService;
import com.af.tourism.service.app.DiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 旅行日记相关接口。
 */
@RestController
@Validated
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryCategoryService diaryCategoryService;
    private final DiaryService diaryService;

    /**
     * 发布旅行日记
     * @param request 旅行日记信息
     * @return 返回值
     */
    @PostMapping("/travel-diaries")
    @PreAuthorize("isAuthenticated()")
    @OperationLogRecord(
            module = OperationLogModule.DIARY,
            action = OperationLogAction.CREATE_DIARY,
            description = "发布旅行日记",
            bizIdField = "data.id"
    )
    public ApiResponse<TravelDiaryPublishVO> publishDiary(@Valid @RequestBody TravelDiaryPublishDTO request) {
        // 获取用户id，若 id 为空，直接抛出异常
        Long userId = SecurityUtils.requireCurrentUserId();
        return ApiResponse.ok(diaryService.publishDiary(request, userId));
    }

    /**
     * 编辑旅行日记
     * @param diaryId 日记 id
     * @param request 编辑信息
     * @return 编辑结果
     */
    @PutMapping("/travel-diaries/{diaryId}")
    @PreAuthorize("isAuthenticated()")
    @OperationLogRecord(
            module = OperationLogModule.DIARY,
            action = OperationLogAction.UPDATE_DIARY,
            description = "编辑旅行日记",
            bizIdArgIndex = 0
    )
    public ApiResponse<Void> updateDiary(@PathVariable("diaryId") Long diaryId,
                                         @Valid @RequestBody TravelDiaryUpdateDTO request) {
        // 获取用户id，若 id 为空，直接抛出异常
        Long userId = SecurityUtils.requireCurrentUserId();
        diaryService.updateDiary(diaryId, request, userId);
        return ApiResponse.ok();
    }

    /**
     * 删除旅行日记
     * @param diaryId 日记 id
     * @return 删除结果
     */
    @DeleteMapping("/travel-diaries/{diaryId}")
    @PreAuthorize("isAuthenticated()")
    @OperationLogRecord(
            module = OperationLogModule.DIARY,
            action = OperationLogAction.DELETE_DIARY,
            description = "删除旅行日记",
            bizIdArgIndex = 0
    )
    public ApiResponse<Void> deleteDiary(@PathVariable("diaryId") Long diaryId) {
        // 获取用户id，若 id 为空，直接抛出异常
        Long userId = SecurityUtils.requireCurrentUserId();
        diaryService.deleteDiary(diaryId, userId);
        return ApiResponse.ok();
    }

    /**
     * 日记分类选项
     * @return 日记分类选项列表
     */
    @GetMapping("/diary-categories/options")
    public ApiResponse<List<OptionVO<Long>>> listDiaryCategoryOptions() {
        return ApiResponse.ok(diaryCategoryService.listCategoryOptions());
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

    /**
     * 查询作者更多创作列表
     * @param diaryId 日记列表
     * @param queryDTO 请求参数
     * @return 作者其他日记列表
     */
    @GetMapping("/travel-diaries/{diaryId}/more-from-author")
    public ApiResponse<PageResponse<DiaryCardVO>> getMoreFromAuthor(@PathVariable("diaryId") Long diaryId, DiaryQueryDTO queryDTO) {
        return ApiResponse.ok(diaryService.getMoreFromAuthor(diaryId, queryDTO));
    }
}
