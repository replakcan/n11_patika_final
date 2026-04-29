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
import static org.mockito.Mockito.doThrow;
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

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void shouldCreateOrderFromCartAndClearCartItems() {
        String userId = "user-1";
        String accessToken = "token";
        CartItemSummary firstItem = new CartItemSummary(10L, 100L, 2, LocalDateTime.now(), LocalDateTime.now());
        CartItemSummary secondItem = new CartItemSummary(11L, 101L, 1, LocalDateTime.now(), LocalDateTime.now());
        CartSummary cartSummary = new CartSummary(userId, 3, List.of(firstItem, secondItem));

        when(cartClient.getCart(accessToken)).thenReturn(cartSummary);
        when(productClient.decrementStock(100L, 2, accessToken)).thenReturn(new ProductSummary(
                100L, "Keyboard", "Mechanical keyboard", new BigDecimal("50.00"), 5, null, true
        ));
        when(productClient.decrementStock(101L, 1, accessToken)).thenReturn(new ProductSummary(
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
        assertThat(response.status()).isEqualTo(OrderStatus.CREATED);
        assertThat(response.totalAmount()).isEqualByComparingTo("120.00");
        assertThat(response.items()).hasSize(2);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        Order savedOrder = orderCaptor.getValue();
        assertThat(savedOrder.getItems()).hasSize(2);
        assertThat(savedOrder.getTotalAmount()).isEqualByComparingTo("120.00");

        verify(productClient).decrementStock(100L, 2, accessToken);
        verify(productClient).decrementStock(101L, 1, accessToken);
        verify(cartClient).deleteCartItem(10L, accessToken);
        verify(cartClient).deleteCartItem(11L, accessToken);
        verify(productClient, never()).incrementStock(any(), any(), any());
    }

    @Test
    void shouldRejectEmptyCart() {
        String userId = "user-1";
        String accessToken = "token";

        when(cartClient.getCart(accessToken)).thenReturn(new CartSummary(userId, 0, List.of()));

        assertThatThrownBy(() -> orderService.placeOrder(userId, accessToken))
                .isInstanceOf(EmptyCartException.class);

        verify(orderRepository, never()).save(any(Order.class));
        verify(productClient, never()).decrementStock(any(), any(), any());
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
        verify(productClient, never()).decrementStock(any(), any(), any());
        verify(cartClient, never()).deleteCartItem(any(), any());
    }

    @Test
    void shouldCompensateStockWhenLaterDecrementFails() {
        String userId = "user-1";
        String accessToken = "token";
        CartItemSummary firstItem = new CartItemSummary(10L, 100L, 2, LocalDateTime.now(), LocalDateTime.now());
        CartItemSummary secondItem = new CartItemSummary(11L, 101L, 1, LocalDateTime.now(), LocalDateTime.now());

        when(cartClient.getCart(accessToken)).thenReturn(new CartSummary(userId, 3, List.of(firstItem, secondItem)));
        when(productClient.decrementStock(100L, 2, accessToken)).thenReturn(new ProductSummary(
                100L, "Keyboard", "Mechanical keyboard", new BigDecimal("50.00"), 5, null, true
        ));
        when(productClient.decrementStock(101L, 1, accessToken)).thenThrow(new RuntimeException("stock update failed"));

        assertThatThrownBy(() -> orderService.placeOrder(userId, accessToken))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("stock update failed");

        verify(productClient).incrementStock(100L, 2, accessToken);
        verify(orderRepository, never()).save(any(Order.class));
        verify(cartClient, never()).deleteCartItem(any(), any());
    }

    @Test
    void shouldCompensateStockWhenCartClearingFails() {
        String userId = "user-1";
        String accessToken = "token";
        CartItemSummary firstItem = new CartItemSummary(10L, 100L, 2, LocalDateTime.now(), LocalDateTime.now());

        when(cartClient.getCart(accessToken)).thenReturn(new CartSummary(userId, 2, List.of(firstItem)));
        when(productClient.decrementStock(100L, 2, accessToken)).thenReturn(new ProductSummary(
                100L, "Keyboard", "Mechanical keyboard", new BigDecimal("50.00"), 5, null, true
        ));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });
        doThrow(new RuntimeException("cart clear failed"))
                .when(cartClient).deleteCartItem(10L, accessToken);

        assertThatThrownBy(() -> orderService.placeOrder(userId, accessToken))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("cart clear failed");

        verify(productClient).incrementStock(100L, 2, accessToken);
    }
}
