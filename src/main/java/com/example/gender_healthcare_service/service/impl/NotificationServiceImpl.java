package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.entity.Notification;
import com.example.gender_healthcare_service.entity.User;
import com.example.gender_healthcare_service.repository.NotificationRepository;
import com.example.gender_healthcare_service.service.INotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements INotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public void createNotification(User user, String message, String link) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setLink(link);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }
}

