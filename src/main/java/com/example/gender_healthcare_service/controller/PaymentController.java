package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.entity.Payment;
import com.example.gender_healthcare_service.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;



}
