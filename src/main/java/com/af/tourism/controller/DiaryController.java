package com.af.tourism.controller;

import com.af.tourism.common.ApiResponse;
import com.af.tourism.common.ErrorCode;
import com.af.tourism.pojo.dto.DiaryQueryDTO;
import com.af.tourism.pojo.vo.DiaryCardVO;
import com.af.tourism.pojo.vo.PageResponse;
import com.af.tourism.service.DiaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 日记列表接口
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
public class DiaryController {

    private final DiaryService diaryService;

    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    /**
     * 按条件查找笔记信息
     * @param queryDTO 查询参数
     * @return 笔记信息列表
     */
    @GetMapping("/diaries")
    public ApiResponse<PageResponse<DiaryCardVO>> listDiaries(DiaryQueryDTO queryDTO) {

        log.info("开始查询笔记信息:{}", queryDTO);

        // 1. 进行参数校验
        if (queryDTO.getPage() < 1 || queryDTO.getSize() < 1) {
            return ApiResponse.fail(ErrorCode.PARAM_INVALID, "参数有误");
        }

        // 2. 进行查询
        PageResponse<DiaryCardVO> data = diaryService.listDiaries(queryDTO);

        // 3. 返回数据
        return ApiResponse.ok(data);
    }
}

