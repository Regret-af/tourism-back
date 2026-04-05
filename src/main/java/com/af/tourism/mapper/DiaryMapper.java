package com.af.tourism.mapper;

import com.af.tourism.pojo.dto.DiaryQueryDTO;
import com.af.tourism.pojo.entity.TravelDiary;
import com.af.tourism.pojo.vo.DiaryCardVO;
import com.af.tourism.pojo.vo.DiaryDetailVO;
import com.af.tourism.pojo.vo.DiaryProfileCardVO;
import com.af.tourism.pojo.vo.MyDiaryProfileCardVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 旅行日记 Mapper。
 */
@Mapper
public interface DiaryMapper extends BaseMapper<TravelDiary> {

    /**
     * 查询符合条件的旅行日记列表
     * @param queryDTO 查询条件
     * @return 旅行日记列表
     */
    List<DiaryCardVO> selectDiaryList(@Param("queryDTO") DiaryQueryDTO queryDTO);

    /**
     * 查询旅行日记详情
     * @param diaryId 日记 id
     * @return 旅行日记详情
     */
    DiaryDetailVO selectDiaryDetail(@Param("diaryId") Long diaryId);

    /**
     * 更新旅行日记评论数量
     * @param diaryId 旅行日记 id
     * @param delta 评论数量差值
     * @return 更新条数
     */
    int updateCommentCount(@Param("diaryId") Long diaryId, @Param("delta") Integer delta);

    /**
     * 更新旅行日记点赞数量
     * @param diaryId 旅行日记 id
     * @param delta 点赞数量差值
     * @return 更新条数
     */
    int updateLikeCount(@Param("diaryId") Long diaryId, @Param("delta") Integer delta);

    /**
     * 更新旅行日记收藏数量
     * @param diaryId 旅行日记 id
     * @param delta 收藏数量差值
     * @return 更新条数
     */
    int updateFavoriteCount(@Param("diaryId") Long diaryId, @Param("delta") Integer delta);

    /**
     * 获取我的日记
     * @param userId 当前用户 id
     * @param queryDTO 排序分页参数
     * @return 分页后的当前用户日记列表
     */
    List<MyDiaryProfileCardVO> selectMyDiaryProfileList(@Param("userId") Long userId, @Param("queryDTO") DiaryQueryDTO queryDTO);

    /**
     * 查询作者更多创作列表
     * @param userId 作者 id
     * @param excludeDiaryId 排除的日记 id
     * @param queryDTO 查询参数
     * @return 作者更多创作列表
     */
    List<DiaryCardVO> selectMoreDiariesByAuthor(@Param("userId") Long userId,
                                                @Param("excludeDiaryId") Long excludeDiaryId,
                                                @Param("queryDTO") DiaryQueryDTO queryDTO);

    /**
     * 在主页获取其他人的日记
     * @param userId 用户 id
     * @param queryDTO 查询参数
     * @return 日记列表
     */
    List<DiaryProfileCardVO> selectUserDiaryProfileList(@Param("userId") Long userId, @Param("queryDTO") DiaryQueryDTO queryDTO);
}
