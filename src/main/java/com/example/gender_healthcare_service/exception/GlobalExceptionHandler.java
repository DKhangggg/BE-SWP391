package com.example.gender_healthcare_service.exception;

import com.example.gender_healthcare_service.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<String>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, WebRequest request) {
        ApiResponse<String> response = ApiResponse.error("Yêu cầu không hợp lệ. Vui lòng kiểm tra định dạng JSON.");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BookingConflictException.class)
    public ResponseEntity<ApiResponse<String>> handleBookingConflict(BookingConflictException ex) {
        ApiResponse<String> response = ApiResponse.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(ServiceNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleServiceNotFound(ServiceNotFoundException ex) {
        ApiResponse<String> response = ApiResponse.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalState(IllegalStateException ex) {
        ApiResponse<String> response = ApiResponse.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}

