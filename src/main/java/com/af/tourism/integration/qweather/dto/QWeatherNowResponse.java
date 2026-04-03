package com.af.tourism.integration.qweather.dto;

import lombok.Data;

import java.time.OffsetDateTime;

/**
 * 和风天气 实时天气API返回数据
 */
@Data
public class QWeatherNowResponse {

    private String code;

    private OffsetDateTime updateTime;

    private Now now;

    @Data
    public static class Now {

        private String text;

        private Integer temp;

        private Integer feelsLike;

        private Integer humidity;

        private String windDir;

        private String windScale;

    }

}
