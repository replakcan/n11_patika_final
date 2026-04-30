package com.n11_alpermutluakcan.payment_service.controller;

import com.n11_alpermutluakcan.payment_service.service.PaymentSagaService;
import lombok.RequiredArgsConstructor;
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

    @PostMapping
    public ResponseEntity<String> handlePostCallback(@RequestParam("token") String token) {
        paymentSagaService.handleCallback(token);
        return ResponseEntity.ok("Payment callback processed.");
    }

    @GetMapping
    public ResponseEntity<String> handleGetCallback(@RequestParam("token") String token) {
        paymentSagaService.handleCallback(token);
        return ResponseEntity.ok("Payment callback processed.");
    }
}
