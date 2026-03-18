package com.af.tourism.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色表实体。
 */
@Data
@TableName("roles")
public class Role {

    @TableId
    private Long id;

    @TableField("role_code")
    private String roleCode;

    @TableField("role_name")
    private String roleName;

    private Integer status;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
