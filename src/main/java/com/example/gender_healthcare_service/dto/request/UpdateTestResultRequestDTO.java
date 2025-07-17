package com.example.gender_healthcare_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTestResultRequestDTO {
    
    @NotBlank(message = "Kết quả xét nghiệm không được để trống")
    private String result;
    
    private String resultType; // "Bình thường", "Bất thường", "Chờ kết quả"
    
    private String notes; // Ghi chú thêm từ bác sĩ/kỹ thuật viên
    
    @NotNull(message = "Ngày trả kết quả không được để trống")
    private LocalDateTime resultDate;
    
    private String updatedBy; // Người cập nhật kết quả
}
