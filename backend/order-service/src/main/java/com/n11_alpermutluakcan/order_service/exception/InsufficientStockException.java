package com.n11_alpermutluakcan.order_service.exception;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(Long productId, Integer requestedQuantity, Integer availableStock) {
        super("Insufficient stock for product id " + productId
                + ". Requested: " + requestedQuantity
                + ", available: " + availableStock);
    }
}
