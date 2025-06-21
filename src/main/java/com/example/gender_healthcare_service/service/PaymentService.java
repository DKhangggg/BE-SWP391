package com.example.gender_healthcare_service.service;

public interface PaymentService  {
    public String createPayment(String paymentDetails);
    public String getPaymentStatus(String paymentId);
    public String refundPayment(String paymentId);
    public String cancelPayment(String paymentId);
    public String updatePayment(String paymentId, String paymentDetails);
    public String getPaymentHistory(String userId);
    public String getAllPayments(String userId);
}
