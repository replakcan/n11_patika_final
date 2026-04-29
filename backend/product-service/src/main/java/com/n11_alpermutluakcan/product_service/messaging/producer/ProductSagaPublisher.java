package com.n11_alpermutluakcan.product_service.messaging.producer;

import com.n11_alpermutluakcan.product_service.messaging.config.MessagingProperties;
import com.n11_alpermutluakcan.product_service.messaging.event.StockReservationFailedEvent;
import com.n11_alpermutluakcan.product_service.messaging.event.StockReservedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProductSagaPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final MessagingProperties messagingProperties;

    public void publishStockReserved(Long orderId, String userId) {
        StockReservedEvent event = new StockReservedEvent(
                UUID.randomUUID().toString(),
                orderId,
                userId,
                LocalDateTime.now()
        );

        rabbitTemplate.convertAndSend(
                messagingProperties.getExchange(),
                messagingProperties.getStockReservedRoutingKey(),
                event
        );
    }

    public void publishStockReservationFailed(Long orderId, String userId, String reason) {
        StockReservationFailedEvent event = new StockReservationFailedEvent(
                UUID.randomUUID().toString(),
                orderId,
                userId,
                reason,
                LocalDateTime.now()
        );

        rabbitTemplate.convertAndSend(
                messagingProperties.getExchange(),
                messagingProperties.getStockFailedRoutingKey(),
                event
        );
    }
}
