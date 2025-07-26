package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.dto.response.UserResponseDTO;
import com.example.gender_healthcare_service.entity.User;
import com.example.gender_healthcare_service.repository.UserRepository;
import com.example.gender_healthcare_service.service.AvatarService;
import com.example.gender_healthcare_service.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AvatarServiceImpl implements AvatarService {

    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final ModelMapper modelMapper;

    @Override
    public UserResponseDTO uploadAvatar(MultipartFile file) {
        try {

            validateFile(file);
            
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findUserByUsername(username);
            if (user == null) {
                throw new RuntimeException("User not found");
            }

            String oldPublicId = user.getAvatarPublicId();
            if (oldPublicId != null && !oldPublicId.trim().isEmpty()) {
                cloudinaryService.deleteOldImage(oldPublicId);
                log.info("Old avatar deleted: {}", oldPublicId);
            }

            Map<String, Object> uploadResult = cloudinaryService.uploadImage(file);
            String publicId = (String) uploadResult.get("public_id");
            String secureUrl = (String) uploadResult.get("secure_url");
            
            user.setAvatarUrl(secureUrl);
            user.setAvatarPublicId(publicId);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);

            log.info("Avatar uploaded successfully for user {}: {}", user.getId(), publicId);
            
            return modelMapper.map(user, UserResponseDTO.class);
            
        } catch (Exception e) {
            log.error("Upload avatar failed: {}", e.getMessage());
            throw new RuntimeException("Upload avatar failed: " + e.getMessage());
        }
    }

    @Override
    public UserResponseDTO deleteAvatar() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findUserByUsername(username);
            if (user == null) {
                throw new RuntimeException("User not found");
            }

            String publicId = user.getAvatarPublicId();
            if (publicId != null && !publicId.trim().isEmpty()) {
                cloudinaryService.deleteOldImage(publicId);
                log.info("Avatar deleted from Cloudinary: {}", publicId);
            }

            user.setAvatarUrl(null);
            user.setAvatarPublicId(null);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);

            log.info("Avatar deleted successfully for user: {}", user.getId());
            return modelMapper.map(user, UserResponseDTO.class);
            
        } catch (Exception e) {
            log.error("Delete avatar failed: {}", e.getMessage());
            throw new RuntimeException("Delete avatar failed: " + e.getMessage());
        }
    }

    @Override
    public String validateAvatarUrl(String avatarUrl, String avatarPublicId) {
        if (avatarUrl == null || avatarUrl.trim().isEmpty()) {
            return getDefaultAvatarUrl();
        }

        if (avatarPublicId != null && !avatarPublicId.trim().isEmpty()) {
            if (!cloudinaryService.imageExists(avatarPublicId)) {
                log.warn("Avatar image not found on Cloudinary: {}", avatarPublicId);
                return getDefaultAvatarUrl();
            }
        }

        return avatarUrl;
    }

    @Override
    public boolean hasAvatar() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findUserByUsername(username);
            if (user == null) {
                return false;
            }
            
            return user.getAvatarPublicId() != null && 
                   !user.getAvatarPublicId().trim().isEmpty() &&
                   cloudinaryService.imageExists(user.getAvatarPublicId());
        } catch (Exception e) {
            log.error("Error checking avatar existence: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public String getCurrentAvatarUrl() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findUserByUsername(username);
            if (user == null) {
                return getDefaultAvatarUrl();
            }
            
            return validateAvatarUrl(user.getAvatarUrl(), user.getAvatarPublicId());
        } catch (Exception e) {
            log.error("Error getting current avatar URL: {}", e.getMessage());
            return getDefaultAvatarUrl();
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File không được để trống!");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Chỉ chấp nhận file ảnh!");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("Kích thước file không được vượt quá 5MB!");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            String extension = getExtension(originalFilename).toLowerCase();
            if (!extension.matches("(jpg|jpeg|png|gif|webp)")) {
                throw new IllegalArgumentException("Chỉ chấp nhận file ảnh có định dạng: JPG, JPEG, PNG, GIF, WEBP!");
            }
        }
    }

    private String getDefaultAvatarUrl() {
        return "https://res.cloudinary.com/demo/image/upload/v1/samples/people/boy-snow-hoodie.jpg";
    }

    private String getExtension(String filename) {
        if (filename == null) return "";
        int dot = filename.lastIndexOf('.');
        return (dot == -1) ? "" : filename.substring(dot + 1);
    }
} 