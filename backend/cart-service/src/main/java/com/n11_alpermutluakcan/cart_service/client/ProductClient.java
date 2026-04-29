package com.n11_alpermutluakcan.cart_service.client;

import com.n11_alpermutluakcan.cart_service.client.dto.ProductSummary;
import com.n11_alpermutluakcan.cart_service.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class ProductClient {

    private final RestClient.Builder restClientBuilder;

    @Value("${product-service.base-url}")
    private String productServiceBaseUrl;

    public ProductSummary getProductById(Long productId) {
        try {
            return restClientBuilder
                    .baseUrl(productServiceBaseUrl)
                    .build()
                    .get()
                    .uri("/api/products/{id}", productId)
                    .retrieve()
                    .body(ProductSummary.class);
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ProductNotFoundException(productId);
            }
            throw exception;
        }
    }
}
