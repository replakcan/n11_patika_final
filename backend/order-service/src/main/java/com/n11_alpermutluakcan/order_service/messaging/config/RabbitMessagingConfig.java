package com.n11_alpermutluakcan.order_service.messaging.config;

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
    public Queue stockReservedQueue(MessagingProperties properties) {
        return new Queue(properties.getStockReservedQueue(), true);
    }

    @Bean
    public Queue stockFailedQueue(MessagingProperties properties) {
        return new Queue(properties.getStockFailedQueue(), true);
    }

    @Bean
    public Queue cartClearedQueue(MessagingProperties properties) {
        return new Queue(properties.getCartClearedQueue(), true);
    }

    @Bean
    public Queue cartClearFailedQueue(MessagingProperties properties) {
        return new Queue(properties.getCartClearFailedQueue(), true);
    }

    @Bean
    public Queue paymentInitializedQueue(MessagingProperties properties) {
        return new Queue(properties.getPaymentInitializedQueue(), true);
    }

    @Bean
    public Queue paymentSucceededQueue(MessagingProperties properties) {
        return new Queue(properties.getPaymentSucceededQueue(), true);
    }

    @Bean
    public Queue paymentFailedQueue(MessagingProperties properties) {
        return new Queue(properties.getPaymentFailedQueue(), true);
    }

    @Bean
    public Binding stockReservedBinding(Queue stockReservedQueue, DirectExchange ecommerceExchange, MessagingProperties properties) {
        return BindingBuilder.bind(stockReservedQueue)
                .to(ecommerceExchange)
                .with(properties.getStockReservedRoutingKey());
    }

    @Bean
    public Binding stockFailedBinding(Queue stockFailedQueue, DirectExchange ecommerceExchange, MessagingProperties properties) {
        return BindingBuilder.bind(stockFailedQueue)
                .to(ecommerceExchange)
                .with(properties.getStockFailedRoutingKey());
    }

    @Bean
    public Binding cartClearedBinding(Queue cartClearedQueue, DirectExchange ecommerceExchange, MessagingProperties properties) {
        return BindingBuilder.bind(cartClearedQueue)
                .to(ecommerceExchange)
                .with(properties.getCartClearedRoutingKey());
    }

    @Bean
    public Binding cartClearFailedBinding(Queue cartClearFailedQueue, DirectExchange ecommerceExchange, MessagingProperties properties) {
        return BindingBuilder.bind(cartClearFailedQueue)
                .to(ecommerceExchange)
                .with(properties.getCartClearFailedRoutingKey());
    }

    @Bean
    public Binding paymentInitializedBinding(
            Queue paymentInitializedQueue,
            DirectExchange ecommerceExchange,
            MessagingProperties properties
    ) {
        return BindingBuilder.bind(paymentInitializedQueue)
                .to(ecommerceExchange)
                .with(properties.getPaymentInitializedRoutingKey());
    }

    @Bean
    public Binding paymentSucceededBinding(
            Queue paymentSucceededQueue,
            DirectExchange ecommerceExchange,
            MessagingProperties properties
    ) {
        return BindingBuilder.bind(paymentSucceededQueue)
                .to(ecommerceExchange)
                .with(properties.getPaymentSucceededRoutingKey());
    }

    @Bean
    public Binding paymentFailedBinding(
            Queue paymentFailedQueue,
            DirectExchange ecommerceExchange,
            MessagingProperties properties
    ) {
        return BindingBuilder.bind(paymentFailedQueue)
                .to(ecommerceExchange)
                .with(properties.getPaymentFailedRoutingKey());
    }

    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
