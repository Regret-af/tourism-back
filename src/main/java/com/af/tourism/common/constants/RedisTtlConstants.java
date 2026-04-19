package com.af.tourism.common.constants;

import java.time.Duration;

public final class RedisTtlConstants {

    private RedisTtlConstants() {
    }

    public static final Duration DEFAULT = Duration.ofMinutes(10);
    public static final Duration CATEGORY = Duration.ofHours(1);
    public static final Duration ATTRACTION_LIST = Duration.ofMinutes(5);
    public static final Duration ATTRACTION_WEATHER = Duration.ofMinutes(15);
}
