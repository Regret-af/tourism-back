package com.af.tourism.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 发布旅行日记响应。
 */
@Data
public class TravelDiaryPublishVO {

    private Long id;
    private String title;
    private String summary;
    private String coverUrl;
    private String content;
    private Integer status;
    private LocalDateTime publishedAt;
}
