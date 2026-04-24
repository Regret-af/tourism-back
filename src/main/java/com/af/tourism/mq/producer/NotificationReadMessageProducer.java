package com.af.tourism.mq.producer;

import com.af.tourism.common.constants.RabbitMqConstants;
import com.af.tourism.pojo.dto.common.NotificationReadSyncCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationReadMessageProducer {

    private final RabbitTemplate rabbitTemplate;

    public void send(NotificationReadSyncCommand command) {
        rabbitTemplate.convertAndSend(
                RabbitMqConstants.TOURISM_EXCHANGE,
                RabbitMqConstants.NOTIFICATION_READ_ROUTING_KEY,
                command
        );
    }
}
