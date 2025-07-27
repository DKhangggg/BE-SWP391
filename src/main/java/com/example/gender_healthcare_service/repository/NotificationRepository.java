package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser_IdAndIsReadFalseOrderByCreatedAtDesc(Integer userId);
    List<Notification> findByUser_IdOrderByCreatedAtDesc(Integer userId);
    long countByUser_IdAndIsReadFalse(Integer userId);
    List<Notification> findByUser_IdAndTypeOrderByCreatedAtDesc(Integer userId, String type);
}

