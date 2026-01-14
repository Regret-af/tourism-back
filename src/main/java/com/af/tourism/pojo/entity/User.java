package com.af.tourism.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户表实体。
 */
@Data
@TableName("users")
public class User {
    @TableId
    private Long id;
    private String username;
    private String email;
    private String password;
    @TableField("avatar_url")
    private String avatarUrl;
    private String bio;
    private Integer status;
    @TableField("created_at")
    private LocalDateTime createdAt;
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
