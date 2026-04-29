package com.n11_alpermutluakcan.order_service.messaging.event;

public record OrderItemEvent(
        Long productId,
        Integer quantity
) {
}
