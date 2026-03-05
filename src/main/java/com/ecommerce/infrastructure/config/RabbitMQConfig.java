package com.ecommerce.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${app.messaging.product-sync.queue}")
    private String queueName;

    @Value("${app.messaging.product-sync.exchange}")
    private String exchangeName;

    @Value("${app.messaging.product-sync.routing-key}")
    private String routingKey;

    @Bean
    public Queue productSyncQueue() {
        return QueueBuilder.durable(queueName).build();
    }

    @Bean
    public DirectExchange productExchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean
    public Binding productSyncBinding(Queue productSyncQueue, DirectExchange productExchange) {
        return BindingBuilder.bind(productSyncQueue).to(productExchange).with(routingKey);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
