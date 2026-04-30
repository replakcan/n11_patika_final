package com.n11_alpermutluakcan.payment_service.messaging.config;

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
@EnableConfigurationProperties({MessagingProperties.class, com.n11_alpermutluakcan.payment_service.config.IyzicoProperties.class})
public class RabbitMessagingConfig {

    @Bean
    public DirectExchange ecommerceExchange(MessagingProperties properties) {
        return new DirectExchange(properties.getExchange(), true, false);
    }

    @Bean
    public Queue paymentRequestedQueue(MessagingProperties properties) {
        return new Queue(properties.getPaymentRequestedQueue(), true);
    }

    @Bean
    public Binding paymentRequestedBinding(
            Queue paymentRequestedQueue,
            DirectExchange ecommerceExchange,
            MessagingProperties properties
    ) {
        return BindingBuilder.bind(paymentRequestedQueue)
                .to(ecommerceExchange)
                .with(properties.getPaymentRequestedRoutingKey());
    }

    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
