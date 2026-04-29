package com.n11_alpermutluakcan.order_service.messaging.consumer;

import com.n11_alpermutluakcan.order_service.entity.Order;
import com.n11_alpermutluakcan.order_service.entity.OrderStatus;
import com.n11_alpermutluakcan.order_service.messaging.event.CartClearFailedEvent;
import com.n11_alpermutluakcan.order_service.messaging.event.CartClearedEvent;
import com.n11_alpermutluakcan.order_service.messaging.event.StockReservationFailedEvent;
import com.n11_alpermutluakcan.order_service.messaging.event.StockReservedEvent;
import com.n11_alpermutluakcan.order_service.messaging.producer.OrderSagaPublisher;
import com.n11_alpermutluakcan.order_service.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderSagaConsumerTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderSagaPublisher orderSagaPublisher;

    @InjectMocks
    private OrderSagaConsumer orderSagaConsumer;

    @Test
    void shouldMovePendingOrderToStockReservedAndRequestCartClear() {
        Order order = buildOrder(OrderStatus.PENDING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderSagaConsumer.onStockReserved(new StockReservedEvent("evt-1", 1L, "user-1", LocalDateTime.now()));

        verify(orderRepository).save(order);
        verify(orderSagaPublisher).publishClearCartRequested(order);
    }

    @Test
    void shouldMovePendingOrderToFailedWhenStockReservationFails() {
        Order order = buildOrder(OrderStatus.PENDING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderSagaConsumer.onStockReservationFailed(new StockReservationFailedEvent(
                "evt-1", 1L, "user-1", "Insufficient stock", LocalDateTime.now()
        ));

        verify(orderRepository).save(order);
        verify(orderSagaPublisher, never()).publishClearCartRequested(order);
    }

    @Test
    void shouldConfirmOrderAfterCartCleared() {
        Order order = buildOrder(OrderStatus.STOCK_RESERVED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderSagaConsumer.onCartCleared(new CartClearedEvent("evt-1", 1L, "user-1", LocalDateTime.now()));

        verify(orderRepository).save(order);
        verify(orderSagaPublisher, never()).publishReleaseStockRequested(order);
    }

    @Test
    void shouldFailOrderAndRequestStockReleaseWhenCartClearFails() {
        Order order = buildOrder(OrderStatus.STOCK_RESERVED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderSagaConsumer.onCartClearFailed(new CartClearFailedEvent(
                "evt-1", 1L, "user-1", "cart clear failed", LocalDateTime.now()
        ));

        verify(orderRepository).save(order);
        verify(orderSagaPublisher).publishReleaseStockRequested(order);
    }

    private Order buildOrder(OrderStatus status) {
        return Order.builder()
                .id(1L)
                .userId("user-1")
                .status(status)
                .totalAmount(BigDecimal.TEN)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
