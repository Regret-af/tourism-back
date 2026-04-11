package com.af.tourism.pojo.vo.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用选项项
 * @param <T> value 类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptionVO<T> {

    private T value;

    private String label;
}
