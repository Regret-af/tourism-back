package com.af.tourism.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 日记分类实体
 */
@Data
@TableName("diary_category")
public class DiaryCategory {

    @TableId
    private Long id;

    private String name;

    private String slug;
}
