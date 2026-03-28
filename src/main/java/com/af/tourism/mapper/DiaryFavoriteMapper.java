package com.af.tourism.mapper;

import com.af.tourism.pojo.dto.FavoriteDiaryQueryDTO;
import com.af.tourism.pojo.entity.DiaryFavorite;
import com.af.tourism.pojo.vo.FavoriteDiaryCardVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

    /**
     * 查询我的收藏列表
     * @param userId 用户 id
     * @param queryDTO 分页参数
     * @return 收藏的日记列表
     */
    List<FavoriteDiaryCardVO> selectFavoriteDiaryList(@Param("userId") Long userId, @Param("queryDTO") FavoriteDiaryQueryDTO queryDTO);
}
