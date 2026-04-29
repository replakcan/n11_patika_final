package com.n11_alpermutluakcan.order_service.controller;

import com.n11_alpermutluakcan.order_service.dto.OrderResponse;
import com.n11_alpermutluakcan.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public List<OrderResponse> getOrders(@AuthenticationPrincipal Jwt jwt) {
        return orderService.getOrders(getUserId(jwt));
    }

    @GetMapping("/{id}")
    public OrderResponse getOrderById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id
    ) {
        return orderService.getOrderById(getUserId(jwt), id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse placeOrder(@AuthenticationPrincipal Jwt jwt) {
        return orderService.placeOrder(getUserId(jwt), jwt.getTokenValue());
    }

    private String getUserId(Jwt jwt) {
        return jwt.getSubject();
    }
}
