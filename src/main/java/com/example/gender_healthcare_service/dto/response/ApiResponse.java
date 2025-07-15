package com.example.gender_healthcare_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    private String operation;
    
    public static <T> ApiResponse<T> success(String message, T data, String operation) {
        return new ApiResponse<>(true, message, data, LocalDateTime.now(), operation);
    }
    
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, LocalDateTime.now(), null);
    }


    public static <T> ApiResponse<T> success(String message, T data) {
        return success(message, data, null);
    }
}
