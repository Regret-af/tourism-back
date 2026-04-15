package com.af.tourism.service.app;

import com.af.tourism.pojo.dto.app.DiaryQueryDTO;
import com.af.tourism.pojo.dto.app.TravelDiaryPublishDTO;
import com.af.tourism.pojo.dto.app.TravelDiaryUpdateDTO;
import com.af.tourism.pojo.vo.app.*;
import com.af.tourism.pojo.vo.common.PageResponse;

import javax.validation.Valid;

public interface DiaryService {

    /**
     * 发布旅行日记
     * @param request 旅行日记信息
     * @return 返回值
     */
    TravelDiaryPublishVO publishDiary(TravelDiaryPublishDTO request, Long userId);

    /**
     * 编辑旅行日记
     * @param diaryId 日记 id
     * @param request 编辑内容
     * @param userId 用户 id
     */
    void updateDiary(Long diaryId, TravelDiaryUpdateDTO request, Long userId);

    /**
     * 旅行日记列表
     * @param queryDTO 请求参数
     * @return 日记列表
     */
    PageResponse<DiaryCardVO> listDiaries(DiaryQueryDTO queryDTO);

    /**
     * 旅行日记详情
     * @param diaryId 旅行日记 id
     * @return 旅行日记详细信息
     */
    DiaryDetailVO getDiaryDetail(Long diaryId);

    /**
     * 获取我的日记
     * @param userId 当前用户 id
     * @param queryDTO 排序分页参数
     * @return 分页后的当前用户日记列表
     */
    PageResponse<MyDiaryProfileCardVO> listMyDiaries(Long userId, DiaryQueryDTO queryDTO);

    /**
     * 获取他人主页日记
     * @param userId 用户 id
     * @param queryDTO 排序分页参数
     * @return 分页后的用户日记列表
     */
    PageResponse<DiaryProfileCardVO> listUserPublicDiaries(Long userId, @Valid DiaryQueryDTO queryDTO);

    /**
     * 查询作者更多创作列表
     * @param diaryId 日记列表
     * @param queryDTO 请求参数
     * @return 作者其他日记列表
     */
    PageResponse<DiaryCardVO> getMoreFromAuthor(Long diaryId, DiaryQueryDTO queryDTO);
}
