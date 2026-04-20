package com.af.tourism.converter;

import com.af.tourism.pojo.dto.app.TravelDiaryPublishDTO;
import com.af.tourism.pojo.entity.DiaryComment;
import com.af.tourism.pojo.entity.TravelDiary;
import com.af.tourism.pojo.vo.app.DiaryCommentCreateVO;
import com.af.tourism.pojo.vo.app.DiaryDetailVO;
import com.af.tourism.pojo.vo.app.TravelDiaryPublishVO;
import org.mapstruct.Mapper;

/**
 * 日记相关对象转换器
 */
@Mapper(componentModel = "spring")
public interface DiaryConverter {

    TravelDiary toTravelDiary(TravelDiaryPublishDTO travelDiaryPublishDTO);

    TravelDiaryPublishVO toTravelDiaryPublishVO(TravelDiary travelDiary);

    DiaryCommentCreateVO toDiaryCommentCreateVO(DiaryComment diaryComment);

    DiaryDetailVO copy(DiaryDetailVO diaryDetailVO);
}
