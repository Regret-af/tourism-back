package com.af.tourism.mq.consumer;

import com.af.tourism.common.constants.RabbitMqConstants;
import com.af.tourism.pojo.dto.common.NotificationReadSyncCommand;
import com.af.tourism.service.helper.NotificationReadSyncService;
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
public class NotificationReadMessageConsumer {

    private final NotificationReadSyncService notificationReadSyncService;

    @RabbitListener(queues = RabbitMqConstants.NOTIFICATION_READ_QUEUE)
    public void consume(NotificationReadSyncCommand command, Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            notificationReadSyncService.sync(command);
            channel.basicAck(deliveryTag, false);
        } catch (Exception ex) {
            log.error("消费通知已读回写消息失败, userId={}, notificationId={}",
                    command == null ? null : command.getUserId(),
                    command == null ? null : command.getNotificationId(),
                    ex);
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
