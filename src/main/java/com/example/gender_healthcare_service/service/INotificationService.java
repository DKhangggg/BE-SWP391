package com.example.gender_healthcare_service.service;

import com.example.gender_healthcare_service.entity.User;

public interface INotificationService {
    void createNotification(User user, String message, String link);
}

