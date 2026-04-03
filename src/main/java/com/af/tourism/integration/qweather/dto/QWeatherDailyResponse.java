package com.af.tourism.integration.qweather.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 和风天气 每日天气预报API返回数据
 */
@Data
public class QWeatherDailyResponse {

    private String code;

    private List<Daily> daily;

    @Data
    public static class Daily {
        private LocalDate fxDate;

        private String textDay;

        private String textNight;

        private Integer tempMin;

        private Integer tempMax;
    }

}
