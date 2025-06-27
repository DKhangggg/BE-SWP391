package com.example.gender_healthcare_service.service;

import com.example.gender_healthcare_service.dto.request.ReminderRequestDTO;
import com.example.gender_healthcare_service.dto.response.ReminderResponseDTO;
import com.example.gender_healthcare_service.entity.Reminder;

import java.util.List;

public interface ReminderService {
    List<ReminderResponseDTO> getUserReminders();

    List<ReminderResponseDTO> getRemindersByUserId(Integer userId);

    ReminderResponseDTO createReminder(ReminderRequestDTO reminderRequestDTO);

    ReminderResponseDTO updateReminder(Integer id, ReminderRequestDTO reminderRequestDTO);

    void deleteReminder(Integer id);

    ReminderResponseDTO getReminderById(Integer id);
}
