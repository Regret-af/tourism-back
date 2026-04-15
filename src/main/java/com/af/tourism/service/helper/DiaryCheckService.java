package com.af.tourism.service.helper;

import com.af.tourism.common.ErrorCode;
import com.af.tourism.common.enums.DiaryDeletedStatus;
import com.af.tourism.common.enums.DiaryStatus;
import com.af.tourism.common.enums.DiaryVisibility;
import com.af.tourism.exception.BusinessException;
import com.af.tourism.mapper.DiaryMapper;
import com.af.tourism.pojo.entity.TravelDiary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 检查笔记状态服务实现。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DiaryCheckService {

    private final DiaryMapper diaryMapper;

    /**
     * 校验旅行日记是否存在
     * @param diaryId 旅行日记 id
     * @return 旅行日记实体
     */
    public TravelDiary requirePublicDiary(Long diaryId) {
        TravelDiary diary = diaryMapper.selectById(diaryId);

        if (diary == null) {
            log.warn("旅行日记不存在，diaryId={}", diaryId);
            throw new BusinessException(ErrorCode.NOT_FOUND, "旅行日记不存在");
        }
        if (!DiaryStatus.PUBLIC.getValue().equals(diary.getStatus())) {
            log.warn("旅行日记状态异常，diaryId={}, status={}", diaryId, diary.getStatus());
            throw new BusinessException(ErrorCode.NOT_FOUND, "旅行日记不存在");
        }
        if (!DiaryDeletedStatus.NOT_DELETED.getValue().equals(diary.getIsDeleted())) {
            log.warn("旅行日记已删除，diaryId={}, isDeleted={}", diaryId, diary.getIsDeleted());
            throw new BusinessException(ErrorCode.NOT_FOUND, "旅行日记不存在");
        }
        if (!DiaryVisibility.PUBLIC.getValue().equals(diary.getVisibility())) {
            log.warn("旅行日记不可公开访问，diaryId={}, visibility={}", diaryId, diary.getVisibility());
            throw new BusinessException(ErrorCode.NOT_FOUND, "旅行日记不存在");
        }
        return diary;
    }
}
