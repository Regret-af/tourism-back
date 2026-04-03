package com.af.tourism.integration.qweather.dto;

import lombok.Data;

import java.util.List;

/**
 * 和风天气 实时天气预警API返回数据
 */
@Data
public class QWeatherWarningResponse {

    private MetaData metadata;

    private List<Alert> alerts;

    @Data
    public static class MetaData {
        private Boolean zeroResult;
    }

    @Data
    public static class Alert {
        private Color color;

        private String headline;

        private String description;

        @Data
        public static class Color {
            private String code;
        }
    }
}
