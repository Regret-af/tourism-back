package com.af.tourism.common.enums;

/**
 * 操作日志动作类型
 */
public enum OperationLogAction implements OptionProvider<String> {
    // 用户端
    LOGIN("LOGIN", "登录"),
    REGISTER("REGISTER", "注册"),
    UPDATE_PROFILE("UPDATE_PROFILE", "修改个人信息"),
    UPDATE_PASSWORD("UPDATE_PASSWORD", "修改密码"),
    COMMENT("COMMENT", "发表评论"),
    CREATE_DIARY("CREATE_DIARY", "创建日记"),
    UPDATE_DIARY("UPDATE_DIARY", "编辑日记"),
    FAVORITE("FAVORITE", "收藏"),
    UNFAVORITE("UNFAVORITE", "取消收藏"),
    LIKE("LIKE", "点赞"),
    UNLIKE("UNLIKE", "取消点赞"),
    UPLOAD_FILE("UPLOAD_FILE", "上传文件"),

    // 管理端: 登录/登出
    ADMIN_LOGIN("ADMIN_LOGIN", "管理员登录"),
    ADMIN_LOGOUT("ADMIN_LOGOUT", "管理员退出登录"),

    // 管理端: 日记
    UPDATE_DIARY_STATUS("UPDATE_DIARY_STATUS", "修改日记状态"),
    UPDATE_DIARY_DELETED("UPDATE_DIARY_DELETED", "修改日记删除状态"),

    // 管理端: 日记评论
    UPDATE_DIARY_COMMENT_STATUS("UPDATE_DIARY_COMMENT_STATUS", "修改评论状态"),

    // 管理端: 景点分类相关
    CREATE_ATTRACTION_CATEGORY("CREATE_ATTRACTION_CATEGORY", "新增景点分类"),
    UPDATE_ATTRACTION_CATEGORY("UPDATE_ATTRACTION_CATEGORY", "编辑景点分类"),
    UPDATE_ATTRACTION_CATEGORY_STATUS("UPDATE_ATTRACTION_CATEGORY_STATUS", "修改景点分类状态"),
    UPDATE_ATTRACTION_CATEGORY_SORT_ORDER("UPDATE_ATTRACTION_CATEGORY_SORT_ORDER", "修改景点分类排序"),

    // 管理端: 景点相关
    CREATE_ATTRACTION("CREATE_ATTRACTION", "新增景点"),
    UPDATE_ATTRACTION("UPDATE_ATTRACTION", "编辑景点"),
    UPDATE_ATTRACTION_STATUS("UPDATE_ATTRACTION_STATUS", "修改景点状态"),

    // 管理端: 用户
    UPDATE_USER_STATUS("UPDATE_USER_STATUS", "修改用户状态"),
    UPDATE_ROLE_STATUS("UPDATE_ROLE_STATUS", "修改角色状态");

    private final String value;
    private final String label;

    OperationLogAction(String value, String label) {
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
