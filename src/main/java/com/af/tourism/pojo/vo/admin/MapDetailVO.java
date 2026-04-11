package com.af.tourism.pojo.vo.admin;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MapDetailVO {

    private String baiduUid;

    private String name;

    private String locationText;

    private String addressDetail;

    private String telephone;

    private String openingHours;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private LocalDateTime sourceSyncedAt;
}
