package com.example.gender_healthcare_service.service;

import com.example.gender_healthcare_service.dto.request.UserProfileRequest;
import com.example.gender_healthcare_service.dto.response.UserResponseDTO;
import com.example.gender_healthcare_service.dto.request.AdminUpdateUserRequestDTO;
import com.example.gender_healthcare_service.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    User findByUserName(String userName);
    List<UserResponseDTO> getAllUsers();
    Page<User> getAllUsers(Pageable pageable);
    UserResponseDTO getInfo();
    UserResponseDTO updateUser(UserProfileRequest user);
    UserResponseDTO updateUserByAdmin(Integer userId, AdminUpdateUserRequestDTO updateUserDTO);
    void deleteUserByAdmin(Integer userId);

    User findById(Integer userId);

    User findUserByUsername(String username);
}
