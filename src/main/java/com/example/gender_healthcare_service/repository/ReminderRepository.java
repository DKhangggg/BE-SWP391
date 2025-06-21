package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.entity.Reminder;
import com.example.gender_healthcare_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Integer> {
    List<Reminder> findByUser(User user);
}

