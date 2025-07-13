package com.example.gender_healthcare_service.exception;

import com.example.gender_healthcare_service.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, WebRequest request) {
        // Log the exception for debugging purposes
        System.err.println("Failed to read request body: " + ex.getMessage());

        // It's helpful to see what the frontend is actually sending
        // Note: This is for debugging and might not be suitable for production
        // as it can expose sensitive data in logs.
        try {
            if (request instanceof org.springframework.web.context.request.ServletWebRequest) {
                jakarta.servlet.http.HttpServletRequest servletRequest = ((org.springframework.web.context.request.ServletWebRequest) request).getRequest();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(servletRequest.getInputStream()))) {
                    String body = reader.lines().collect(Collectors.joining(System.lineSeparator()));
                    System.err.println("Raw request body: " + body);
                }
            }
        } catch (IOException e) {
            System.err.println("Could not read request body for debugging: " + e.getMessage());
        }

        ApiResponse response = new ApiResponse(false, "Yêu cầu không hợp lệ. Vui lòng kiểm tra định dạng JSON.");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BookingConflictException.class)
    public ResponseEntity<ApiResponse> handleBookingConflict(BookingConflictException ex) {
        ApiResponse response = new ApiResponse(false, ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(ServiceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleServiceNotFound(ServiceNotFoundException ex) {
        ApiResponse response = new ApiResponse(false, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse> handleIllegalState(IllegalStateException ex) {
        ApiResponse response = new ApiResponse(false, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}

