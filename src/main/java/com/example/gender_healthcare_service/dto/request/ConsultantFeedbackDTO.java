package com.example.gender_healthcare_service.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class ConsultantFeedbackDTO {
    // customerId đã được xóa - sẽ tự động lấy từ user hiện tại
    private Integer consultantId;
    private Integer consultationId; // Thêm consultationId
    private Integer bookingId; // Thêm bookingId
    private String comment;
    private Integer rating;
}
