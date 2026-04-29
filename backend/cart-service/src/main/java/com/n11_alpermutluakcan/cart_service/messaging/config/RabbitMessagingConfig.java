package com.n11_alpermutluakcan.cart_service.messaging.config;

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
    public Queue clearCartQueue(MessagingProperties properties) {
        return new Queue(properties.getClearCartQueue(), true);
    }

    @Bean
    public Binding clearCartBinding(Queue clearCartQueue, DirectExchange ecommerceExchange, MessagingProperties properties) {
        return BindingBuilder.bind(clearCartQueue)
                .to(ecommerceExchange)
                .with(properties.getCartClearRoutingKey());
    }

    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
