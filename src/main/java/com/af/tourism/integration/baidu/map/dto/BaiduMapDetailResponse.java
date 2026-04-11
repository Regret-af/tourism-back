package com.af.tourism.integration.baidu.map.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class BaiduMapDetailResponse {

    private Integer status;

    private String message;

    private List<DetailResult> results;

    @Data
    public static class DetailResult {
        private String uid;

        private String name;

        private Location location;

        private String province;

        private String city;

        private String address;

        private String telephone;

        private DetailInfo detail_info;

        @Data
        public static class Location {
            private BigDecimal lat;

            private BigDecimal lng;
        }

        @Data
        public static class DetailInfo {
            private String shop_hours;
        }
    }
}
