package com.n11_alpermutluakcan.product_service.messaging.event;

import java.time.LocalDateTime;
import java.util.List;

public record OrderCreatedEvent(
        String eventId,
        Long orderId,
        String userId,
        List<OrderItemEvent> items,
        LocalDateTime occurredAt
) {
}
