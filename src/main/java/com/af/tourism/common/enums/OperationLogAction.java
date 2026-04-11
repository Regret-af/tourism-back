package com.af.tourism.common.enums;

/**
 * 操作日志动作类型
 */
public enum OperationLogAction {
    // 用户端
    LOGIN,
    REGISTER,
    UPDATE_PROFILE,
    UPDATE_PASSWORD,
    COMMENT,
    CREATE_DIARY,
    FAVORITE,
    UNFAVORITE,
    LIKE,
    UNLIKE,
    UPLOAD_FILE,

    // 管理端: 登录/登出
    ADMIN_LOGIN,
    ADMIN_LOGOUT,

    // 管理端: 日记
    UPDATE_DIARY_STATUS,
    UPDATE_DIARY_DELETED,

    // 管理端: 日记评论
    UPDATE_DIARY_COMMENT_STATUS,

    // 管理端: 景点分类相关
    CREATE_ATTRACTION_CATEGORY,
    UPDATE_ATTRACTION_CATEGORY,
    UPDATE_ATTRACTION_CATEGORY_STATUS,
    UPDATE_ATTRACTION_CATEGORY_SORT_ORDER,

    // 管理端: 景点相关
    CREATE_ATTRACTION,
    UPDATE_ATTRACTION,
    UPDATE_ATTRACTION_STATUS,

    // 管理端: 用户
    UPDATE_USER_STATUS,
    UPDATE_ROLE_STATUS,

}
