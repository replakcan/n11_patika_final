package com.n11_alpermutluakcan.cart_service.messaging.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.messaging")
public class MessagingProperties {

    private String exchange = "ecommerce.exchange";
    private String clearCartQueue = "cart.clear.queue";
    private String cartClearRoutingKey = "cart.clear.requested";
    private String cartClearedRoutingKey = "cart.cleared";
    private String cartClearFailedRoutingKey = "cart.clear.failed";
}
