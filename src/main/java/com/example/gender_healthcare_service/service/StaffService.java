package com.example.gender_healthcare_service.service;

import com.example.gender_healthcare_service.dto.request.StaffRequestDTO;
import com.example.gender_healthcare_service.entity.User;

import java.util.List;

public interface StaffService {
    List<User> getAllStaff();
    User getStaffById(Integer id);
    User createStaff(StaffRequestDTO dto);
    User updateStaff(Integer id, StaffRequestDTO dto);
    boolean deleteStaff(Integer id);
    boolean updateStatus(Integer id, String status);
}
