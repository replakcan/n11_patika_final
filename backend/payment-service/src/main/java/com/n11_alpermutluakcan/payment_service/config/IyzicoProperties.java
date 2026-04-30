package com.n11_alpermutluakcan.payment_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.iyzico")
public class IyzicoProperties {

    private String apiKey;
    private String secretKey;
    private String baseUrl = "https://sandbox-api.iyzipay.com";
    private String callbackUrl;
    private String locale = "tr";
    private String currency = "TRY";
    private String buyerName = "Sandbox";
    private String buyerSurname = "User";
    private String buyerEmail = "sandbox@example.com";
    private String buyerIdentityNumber = "11111111111";
    private String buyerGsmNumber = "+905350000000";
    private String buyerIp = "127.0.0.1";
    private String address = "Altunizade Mah. Inci Cikmazi Sokak No: 3 Uskudar Istanbul";
    private String city = "Istanbul";
    private String country = "Turkey";
    private String zipCode = "34742";
}
