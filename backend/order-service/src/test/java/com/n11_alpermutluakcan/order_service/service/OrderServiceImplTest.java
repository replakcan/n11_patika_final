package com.n11_alpermutluakcan.order_service.service;

import com.n11_alpermutluakcan.order_service.client.CartClient;
import com.n11_alpermutluakcan.order_service.client.ProductClient;
import com.n11_alpermutluakcan.order_service.client.dto.CartItemSummary;
import com.n11_alpermutluakcan.order_service.client.dto.CartSummary;
import com.n11_alpermutluakcan.order_service.client.dto.ProductSummary;
import com.n11_alpermutluakcan.order_service.dto.OrderResponse;
import com.n11_alpermutluakcan.order_service.entity.Order;
import com.n11_alpermutluakcan.order_service.entity.OrderStatus;
import com.n11_alpermutluakcan.order_service.exception.CartOwnershipMismatchException;
import com.n11_alpermutluakcan.order_service.exception.EmptyCartException;
import com.n11_alpermutluakcan.order_service.messaging.producer.OrderSagaPublisher;
import com.n11_alpermutluakcan.order_service.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartClient cartClient;

    @Mock
    private ProductClient productClient;

    @Mock
    private OrderSagaPublisher orderSagaPublisher;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void shouldCreatePendingOrderAndPublishSagaEvent() {
        String userId = "user-1";
        String accessToken = "token";
        CartItemSummary firstItem = new CartItemSummary(10L, 100L, 2, LocalDateTime.now(), LocalDateTime.now());
        CartItemSummary secondItem = new CartItemSummary(11L, 101L, 1, LocalDateTime.now(), LocalDateTime.now());
        CartSummary cartSummary = new CartSummary(userId, 3, List.of(firstItem, secondItem));

        when(cartClient.getCart(accessToken)).thenReturn(cartSummary);
        when(productClient.getProductById(100L)).thenReturn(new ProductSummary(
                100L, "Keyboard", "Mechanical keyboard", new BigDecimal("50.00"), 5, null, true
        ));
        when(productClient.getProductById(101L)).thenReturn(new ProductSummary(
                101L, "Mouse", "Wireless mouse", new BigDecimal("20.00"), 3, null, true
        ));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });

        OrderResponse response = orderService.placeOrder(userId, accessToken);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.userId()).isEqualTo(userId);
        assertThat(response.status()).isEqualTo(OrderStatus.PENDING);
        assertThat(response.totalAmount()).isEqualByComparingTo("120.00");
        assertThat(response.items()).hasSize(2);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        Order savedOrder = orderCaptor.getValue();
        assertThat(savedOrder.getItems()).hasSize(2);
        assertThat(savedOrder.getTotalAmount()).isEqualByComparingTo("120.00");

        verify(productClient).getProductById(100L);
        verify(productClient).getProductById(101L);
        verify(orderSagaPublisher).publishOrderCreated(savedOrder);
    }

    @Test
    void shouldRejectEmptyCart() {
        String userId = "user-1";
        String accessToken = "token";

        when(cartClient.getCart(accessToken)).thenReturn(new CartSummary(userId, 0, List.of()));

        assertThatThrownBy(() -> orderService.placeOrder(userId, accessToken))
                .isInstanceOf(EmptyCartException.class);

        verify(orderRepository, never()).save(any(Order.class));
        verify(productClient, never()).getProductById(any());
        verify(orderSagaPublisher, never()).publishOrderCreated(any(Order.class));
    }

    @Test
    void shouldRejectCartOwnedByAnotherUser() {
        String accessToken = "token";

        when(cartClient.getCart(accessToken)).thenReturn(new CartSummary("user-2", 1, List.of(
                new CartItemSummary(10L, 100L, 1, LocalDateTime.now(), LocalDateTime.now())
        )));

        assertThatThrownBy(() -> orderService.placeOrder("user-1", accessToken))
                .isInstanceOf(CartOwnershipMismatchException.class)
                .hasMessageContaining("Expected user id: user-1")
                .hasMessageContaining("actual user id: user-2");

        verify(orderRepository, never()).save(any(Order.class));
        verify(productClient, never()).getProductById(any());
        verify(orderSagaPublisher, never()).publishOrderCreated(any(Order.class));
    }
}
