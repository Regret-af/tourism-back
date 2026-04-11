package com.af.tourism.common.enums;

/**
 * 可作为前端下拉字典输出的统一接口
 * @param <T> value 类型
 */
public interface OptionProvider<T> {

    T getValue();

    String getLabel();
}
