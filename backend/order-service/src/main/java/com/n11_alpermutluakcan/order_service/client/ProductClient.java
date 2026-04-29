package com.n11_alpermutluakcan.order_service.client;

import com.n11_alpermutluakcan.order_service.client.dto.ProductSummary;
import com.n11_alpermutluakcan.order_service.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
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

    public ProductSummary decrementStock(Long productId, Integer quantity, String accessToken) {
        try {
            return restClientBuilder
                    .baseUrl(productServiceBaseUrl)
                    .build()
                    .post()
                    .uri("/api/products/{id}/decrement-stock", productId)
                    .header(HttpHeaders.AUTHORIZATION, bearerToken(accessToken))
                    .body(new StockAdjustmentRequest(quantity))
                    .retrieve()
                    .body(ProductSummary.class);
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ProductNotFoundException(productId);
            }
            throw exception;
        }
    }

    public void incrementStock(Long productId, Integer quantity, String accessToken) {
        restClientBuilder
                .baseUrl(productServiceBaseUrl)
                .build()
                .post()
                .uri("/api/products/{id}/increment-stock", productId)
                .header(HttpHeaders.AUTHORIZATION, bearerToken(accessToken))
                .body(new StockAdjustmentRequest(quantity))
                .retrieve()
                .toBodilessEntity();
    }

    private String bearerToken(String accessToken) {
        return "Bearer " + accessToken;
    }

    private record StockAdjustmentRequest(Integer quantity) {
    }
}
