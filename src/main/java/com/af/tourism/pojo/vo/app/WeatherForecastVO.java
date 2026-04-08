package com.af.tourism.pojo.vo.app;

import lombok.Data;

import java.time.LocalDate;

/**
 * 天气预测信息响应
 */
@Data
public class WeatherForecastVO {

    private LocalDate date;

    private String weekLabel;

    private String weatherTextDay;

    private String weatherTextNight;

    private Integer tempMin;

    private Integer tempMax;

    private Boolean isSuitable;

}
