package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.entity.Notification;
import com.example.gender_healthcare_service.entity.User;
import com.example.gender_healthcare_service.repository.NotificationRepository;
import com.example.gender_healthcare_service.service.INotificationService;
import com.example.gender_healthcare_service.dto.response.NotificationResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public void createNotification(User user, String title, String message, String link, String type, String description) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setLink(link);
        notification.setType(type);
        notification.setDescription(description);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    @Override
    public List<NotificationResponseDTO> getUserNotifications(Integer userId) {
        List<Notification> notifications = notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationResponseDTO> getUserNotificationsByType(Integer userId, String type) {
        List<Notification> notifications = notificationRepository.findByUser_IdAndTypeOrderByCreatedAtDesc(userId, type);
        return notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public long getUnreadNotificationCount(Integer userId) {
        return notificationRepository.countByUser_IdAndIsReadFalse(userId);
    }

    @Override
    public void markNotificationAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    public void markAllNotificationsAsRead(Integer userId) {
        List<Notification> unreadNotifications = notificationRepository.findByUser_IdAndIsReadFalseOrderByCreatedAtDesc(userId);
        unreadNotifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }

    private NotificationResponseDTO convertToDTO(Notification notification) {
        return new NotificationResponseDTO(
                notification.getId(),
                notification.getMessage(),
                notification.isRead(),
                notification.getLink(),
                notification.getCreatedAt(),
                notification.getType(),
                notification.getTitle(),
                notification.getDescription()
        );
    }
}

