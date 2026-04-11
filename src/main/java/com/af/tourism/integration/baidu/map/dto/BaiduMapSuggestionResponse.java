package com.af.tourism.integration.baidu.map.dto;

import lombok.Data;

import java.util.List;

@Data
public class BaiduMapSuggestionResponse {

    private Integer status;

    private String message;

    private List<SuggestionResult> results;

    @Data
    public static class SuggestionResult {
        private String uid;

        private String name;

        private String province;

        private String city;

        private String address;
    }
}
