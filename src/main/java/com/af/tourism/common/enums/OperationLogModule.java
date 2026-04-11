package com.af.tourism.common.enums;

/**
 * 操作日志模块类型
 */
public enum OperationLogModule implements OptionProvider<String> {
    USER("USER", "用户"),
    ATTRACTION("ATTRACTION", "景点"),
    DIARY("DIARY", "日记"),
    FILE("FILE", "文件"),
    COMMENT("COMMENT", "评论"),
    ROLE("ROLE", "角色"),
    ATTRACTION_CATEGORY("ATTRACTION_CATEGORY", "景点分类");

    private final String value;
    private final String label;

    OperationLogModule(String value, String label) {
        this.value = value;
        this.label = label;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getLabel() {
        return label;
    }
}
