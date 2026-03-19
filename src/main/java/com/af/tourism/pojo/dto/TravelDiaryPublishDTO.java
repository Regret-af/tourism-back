package com.af.tourism.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 发布旅行日记请求。
 */
@Data
public class TravelDiaryPublishDTO {

    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题长度不能超过100")
    private String title;

    @Size(max = 255, message = "摘要长度不能超过255")
    private String summary;

    @Size(max = 500, message = "封面地址长度不能超过500")
    private String coverUrl;

    @NotBlank(message = "正文不能为空")
    private String content;
}
