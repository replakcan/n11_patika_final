package com.n11_alpermutluakcan.product_service.messaging.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MessagingProperties.class)
public class RabbitMessagingConfig {

    @Bean
    public DirectExchange ecommerceExchange(MessagingProperties properties) {
        return new DirectExchange(properties.getExchange(), true, false);
    }

    @Bean
    public Queue orderCreatedQueue(MessagingProperties properties) {
        return new Queue(properties.getOrderCreatedQueue(), true);
    }

    @Bean
    public Queue stockReleaseQueue(MessagingProperties properties) {
        return new Queue(properties.getStockReleaseQueue(), true);
    }

    @Bean
    public Binding orderCreatedBinding(Queue orderCreatedQueue, DirectExchange ecommerceExchange, MessagingProperties properties) {
        return BindingBuilder.bind(orderCreatedQueue)
                .to(ecommerceExchange)
                .with(properties.getOrderCreatedRoutingKey());
    }

    @Bean
    public Binding stockReleaseBinding(Queue stockReleaseQueue, DirectExchange ecommerceExchange, MessagingProperties properties) {
        return BindingBuilder.bind(stockReleaseQueue)
                .to(ecommerceExchange)
                .with(properties.getStockReleaseRoutingKey());
    }

    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
