package com.n11_alpermutluakcan.cart_service.messaging.consumer;

import com.n11_alpermutluakcan.cart_service.messaging.event.ClearCartRequestedEvent;
import com.n11_alpermutluakcan.cart_service.messaging.producer.CartSagaPublisher;
import com.n11_alpermutluakcan.cart_service.service.CartService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CartSagaConsumerTest {

    @Mock
    private CartService cartService;

    @Mock
    private CartSagaPublisher cartSagaPublisher;

    @InjectMocks
    private CartSagaConsumer cartSagaConsumer;

    @Test
    void shouldClearCartAndPublishSuccess() {
        ClearCartRequestedEvent event = new ClearCartRequestedEvent("evt-1", 1L, "user-1", LocalDateTime.now());

        cartSagaConsumer.onClearCartRequested(event);

        verify(cartService).clearCart("user-1");
        verify(cartSagaPublisher).publishCartCleared(1L, "user-1");
    }

    @Test
    void shouldPublishFailureWhenCartClearFails() {
        ClearCartRequestedEvent event = new ClearCartRequestedEvent("evt-1", 1L, "user-1", LocalDateTime.now());
        doThrow(new RuntimeException("Cart clear failed"))
                .when(cartService).clearCart("user-1");

        cartSagaConsumer.onClearCartRequested(event);

        verify(cartSagaPublisher).publishCartClearFailed(1L, "user-1", "Cart clear failed");
    }
}
