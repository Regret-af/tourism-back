package com.af.tourism.mapper;

import com.af.tourism.pojo.entity.DiaryFavorite;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 收藏 Mapper。
 */
@Mapper
public interface DiaryFavoriteMapper extends BaseMapper<DiaryFavorite> {

    /**
     * 查找收藏记录
     * @param diaryId
     * @param userId
     * @return
     */
    DiaryFavorite selectByDiaryIdAndUserId(@Param("diaryId") Long diaryId, @Param("userId") Long userId);

    /**
     * 删除收藏记录
     * @param diaryId 旅行日记 id
     * @param userId 用户 id
     * @return 修改数量
     */
    int deleteByDiaryIdAndUserId(@Param("diaryId") Long diaryId, @Param("userId") Long userId);
}
