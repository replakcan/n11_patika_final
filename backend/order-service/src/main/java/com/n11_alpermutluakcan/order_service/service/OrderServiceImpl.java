package com.n11_alpermutluakcan.order_service.service;

import com.n11_alpermutluakcan.order_service.client.CartClient;
import com.n11_alpermutluakcan.order_service.client.ProductClient;
import com.n11_alpermutluakcan.order_service.client.dto.CartItemSummary;
import com.n11_alpermutluakcan.order_service.client.dto.CartSummary;
import com.n11_alpermutluakcan.order_service.client.dto.ProductSummary;
import com.n11_alpermutluakcan.order_service.dto.OrderResponse;
import com.n11_alpermutluakcan.order_service.entity.Order;
import com.n11_alpermutluakcan.order_service.entity.OrderItem;
import com.n11_alpermutluakcan.order_service.entity.OrderStatus;
import com.n11_alpermutluakcan.order_service.exception.CartOwnershipMismatchException;
import com.n11_alpermutluakcan.order_service.exception.EmptyCartException;
import com.n11_alpermutluakcan.order_service.exception.OrderNotFoundException;
import com.n11_alpermutluakcan.order_service.mapper.OrderMapper;
import com.n11_alpermutluakcan.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartClient cartClient;
    private final ProductClient productClient;

    @Override
    @Transactional
    public OrderResponse placeOrder(String userId, String accessToken) {
        CartSummary cart = cartClient.getCart(accessToken);
        validateCartOwnership(userId, cart);

        if (cart.items() == null || cart.items().isEmpty()) {
            throw new EmptyCartException();
        }

        Order order = Order.builder()
                .userId(userId)
                .status(OrderStatus.CREATED)
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<StockAdjustment> decrementedStocks = new ArrayList<>();

        try {
            for (CartItemSummary cartItem : cart.items()) {
                ProductSummary product = productClient.decrementStock(
                        cartItem.productId(),
                        cartItem.quantity(),
                        accessToken
                );
                decrementedStocks.add(new StockAdjustment(product.id(), cartItem.quantity()));

                BigDecimal lineTotal = product.price().multiply(BigDecimal.valueOf(cartItem.quantity()));
                totalAmount = totalAmount.add(lineTotal);

                order.addItem(OrderItem.builder()
                        .productId(product.id())
                        .productName(product.name())
                        .unitPrice(product.price())
                        .quantity(cartItem.quantity())
                        .lineTotal(lineTotal)
                        .build());
            }

            order.setTotalAmount(totalAmount);
            Order savedOrder = orderRepository.save(order);

            for (CartItemSummary cartItem : cart.items()) {
                cartClient.deleteCartItem(cartItem.id(), accessToken);
            }

            return OrderMapper.toResponse(savedOrder);
        } catch (RuntimeException exception) {
            compensateStock(decrementedStocks, accessToken);
            throw exception;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrders(String userId) {
        return orderRepository.findAllByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(OrderMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(String userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        return OrderMapper.toResponse(order);
    }

    private void validateCartOwnership(String userId, CartSummary cart) {
        if (cart.userId() != null && !userId.equals(cart.userId())) {
            throw new CartOwnershipMismatchException(userId, cart.userId());
        }
    }

    private void compensateStock(List<StockAdjustment> decrementedStocks, String accessToken) {
        for (int i = decrementedStocks.size() - 1; i >= 0; i--) {
            StockAdjustment stockAdjustment = decrementedStocks.get(i);
            try {
                productClient.incrementStock(stockAdjustment.productId(), stockAdjustment.quantity(), accessToken);
            } catch (RuntimeException compensationException) {
                log.error(
                        "Failed to compensate stock for product {} by quantity {}",
                        stockAdjustment.productId(),
                        stockAdjustment.quantity(),
                        compensationException
                );
            }
        }
    }

    private record StockAdjustment(Long productId, Integer quantity) {
    }
}
