package com.af.tourism.pojo.entity;

import lombok.Data;

/**
 * 日记及作者信息聚合，用于列表返回。
 */
@Data
public class DiaryWithUser {
    private TravelDiary diary;
    private User user;
}

