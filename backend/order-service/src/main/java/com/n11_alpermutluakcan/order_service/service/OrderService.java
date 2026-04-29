package com.n11_alpermutluakcan.order_service.service;

import com.n11_alpermutluakcan.order_service.dto.OrderResponse;

import java.util.List;

public interface OrderService {

    OrderResponse placeOrder(String userId, String accessToken);

    List<OrderResponse> getOrders(String userId);

    OrderResponse getOrderById(String userId, Long orderId);
}
