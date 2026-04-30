package com.n11_alpermutluakcan.order_service.entity;

public enum OrderStatus {
    PENDING,
    STOCK_RESERVED,
    PAYMENT_PENDING,
    CONFIRMED,
    FAILED,
    CANCELLED
}
