package com.n11_alpermutluakcan.cart_service.messaging.consumer;

import com.n11_alpermutluakcan.cart_service.messaging.event.ClearCartRequestedEvent;
import com.n11_alpermutluakcan.cart_service.messaging.producer.CartSagaPublisher;
import com.n11_alpermutluakcan.cart_service.service.CartService;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CartSagaConsumer {

    private final CartService cartService;
    private final CartSagaPublisher cartSagaPublisher;

    @RabbitListener(queues = "${app.messaging.clear-cart-queue:cart.clear.queue}")
    public void onClearCartRequested(ClearCartRequestedEvent event) {
        try {
            log.info("Received ClearCartRequested for order {} and user {}", event.orderId(), event.userId());
            cartService.clearCart(event.userId());
            log.info("Published CartCleared for order {} and user {}", event.orderId(), event.userId());
            cartSagaPublisher.publishCartCleared(event.orderId(), event.userId());
        } catch (RuntimeException exception) {
            log.error("Failed to clear cart for order {} and user {}", event.orderId(), event.userId(), exception);
            cartSagaPublisher.publishCartClearFailed(event.orderId(), event.userId(), exception.getMessage());
        }
    }
}
