package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.dto.response.ApiResponse;
import com.example.gender_healthcare_service.entity.Payment;
import com.example.gender_healthcare_service.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> createPayment(@RequestBody String paymentDetails) {
        try {
            String result = paymentService.createPayment(paymentDetails);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Tạo thanh toán thành công", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi tạo thanh toán: " + e.getMessage()));
        }
    }

    @GetMapping("/{paymentId}/status")
    public ResponseEntity<ApiResponse<String>> getPaymentStatus(@PathVariable String paymentId) {
        try {
            String status = paymentService.getPaymentStatus(paymentId);
            return ResponseEntity.ok(ApiResponse.success("Lấy trạng thái thanh toán thành công", status));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy trạng thái thanh toán: " + e.getMessage()));
        }
    }

    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<ApiResponse<String>> refundPayment(@PathVariable String paymentId) {
        try {
            String result = paymentService.refundPayment(paymentId);
            return ResponseEntity.ok(ApiResponse.success("Hoàn tiền thành công", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi hoàn tiền: " + e.getMessage()));
        }
    }

    @PostMapping("/{paymentId}/cancel")
    public ResponseEntity<ApiResponse<String>> cancelPayment(@PathVariable String paymentId) {
        try {
            String result = paymentService.cancelPayment(paymentId);
            return ResponseEntity.ok(ApiResponse.success("Hủy thanh toán thành công", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi hủy thanh toán: " + e.getMessage()));
        }
    }

    @PutMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<String>> updatePayment(@PathVariable String paymentId, @RequestBody String paymentDetails) {
        try {
            String result = paymentService.updatePayment(paymentId, paymentDetails);
            return ResponseEntity.ok(ApiResponse.success("Cập nhật thanh toán thành công", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi cập nhật thanh toán: " + e.getMessage()));
        }
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<ApiResponse<String>> getPaymentHistory(@PathVariable String userId) {
        try {
            String history = paymentService.getPaymentHistory(userId);
            return ResponseEntity.ok(ApiResponse.success("Lấy lịch sử thanh toán thành công", history));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy lịch sử thanh toán: " + e.getMessage()));
        }
    }

    @GetMapping("/all/{userId}")
    public ResponseEntity<ApiResponse<String>> getAllPayments(@PathVariable String userId) {
        try {
            String payments = paymentService.getAllPayments(userId);
            return ResponseEntity.ok(ApiResponse.success("Lấy tất cả thanh toán thành công", payments));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy tất cả thanh toán: " + e.getMessage()));
        }
    }
}
