package com.n11_alpermutluakcan.order_service.messaging.producer;

import com.n11_alpermutluakcan.order_service.entity.Order;
import com.n11_alpermutluakcan.order_service.entity.OrderItem;
import com.n11_alpermutluakcan.order_service.messaging.config.MessagingProperties;
import com.n11_alpermutluakcan.order_service.messaging.event.ClearCartRequestedEvent;
import com.n11_alpermutluakcan.order_service.messaging.event.OrderCreatedEvent;
import com.n11_alpermutluakcan.order_service.messaging.event.OrderItemEvent;
import com.n11_alpermutluakcan.order_service.messaging.event.ReleaseStockRequestedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderSagaPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final MessagingProperties messagingProperties;

    public void publishOrderCreated(Order order) {
        OrderCreatedEvent event = new OrderCreatedEvent(
                UUID.randomUUID().toString(),
                order.getId(),
                order.getUserId(),
                toOrderItemEvents(order.getItems()),
                LocalDateTime.now()
        );

        rabbitTemplate.convertAndSend(
                messagingProperties.getExchange(),
                messagingProperties.getOrderCreatedRoutingKey(),
                event
        );
    }

    public void publishClearCartRequested(Order order) {
        ClearCartRequestedEvent event = new ClearCartRequestedEvent(
                UUID.randomUUID().toString(),
                order.getId(),
                order.getUserId(),
                LocalDateTime.now()
        );

        rabbitTemplate.convertAndSend(
                messagingProperties.getExchange(),
                messagingProperties.getCartClearRoutingKey(),
                event
        );
    }

    public void publishReleaseStockRequested(Order order) {
        ReleaseStockRequestedEvent event = new ReleaseStockRequestedEvent(
                UUID.randomUUID().toString(),
                order.getId(),
                order.getUserId(),
                toOrderItemEvents(order.getItems()),
                LocalDateTime.now()
        );

        rabbitTemplate.convertAndSend(
                messagingProperties.getExchange(),
                messagingProperties.getStockReleaseRoutingKey(),
                event
        );
    }

    private List<OrderItemEvent> toOrderItemEvents(List<OrderItem> items) {
        return items.stream()
                .map(item -> new OrderItemEvent(item.getProductId(), item.getQuantity()))
                .toList();
    }
}
