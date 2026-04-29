package com.n11_alpermutluakcan.product_service.exception;

public class ProductInactiveException extends RuntimeException {

    public ProductInactiveException(Long productId) {
        super("Product is inactive and stock cannot be adjusted. Product id: " + productId);
    }
}
