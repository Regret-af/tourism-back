package com.af.tourism.mq.producer;

import com.af.tourism.common.constants.RabbitMqConstants;
import com.af.tourism.pojo.dto.common.DiaryInteractionNotifyCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DiaryInteractionNotificationMessageProducer {

    private final RabbitTemplate rabbitTemplate;

    public void send(DiaryInteractionNotifyCommand command) {
        rabbitTemplate.convertAndSend(
                RabbitMqConstants.TOURISM_EXCHANGE,
                RabbitMqConstants.DIARY_INTERACTION_NOTIFICATION_ROUTING_KEY,
                command
        );
    }
}
