package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.entity.Payment;
import com.example.gender_healthcare_service.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<String> createPayment(@RequestBody String paymentDetails) {
        String result = paymentService.createPayment(paymentDetails);
        return ResponseEntity.status(201).body(result);
    }

    @GetMapping("/{paymentId}/status")
    public ResponseEntity<String> getPaymentStatus(@PathVariable String paymentId) {
        String status = paymentService.getPaymentStatus(paymentId);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<String> refundPayment(@PathVariable String paymentId) {
        String result = paymentService.refundPayment(paymentId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{paymentId}/cancel")
    public ResponseEntity<String> cancelPayment(@PathVariable String paymentId) {
        String result = paymentService.cancelPayment(paymentId);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{paymentId}")
    public ResponseEntity<String> updatePayment(@PathVariable String paymentId, @RequestBody String paymentDetails) {
        String result = paymentService.updatePayment(paymentId, paymentDetails);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<String> getPaymentHistory(@PathVariable String userId) {
        String history = paymentService.getPaymentHistory(userId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/all/{userId}")
    public ResponseEntity<String> getAllPayments(@PathVariable String userId) {
        String payments = paymentService.getAllPayments(userId);
        return ResponseEntity.ok(payments);
    }
}
