package com.af.tourism.common.constants;

public final class RedisKeyConstants {

    private RedisKeyConstants() {
    }

    public static final String ROOT_PREFIX = "tourism";

    public static final String ATTRACTION_CATEGORY_LIST = "attraction:category:list";
    public static final String DIARY_CATEGORY_OPTIONS = "diary:category:options";
    public static final String ATTRACTION_LIST = "attraction:list";
    public static final String ATTRACTION_DETAIL = "attraction:detail";
    public static final String ATTRACTION_WEATHER = "attraction:weather";
    public static final String DIARY_LIST = "diary:list";
    public static final String DIARY_DETAIL = "diary:detail";

    public static final String DIARY_LIKE = "diary:like";
    public static final String DIARY_FAVORITE = "diary:favorite";
    public static final String DIARY_COMMENT = "diary:comment";
    public static final String NOTIFICATION = "notification";
    public static final String AUTH = "auth";
}
