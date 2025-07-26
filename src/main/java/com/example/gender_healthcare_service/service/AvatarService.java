package com.example.gender_healthcare_service.service;

import com.example.gender_healthcare_service.dto.response.UserResponseDTO;
import org.springframework.web.multipart.MultipartFile;

public interface AvatarService {
    

    UserResponseDTO uploadAvatar(MultipartFile file);
    UserResponseDTO deleteAvatar();
    String validateAvatarUrl(String avatarUrl, String avatarPublicId);
    boolean hasAvatar();
    String getCurrentAvatarUrl();
} 