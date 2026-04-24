package com.af.tourism.mq.consumer;

import com.af.tourism.common.constants.RabbitMqConstants;
import com.af.tourism.pojo.dto.common.OperationLogRecordDTO;
import com.af.tourism.service.helper.OperationLogAsyncService;
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
public class OperationLogMessageConsumer {

    private final OperationLogAsyncService operationLogAsyncService;

    @RabbitListener(queues = RabbitMqConstants.OPERATION_LOG_QUEUE)
    public void consume(OperationLogRecordDTO request, Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            operationLogAsyncService.record(request);
            channel.basicAck(deliveryTag, false);
        } catch (Exception ex) {
            log.error("消费操作日志消息失败, module={}, action={}, bizId={}",
                    request == null ? null : request.getModule(),
                    request == null ? null : request.getAction(),
                    request == null ? null : request.getBizId(),
                    ex);
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
