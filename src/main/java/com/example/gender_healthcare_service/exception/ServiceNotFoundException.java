package com.example.gender_healthcare_service.exception;

public class ServiceNotFoundException extends RuntimeException {
    public ServiceNotFoundException(String message) {
        super(message);
    }

    public ServiceNotFoundException(Integer id) {
        super("Testing service not found with id: " + id);
    }
}
