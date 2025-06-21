package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.entity.Reminder;
import com.example.gender_healthcare_service.entity.User;
import com.example.gender_healthcare_service.repository.ReminderRepository;
import com.example.gender_healthcare_service.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReminderServiceImpl implements ReminderService {

    private final ReminderRepository reminderRepository;

    @Override
    public List<Reminder> getUserReminders() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return reminderRepository.findByUser(currentUser);
    }
}

