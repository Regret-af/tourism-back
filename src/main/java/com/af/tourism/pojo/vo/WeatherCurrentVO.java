package com.af.tourism.pojo.vo;

import lombok.Data;

/**
 * 当前天气信息响应
 */
@Data
public class WeatherCurrentVO {

    private String weatherText;

    private Integer temperature;

    private Integer feelsLike;

    private Integer humidity;

    private String windDirection;

    private String windLevel;

    private Boolean isSuitable;

    private String travelTip;

}
