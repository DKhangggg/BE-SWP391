package com.example.gender_healthcare_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingStatusUpdateDTO {
    private Integer bookingId;
    private String customerName;
    private String serviceName;
    private String status;
    private String previousStatus;
    private String message;
    private LocalDateTime timestamp;
    private String updatedBy;
    private String notes;
    
    // Constructor cho quick updates
    public BookingStatusUpdateDTO(Integer bookingId, String status, String message) {
        this.bookingId = bookingId;
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
    
    // Constructor với customer info
    public BookingStatusUpdateDTO(Integer bookingId, String customerName, String serviceName, 
                                 String status, String message, String updatedBy) {
        this.bookingId = bookingId;
        this.customerName = customerName;
        this.serviceName = serviceName;
        this.status = status;
        this.message = message;
        this.updatedBy = updatedBy;
        this.timestamp = LocalDateTime.now();
    }
} 