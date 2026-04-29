package com.n11_alpermutluakcan.order_service.client;

import com.n11_alpermutluakcan.order_service.client.dto.CartSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class CartClient {

    private final RestClient.Builder restClientBuilder;

    @Value("${cart-service.base-url}")
    private String cartServiceBaseUrl;

    public CartSummary getCart(String accessToken) {
        return restClientBuilder
                .baseUrl(cartServiceBaseUrl)
                .build()
                .get()
                .uri("/api/cart")
                .header(HttpHeaders.AUTHORIZATION, bearerToken(accessToken))
                .retrieve()
                .body(CartSummary.class);
    }

    public void deleteCartItem(Long itemId, String accessToken) {
        restClientBuilder
                .baseUrl(cartServiceBaseUrl)
                .build()
                .delete()
                .uri("/api/cart/items/{itemId}", itemId)
                .header(HttpHeaders.AUTHORIZATION, bearerToken(accessToken))
                .retrieve()
                .toBodilessEntity();
    }

    private String bearerToken(String accessToken) {
        return "Bearer " + accessToken;
    }
}
