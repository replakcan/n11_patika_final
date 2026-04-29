package com.n11_alpermutluakcan.order_service.exception;

public class ProductInactiveException extends RuntimeException {

    public ProductInactiveException(Long productId) {
        super("Product is inactive and cannot be ordered. Product id: " + productId);
    }
}
