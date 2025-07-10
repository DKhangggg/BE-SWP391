package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.dto.request.ReminderRequestDTO;
import com.example.gender_healthcare_service.dto.response.ReminderResponseDTO;
import com.example.gender_healthcare_service.entity.Reminder;
import com.example.gender_healthcare_service.entity.User;
import com.example.gender_healthcare_service.repository.ReminderRepository;
import com.example.gender_healthcare_service.repository.UserRepository;
import com.example.gender_healthcare_service.service.ReminderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReminderServiceImpl implements ReminderService {

    private final ReminderRepository reminderRepository;
    private final UserRepository userRepository;

    @Override
    public List<ReminderResponseDTO> getUserReminders() {
        String userName =  SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findUserByUsername(userName);
        if (currentUser == null) {
           throw  new EntityNotFoundException("User not found with username: " + userName);
        }
        List<ReminderResponseDTO> reminders = reminderRepository.findByUser(currentUser).stream()
                .filter(reminder -> !Boolean.TRUE.equals(reminder.getIsDeleted()))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return reminders;
    }

    @Override
    public List<ReminderResponseDTO> getRemindersByUserId(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        return reminderRepository.findByUser(user).stream()
                .filter(reminder -> !Boolean.TRUE.equals(reminder.getIsDeleted()))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ReminderResponseDTO createReminder(ReminderRequestDTO reminderRequestDTO) {
        User user = userRepository.findById(reminderRequestDTO.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + reminderRequestDTO.getUserId()));

        Reminder reminder = new Reminder();
        reminder.setUser(user);
        reminder.setReminderType(reminderRequestDTO.getReminderType());
        reminder.setMessage(reminderRequestDTO.getMessage());
        reminder.setReminderDate(reminderRequestDTO.getReminderDate().atStartOfDay());
        reminder.setIsSent(false);
        reminder.setCreatedAt(LocalDateTime.now());
        reminder.setIsDeleted(false);

        Reminder savedReminder = reminderRepository.save(reminder);
        return mapToDTO(savedReminder);
    }

    @Override
    public ReminderResponseDTO updateReminder(Integer id, ReminderRequestDTO reminderRequestDTO) {
        Reminder existingReminder = reminderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reminder not found with id: " + id));

        if (Boolean.TRUE.equals(existingReminder.getIsDeleted())) {
            throw new EntityNotFoundException("Reminder has been deleted");
        }

        if (reminderRequestDTO.getUserId() != null && !existingReminder.getUser().getId().equals(reminderRequestDTO.getUserId())) {
            User newUser = userRepository.findById(reminderRequestDTO.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + reminderRequestDTO.getUserId()));
            existingReminder.setUser(newUser);
        }

        if (reminderRequestDTO.getReminderType() != null) {
            existingReminder.setReminderType(reminderRequestDTO.getReminderType());
        }

        if (reminderRequestDTO.getMessage() != null) {
            existingReminder.setMessage(reminderRequestDTO.getMessage());
        }

        if (reminderRequestDTO.getReminderDate() != null) {
            existingReminder.setReminderDate(reminderRequestDTO.getReminderDate().atStartOfDay());
        }

        Reminder updatedReminder = reminderRepository.save(existingReminder);
        return mapToDTO(updatedReminder);
    }

    @Override
    public void deleteReminder(Integer id) {
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reminder not found with id: " + id));

        reminder.setIsDeleted(true);
        reminderRepository.save(reminder);
    }

    @Override
    public ReminderResponseDTO getReminderById(Integer id) {
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reminder not found with id: " + id));

        if (Boolean.TRUE.equals(reminder.getIsDeleted())) {
            throw new EntityNotFoundException("Reminder has been deleted");
        }

        return mapToDTO(reminder);
    }

    private ReminderResponseDTO mapToDTO(Reminder reminder) {
        ReminderResponseDTO dto = new ReminderResponseDTO();
        dto.setId(reminder.getId());
        dto.setReminderType(reminder.getReminderType());
        dto.setMessage(reminder.getMessage());
        dto.setReminderDate(reminder.getReminderDate().atStartOfDay());
        dto.setIsSent(reminder.getIsSent());
        dto.setCreatedAt(reminder.getCreatedAt());
        dto.setUserId(reminder.getUser().getId());
        dto.setUsername(reminder.getUser().getUsername());
        return dto;
    }
}
