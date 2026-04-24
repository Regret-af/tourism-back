package com.af.tourism.common.constants;

public final class RabbitMqConstants {

    private RabbitMqConstants() {
    }

    public static final String TOURISM_EXCHANGE = "tourism.exchange";

    public static final String OPERATION_LOG_QUEUE = "tourism.operation-log.queue";
    public static final String OPERATION_LOG_ROUTING_KEY = "tourism.operation-log";

    public static final String DIARY_INTERACTION_NOTIFICATION_QUEUE = "tourism.diary-interaction-notification.queue";
    public static final String DIARY_INTERACTION_NOTIFICATION_ROUTING_KEY = "tourism.diary-interaction-notification";
}