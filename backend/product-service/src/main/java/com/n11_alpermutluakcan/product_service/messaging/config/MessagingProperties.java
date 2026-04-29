package com.n11_alpermutluakcan.product_service.messaging.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.messaging")
public class MessagingProperties {

    private String exchange = "ecommerce.exchange";
    private String orderCreatedQueue = "product.order-created.queue";
    private String stockReleaseQueue = "product.stock-release.queue";
    private String orderCreatedRoutingKey = "order.created";
    private String stockReservedRoutingKey = "stock.reserved";
    private String stockFailedRoutingKey = "stock.failed";
    private String stockReleaseRoutingKey = "stock.release.requested";
}
