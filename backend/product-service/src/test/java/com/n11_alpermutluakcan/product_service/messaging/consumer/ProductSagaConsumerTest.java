package com.n11_alpermutluakcan.product_service.messaging.consumer;

import com.n11_alpermutluakcan.product_service.messaging.event.OrderCreatedEvent;
import com.n11_alpermutluakcan.product_service.messaging.event.OrderItemEvent;
import com.n11_alpermutluakcan.product_service.messaging.event.ReleaseStockRequestedEvent;
import com.n11_alpermutluakcan.product_service.messaging.producer.ProductSagaPublisher;
import com.n11_alpermutluakcan.product_service.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductSagaConsumerTest {

    @Mock
    private ProductService productService;

    @Mock
    private ProductSagaPublisher productSagaPublisher;

    @InjectMocks
    private ProductSagaConsumer productSagaConsumer;

    @Test
    void shouldReserveStockForAllItemsAndPublishSuccess() {
        OrderCreatedEvent event = new OrderCreatedEvent(
                "evt-1",
                1L,
                "user-1",
                List.of(new OrderItemEvent(10L, 2), new OrderItemEvent(11L, 1)),
                LocalDateTime.now()
        );

        productSagaConsumer.onOrderCreated(event);

        verify(productService).decrementStock(10L, 2);
        verify(productService).decrementStock(11L, 1);
        verify(productSagaPublisher).publishStockReserved(1L, "user-1");
    }

    @Test
    void shouldReleaseStockForAllItems() {
        ReleaseStockRequestedEvent event = new ReleaseStockRequestedEvent(
                "evt-1",
                1L,
                "user-1",
                List.of(new OrderItemEvent(10L, 2), new OrderItemEvent(11L, 1)),
                LocalDateTime.now()
        );

        productSagaConsumer.onReleaseStockRequested(event);

        verify(productService).incrementStock(10L, 2);
        verify(productService).incrementStock(11L, 1);
    }
}
