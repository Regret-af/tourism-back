package com.af.tourism.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 景点天气信息返回
 */
@Data
public class AttractionWeatherVO {

    private Boolean available;

    private String source = "qweather";

    private LocalDateTime sourceUpdateTime;

    private WeatherCurrentVO current;

    private List<WeatherForecastVO> forecast;

    private List<WeatherAlertVO> alerts;

}
