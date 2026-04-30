package com.n11_alpermutluakcan.order_service.messaging.consumer;

import com.n11_alpermutluakcan.order_service.entity.Order;
import com.n11_alpermutluakcan.order_service.entity.OrderStatus;
import com.n11_alpermutluakcan.order_service.messaging.event.CartClearFailedEvent;
import com.n11_alpermutluakcan.order_service.messaging.event.CartClearedEvent;
import com.n11_alpermutluakcan.order_service.messaging.event.PaymentFailedEvent;
import com.n11_alpermutluakcan.order_service.messaging.event.PaymentInitializedEvent;
import com.n11_alpermutluakcan.order_service.messaging.event.PaymentSucceededEvent;
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
    void shouldMovePendingOrderToStockReservedAndRequestPayment() {
        Order order = buildOrder(OrderStatus.PENDING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderSagaConsumer.onStockReserved(new StockReservedEvent("evt-1", 1L, "user-1", LocalDateTime.now()));

        verify(orderRepository).save(order);
        verify(orderSagaPublisher).publishPaymentRequested(order);
    }

    @Test
    void shouldMovePendingOrderToFailedWhenStockReservationFails() {
        Order order = buildOrder(OrderStatus.PENDING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderSagaConsumer.onStockReservationFailed(new StockReservationFailedEvent(
                "evt-1", 1L, "user-1", "Insufficient stock", LocalDateTime.now()
        ));

        verify(orderRepository).save(order);
        verify(orderSagaPublisher, never()).publishPaymentRequested(order);
    }

    @Test
    void shouldStorePaymentUrlWhenPaymentInitialized() {
        Order order = buildOrder(OrderStatus.STOCK_RESERVED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderSagaConsumer.onPaymentInitialized(new PaymentInitializedEvent(
                "evt-1", 1L, "user-1", "https://sandbox.example/payment", LocalDateTime.now()
        ));

        verify(orderRepository).save(order);
    }

    @Test
    void shouldConfirmOrderAndRequestCartClearWhenPaymentSucceeds() {
        Order order = buildOrder(OrderStatus.PAYMENT_PENDING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderSagaConsumer.onPaymentSucceeded(new PaymentSucceededEvent(
                "evt-1", 1L, "user-1", "payment-1", LocalDateTime.now()
        ));

        verify(orderRepository).save(order);
        verify(orderSagaPublisher).publishClearCartRequested(order);
    }

    @Test
    void shouldFailOrderAndRequestStockReleaseWhenPaymentFails() {
        Order order = buildOrder(OrderStatus.PAYMENT_PENDING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderSagaConsumer.onPaymentFailed(new PaymentFailedEvent(
                "evt-1", 1L, "user-1", "payment failed", LocalDateTime.now()
        ));

        verify(orderRepository).save(order);
        verify(orderSagaPublisher).publishReleaseStockRequested(order);
    }

    @Test
    void shouldIgnoreCartClearFailureForConfirmedOrder() {
        Order order = buildOrder(OrderStatus.CONFIRMED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderSagaConsumer.onCartClearFailed(new CartClearFailedEvent(
                "evt-1", 1L, "user-1", "cart clear failed", LocalDateTime.now()
        ));

        verify(orderRepository, never()).save(order);
        verify(orderSagaPublisher, never()).publishReleaseStockRequested(order);
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
