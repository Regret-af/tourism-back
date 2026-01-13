package com.af.tourism.pojo.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

/**
 * 景点信息查询参数实体
 */
@Data
@JsonPropertyOrder(alphabetic = true) // 按照字母属性进行序列化
public class AttractionQueryDTO {

    Integer page = 1;
    Integer size = 10;
    String q;
    String location;
    Integer priceLevel;
    String tags;
    String sort;
    String algo;
    String scene;
}
