package com.n11_alpermutluakcan.cart_service.exception;

public class ProductInactiveException extends RuntimeException {

    public ProductInactiveException(Long productId) {
        super("Product is inactive with id: " + productId);
    }
}
