package com.example.gender_healthcare_service.service;

import com.example.gender_healthcare_service.dto.request.UserProfileRequest;
import com.example.gender_healthcare_service.dto.response.UserResponseDTO;
import com.example.gender_healthcare_service.dto.request.AdminUpdateUserRequestDTO;
import com.example.gender_healthcare_service.entity.User;

import java.util.List;

public interface UserService {
    User findByUserName(String userName);
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO getInfo();
    UserResponseDTO updateUser(UserProfileRequest user);
    UserResponseDTO updateUserByAdmin(Integer userId, AdminUpdateUserRequestDTO updateUserDTO);
    void deleteUserByAdmin(Integer userId);
}
