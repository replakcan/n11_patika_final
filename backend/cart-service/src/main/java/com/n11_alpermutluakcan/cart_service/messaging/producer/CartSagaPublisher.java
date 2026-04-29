package com.n11_alpermutluakcan.cart_service.messaging.producer;

import com.n11_alpermutluakcan.cart_service.messaging.config.MessagingProperties;
import com.n11_alpermutluakcan.cart_service.messaging.event.CartClearFailedEvent;
import com.n11_alpermutluakcan.cart_service.messaging.event.CartClearedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CartSagaPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final MessagingProperties messagingProperties;

    public void publishCartCleared(Long orderId, String userId) {
        CartClearedEvent event = new CartClearedEvent(
                UUID.randomUUID().toString(),
                orderId,
                userId,
                LocalDateTime.now()
        );

        rabbitTemplate.convertAndSend(
                messagingProperties.getExchange(),
                messagingProperties.getCartClearedRoutingKey(),
                event
        );
    }

    public void publishCartClearFailed(Long orderId, String userId, String reason) {
        CartClearFailedEvent event = new CartClearFailedEvent(
                UUID.randomUUID().toString(),
                orderId,
                userId,
                reason,
                LocalDateTime.now()
        );

        rabbitTemplate.convertAndSend(
                messagingProperties.getExchange(),
                messagingProperties.getCartClearFailedRoutingKey(),
                event
        );
    }
}
