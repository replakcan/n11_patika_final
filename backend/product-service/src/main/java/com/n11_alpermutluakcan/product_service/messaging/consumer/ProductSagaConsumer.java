package com.n11_alpermutluakcan.product_service.messaging.consumer;

import com.n11_alpermutluakcan.product_service.messaging.event.OrderCreatedEvent;
import com.n11_alpermutluakcan.product_service.messaging.event.OrderItemEvent;
import com.n11_alpermutluakcan.product_service.messaging.event.ReleaseStockRequestedEvent;
import com.n11_alpermutluakcan.product_service.messaging.producer.ProductSagaPublisher;
import com.n11_alpermutluakcan.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProductSagaConsumer {

    private final ProductService productService;
    private final ProductSagaPublisher productSagaPublisher;

    @RabbitListener(queues = "${app.messaging.order-created-queue:product.order-created.queue}")
    public void onOrderCreated(OrderCreatedEvent event) {
        List<OrderItemEvent> adjustedItems = new ArrayList<>();

        try {
            for (OrderItemEvent item : event.items()) {
                productService.decrementStock(item.productId(), item.quantity());
                adjustedItems.add(item);
            }

            productSagaPublisher.publishStockReserved(event.orderId(), event.userId());
        } catch (RuntimeException exception) {
            rollbackReservedStock(adjustedItems);
            productSagaPublisher.publishStockReservationFailed(event.orderId(), event.userId(), exception.getMessage());
        }
    }

    @RabbitListener(queues = "${app.messaging.stock-release-queue:product.stock-release.queue}")
    public void onReleaseStockRequested(ReleaseStockRequestedEvent event) {
        for (OrderItemEvent item : event.items()) {
            productService.incrementStock(item.productId(), item.quantity());
        }
    }

    private void rollbackReservedStock(List<OrderItemEvent> adjustedItems) {
        for (int i = adjustedItems.size() - 1; i >= 0; i--) {
            OrderItemEvent item = adjustedItems.get(i);
            try {
                productService.incrementStock(item.productId(), item.quantity());
            } catch (RuntimeException compensationException) {
                log.error(
                        "Failed to rollback reserved stock for product {} by quantity {}",
                        item.productId(),
                        item.quantity(),
                        compensationException
                );
            }
        }
    }
}
