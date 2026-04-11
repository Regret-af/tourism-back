package com.af.tourism.common.enums;

/**
 * 操作日志来源类型
 */
public enum OperationLogSource implements OptionProvider<String> {
    APP("APP", "用户端"),
    ADMIN("ADMIN", "管理端");

    private final String value;
    private final String label;

    OperationLogSource(String value, String label) {
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
