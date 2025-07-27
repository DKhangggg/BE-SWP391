package com.example.gender_healthcare_service.service;

import com.example.gender_healthcare_service.entity.User;
import com.example.gender_healthcare_service.dto.response.NotificationResponseDTO;

import java.util.List;

public interface INotificationService {
    void createNotification(User user, String message, String link);
    void createNotification(User user, String title, String message, String link, String type, String description);
    List<NotificationResponseDTO> getUserNotifications(Integer userId);
    List<NotificationResponseDTO> getUserNotificationsByType(Integer userId, String type);
    long getUnreadNotificationCount(Integer userId);
    void markNotificationAsRead(Long notificationId);
    void markAllNotificationsAsRead(Integer userId);
}

