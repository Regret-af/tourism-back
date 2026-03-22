package com.af.tourism.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志实体。
 */
@Data
@TableName("operation_logs")
public class OperationLog {

    @TableId
    private Long id;

    @TableField("user_id")
    private Long userId;

    private String module;

    private String action;

    @TableField("biz_id")
    private Long bizId;

    private String description;

    @TableField("request_ip")
    private String requestIp;

    @TableField("user_agent")
    private String userAgent;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
