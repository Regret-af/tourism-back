package com.af.tourism.converter;

import com.af.tourism.pojo.entity.DiaryWithUser;
import com.af.tourism.pojo.entity.User;
import com.af.tourism.pojo.vo.DiaryCardVO;
import com.af.tourism.pojo.vo.UserPublicVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * MapStruct 转换器：日记聚合 -> 列表卡片 VO。
 */
@Mapper(componentModel = "spring")
public interface DiaryConverter {

    @Mapping(target = "id", source = "diary.id")
    @Mapping(target = "title", source = "diary.title")
    @Mapping(target = "summary", source = "diary.summary")
    @Mapping(target = "coverImage", source = "diary.coverImage")
    @Mapping(target = "isFeatured", source = "diary.isFeatured")
    @Mapping(target = "likeCount", source = "diary.likeCount")
    @Mapping(target = "viewCount", source = "diary.viewCount")
    @Mapping(target = "createdAt", source = "diary.createdAt")
    @Mapping(target = "user", source = "user")
    DiaryCardVO toCardVO(DiaryWithUser item);

    UserPublicVO toUserPublic(User user);

    List<DiaryCardVO> toCardVOList(List<DiaryWithUser> list);
}

