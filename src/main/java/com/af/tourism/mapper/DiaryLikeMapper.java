package com.af.tourism.mapper;

import com.af.tourism.pojo.entity.DiaryLike;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 点赞 Mapper。
 */
@Mapper
public interface DiaryLikeMapper extends BaseMapper<DiaryLike> {

    /**
     * 查找点赞记录
     * @param diaryId 旅行日记 id
     * @param userId 用户 id
     * @return 点赞记录
     */
    DiaryLike selectByDiaryIdAndUserId(@Param("diaryId") Long diaryId, @Param("userId") Long userId);

    /**
     * 删除点赞记录
     * @param diaryId 旅行日记 id
     * @param userId 用户 id
     * @return 修改数量
     */
    int deleteByDiaryIdAndUserId(@Param("diaryId") Long diaryId, @Param("userId") Long userId);
}
