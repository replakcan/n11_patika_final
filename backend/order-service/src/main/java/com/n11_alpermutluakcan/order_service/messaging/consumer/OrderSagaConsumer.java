package com.n11_alpermutluakcan.order_service.messaging.consumer;

import com.n11_alpermutluakcan.order_service.entity.Order;
import com.n11_alpermutluakcan.order_service.entity.OrderStatus;
import com.n11_alpermutluakcan.order_service.messaging.event.CartClearFailedEvent;
import com.n11_alpermutluakcan.order_service.messaging.event.CartClearedEvent;
import com.n11_alpermutluakcan.order_service.messaging.event.StockReservationFailedEvent;
import com.n11_alpermutluakcan.order_service.messaging.event.StockReservedEvent;
import com.n11_alpermutluakcan.order_service.messaging.producer.OrderSagaPublisher;
import com.n11_alpermutluakcan.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderSagaConsumer {

    private final OrderRepository orderRepository;
    private final OrderSagaPublisher orderSagaPublisher;

    @RabbitListener(queues = "${app.messaging.stock-reserved-queue:order.stock-reserved.queue}")
    @Transactional
    public void onStockReserved(StockReservedEvent event) {
        orderRepository.findById(event.orderId())
                .ifPresentOrElse(order -> {
                    if (order.getStatus() != OrderStatus.PENDING) {
                        log.info("Ignoring StockReservedEvent for order {} with status {}", order.getId(), order.getStatus());
                        return;
                    }

                    order.setStatus(OrderStatus.STOCK_RESERVED);
                    orderRepository.save(order);
                    orderSagaPublisher.publishClearCartRequested(order);
                }, () -> log.warn("Received StockReservedEvent for unknown order {}", event.orderId()));
    }

    @RabbitListener(queues = "${app.messaging.stock-failed-queue:order.stock-failed.queue}")
    @Transactional
    public void onStockReservationFailed(StockReservationFailedEvent event) {
        orderRepository.findById(event.orderId())
                .ifPresentOrElse(order -> {
                    if (isFinal(order)) {
                        log.info("Ignoring StockReservationFailedEvent for order {} with status {}", order.getId(), order.getStatus());
                        return;
                    }

                    order.setStatus(OrderStatus.FAILED);
                    orderRepository.save(order);
                }, () -> log.warn("Received StockReservationFailedEvent for unknown order {}", event.orderId()));
    }

    @RabbitListener(queues = "${app.messaging.cart-cleared-queue:order.cart-cleared.queue}")
    @Transactional
    public void onCartCleared(CartClearedEvent event) {
        orderRepository.findById(event.orderId())
                .ifPresentOrElse(order -> {
                    if (order.getStatus() != OrderStatus.STOCK_RESERVED) {
                        log.info("Ignoring CartClearedEvent for order {} with status {}", order.getId(), order.getStatus());
                        return;
                    }

                    order.setStatus(OrderStatus.CONFIRMED);
                    orderRepository.save(order);
                }, () -> log.warn("Received CartClearedEvent for unknown order {}", event.orderId()));
    }

    @RabbitListener(queues = "${app.messaging.cart-clear-failed-queue:order.cart-clear-failed.queue}")
    @Transactional
    public void onCartClearFailed(CartClearFailedEvent event) {
        orderRepository.findById(event.orderId())
                .ifPresentOrElse(order -> {
                    if (order.getStatus() != OrderStatus.STOCK_RESERVED) {
                        log.info("Ignoring CartClearFailedEvent for order {} with status {}", order.getId(), order.getStatus());
                        return;
                    }

                    order.setStatus(OrderStatus.FAILED);
                    orderRepository.save(order);
                    orderSagaPublisher.publishReleaseStockRequested(order);
                }, () -> log.warn("Received CartClearFailedEvent for unknown order {}", event.orderId()));
    }

    private boolean isFinal(Order order) {
        return order.getStatus() == OrderStatus.CONFIRMED
                || order.getStatus() == OrderStatus.FAILED
                || order.getStatus() == OrderStatus.CANCELLED;
    }
}
