package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.dto.request.StaffRequestDTO;
import com.example.gender_healthcare_service.entity.User;
import com.example.gender_healthcare_service.entity.enumpackage.RequestStatus;
import com.example.gender_healthcare_service.repository.UserRepository;
import com.example.gender_healthcare_service.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<User> getAllStaff() {
        return userRepository.findAllByRoleNameAndIsDeletedFalse("ROLE_STAFF");
    }

    @Override
    public User getStaffById(Integer id) {
        return userRepository.findByIdAndRoleName(id, "ROLE_STAFF").orElse(null);
    }

    @Override
    public User createStaff(StaffRequestDTO dto) {
        User user = modelMapper.map(dto, User.class);
        user.setUsername(dto.getEmail());
        user.setRoleName("ROLE_STAFF");
        user.setIsDeleted(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setPasswordHash(passwordEncoder.encode("123456")); // default pass
        return userRepository.save(user);
    }

    @Override
    public User updateStaff(Integer id, StaffRequestDTO dto) {
        User user = getStaffById(id);
        if (user != null) {
            user.setFullName(dto.getFullName());
            user.setPhoneNumber(dto.getPhoneNumber());
            user.setAddress(dto.getAddress());
            user.setStatus(RequestStatus.valueOf(dto.getStatus()));
            user.setUpdatedAt(LocalDateTime.now());
            return userRepository.save(user);
        }
        return null;
    }

    @Override
    public boolean deleteStaff(Integer id) {
        User user = getStaffById(id);
        if (user != null) {
            user.setIsDeleted(true);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateStatus(Integer id, String status) {
        User user = getStaffById(id);
        if (user != null) {
            user.setStatus(RequestStatus.valueOf(status));
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
            return true;
        }
        return false;
    }
}
