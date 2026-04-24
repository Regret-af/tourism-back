package com.af.tourism.config;

import com.af.tourism.common.constants.RabbitMqConstants;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Bean
    public DirectExchange tourismExchange() {
        return new DirectExchange(RabbitMqConstants.TOURISM_EXCHANGE, true, false);
    }

    @Bean
    public Queue operationLogQueue() {
        return new Queue(RabbitMqConstants.OPERATION_LOG_QUEUE, true);
    }

    @Bean
    public Queue diaryInteractionNotificationQueue() {
        return new Queue(RabbitMqConstants.DIARY_INTERACTION_NOTIFICATION_QUEUE, true);
    }

    @Bean
    public Binding operationLogBinding(Queue operationLogQueue, DirectExchange tourismExchange) {
        return BindingBuilder.bind(operationLogQueue)
                .to(tourismExchange)
                .with(RabbitMqConstants.OPERATION_LOG_ROUTING_KEY);
    }

    @Bean
    public Binding diaryInteractionNotificationBinding(Queue diaryInteractionNotificationQueue,
                                                       DirectExchange tourismExchange) {
        return BindingBuilder.bind(diaryInteractionNotificationQueue)
                .to(tourismExchange)
                .with(RabbitMqConstants.DIARY_INTERACTION_NOTIFICATION_ROUTING_KEY);
    }

    @Bean
    public MessageConverter rabbitMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter rabbitMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(rabbitMessageConverter);
        rabbitTemplate.setMandatory(true);
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter rabbitMessageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(rabbitMessageConverter);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setPrefetchCount(10);
        return factory;
    }
}
