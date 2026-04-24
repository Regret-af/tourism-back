package com.af.tourism.mq.consumer;

import com.af.tourism.common.constants.RabbitMqConstants;
import com.af.tourism.pojo.dto.common.DiaryInteractionNotifyCommand;
import com.af.tourism.service.helper.DiaryInteractionNotificationService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class DiaryInteractionNotificationMessageConsumer {

    private final DiaryInteractionNotificationService diaryInteractionNotificationService;

    @RabbitListener(queues = RabbitMqConstants.DIARY_INTERACTION_NOTIFICATION_QUEUE)
    public void consume(DiaryInteractionNotifyCommand command, Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            diaryInteractionNotificationService.notifyInteraction(command);
            channel.basicAck(deliveryTag, false);
        } catch (Exception ex) {
            log.error("消费日记互动通知消息失败, type={}, triggerUserId={}, recipientUserId={}, diaryId={}",
                    command == null ? null : command.getType(),
                    command == null ? null : command.getTriggerUserId(),
                    command == null ? null : command.getRecipientUserId(),
                    command == null ? null : command.getRelatedDiaryId(),
                    ex);
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
