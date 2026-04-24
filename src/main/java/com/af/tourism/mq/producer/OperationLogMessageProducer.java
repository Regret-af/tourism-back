package com.af.tourism.mq.producer;

import com.af.tourism.common.constants.RabbitMqConstants;
import com.af.tourism.pojo.dto.common.OperationLogRecordDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OperationLogMessageProducer {

    private final RabbitTemplate rabbitTemplate;

    public void send(OperationLogRecordDTO request) {
        rabbitTemplate.convertAndSend(
                RabbitMqConstants.TOURISM_EXCHANGE,
                RabbitMqConstants.OPERATION_LOG_ROUTING_KEY,
                request
        );
    }
}
