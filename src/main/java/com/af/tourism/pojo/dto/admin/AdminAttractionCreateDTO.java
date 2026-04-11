package com.af.tourism.pojo.dto.admin;

import com.af.tourism.common.enums.AttractionStatus;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 管理端景点新增请求
 */
@Data
public class AdminAttractionCreateDTO {

    @NotNull(message = "categoryId不能为空")
    @Min(value = 1, message = "categoryId不能小于1")
    private Long categoryId;

    @NotBlank(message = "name不能为空")
    @Size(max = 100, message = "name长度不能超过100")
    private String name;

    @Size(max = 255, message = "summary长度不能超过255")
    private String summary;

    private String description;

    @Size(max = 500, message = "coverUrl长度不能超过500")
    private String coverUrl;

    @Size(max = 255, message = "locationText长度不能超过255")
    private String locationText;

    @NotBlank(message = "addressDetail不能为空")
    @Size(max = 255, message = "addressDetail长度不能超过255")
    private String addressDetail;

    @Size(max = 255, message = "telephone长度不能超过255")
    private String telephone;

    @Size(max = 255, message = "openingHours长度不能超过255")
    private String openingHours;

    @Size(max = 24, message = "baiduUid长度不能超过24")
    private String baiduUid;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sourceSyncedAt;

    @DecimalMin(value = "-180.0", message = "longitude不能小于-180")
    @DecimalMax(value = "180.0", message = "longitude不能大于180")
    private BigDecimal longitude;

    @DecimalMin(value = "-90.0", message = "latitude不能小于-90")
    @DecimalMax(value = "90.0", message = "latitude不能大于90")
    private BigDecimal latitude;

    @NotNull(message = "status不能为空")
    private AttractionStatus status;
}
