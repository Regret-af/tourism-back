package com.af.tourism.controller;

import com.af.tourism.common.ApiResponse;
import com.af.tourism.pojo.vo.DiaryCardVO;
import com.af.tourism.pojo.vo.PageResponse;
import com.af.tourism.service.DiaryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 日记列表接口
 */
@RestController
@RequestMapping("/api/v1")
public class DiaryController {

    private final DiaryService diaryService;

    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    /**
     * 按条件查找笔记信息
     * @param page 页码（从 1 开始）
     * @param size 每页条数（最大 50）
     * @param userId 作者ID
     * @param featured 是否精选
     * @param q 关键词（title/summary 模糊）
     * @param sort 排序字段
     * @param algo 推荐算法标识（预留扩展，当前 time_desc=按创建时间倒序）(暂时未用到)
     * @param scene 场景标识（用于推荐/埋点统计）(暂时未用到)
     * @return 笔记信息列表
     */
    @GetMapping("/diaries")
    public ApiResponse<PageResponse<DiaryCardVO>> listDiaries(@RequestParam(value = "page", required = false) Integer page,
                                                              @RequestParam(value = "size", required = false) Integer size,
                                                              @RequestParam(value = "userId", required = false) Long userId,
                                                              @RequestParam(value = "featured", required = false) Integer featured,
                                                              @RequestParam(value = "q", required = false) String q,
                                                              @RequestParam(value = "sort", required = false) String sort,
                                                              @RequestParam(value = "algo", required = false) String algo,
                                                              @RequestParam(value = "scene", required = false) String scene) {
        PageResponse<DiaryCardVO> data = diaryService.listDiaries(page, size, userId, featured, q, sort);
        return ApiResponse.ok(data);
    }
}

