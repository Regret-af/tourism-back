package com.af.tourism.pojo.vo.app;

import lombok.Data;

/**
 * 天气预警信息响应
 */
@Data
public class WeatherAlertVO {

    private String title;

    private String level;

    private String description;

}
