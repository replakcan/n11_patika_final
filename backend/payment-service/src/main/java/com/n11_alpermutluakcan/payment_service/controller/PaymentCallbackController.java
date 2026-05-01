package com.n11_alpermutluakcan.payment_service.controller;

import com.n11_alpermutluakcan.payment_service.config.IyzicoProperties;
import com.n11_alpermutluakcan.payment_service.service.PaymentSagaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments/callback")
@RequiredArgsConstructor
public class PaymentCallbackController {

    private final PaymentSagaService paymentSagaService;
    private final IyzicoProperties iyzicoProperties;

    @PostMapping
    public ResponseEntity<String> handlePostCallback(@RequestParam("token") String token) {
        Long orderId = paymentSagaService.handleCallback(token);
        return redirectPage(orderId);
    }

    @GetMapping
    public ResponseEntity<String> handleGetCallback(@RequestParam("token") String token) {
        Long orderId = paymentSagaService.handleCallback(token);
        return redirectPage(orderId);
    }

    private ResponseEntity<String> redirectPage(Long orderId) {
        String frontendBaseUrl = iyzicoProperties.getFrontendBaseUrl().replaceAll("/+$", "");
        String redirectUrl = frontendBaseUrl + "/orders?payment=confirmed&orderId=" + orderId;
        String html = """
                <!doctype html>
                <html lang="en">
                <head>
                  <meta charset="utf-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1">
                  <meta http-equiv="refresh" content="0;url=%s">
                  <title>Payment processed</title>
                </head>
                <body>
                  <p>Payment processed. Redirecting to your orders...</p>
                  <script>window.top.location.replace("%s");</script>
                </body>
                </html>
                """.formatted(redirectUrl, redirectUrl);

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }
}
