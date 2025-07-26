package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.dto.request.AdminUpdateUserRequestDTO;
import com.example.gender_healthcare_service.dto.request.UserProfileRequest;
import com.example.gender_healthcare_service.dto.response.UserResponseDTO;
import com.example.gender_healthcare_service.exception.ServiceNotFoundException;
import com.example.gender_healthcare_service.repository.UserRepository;
import com.example.gender_healthcare_service.entity.User;
import com.example.gender_healthcare_service.service.UserService;
import com.example.gender_healthcare_service.service.CloudinaryService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    
    @Autowired
    private CloudinaryService cloudinaryService;

    @Value("${file.upload.path:uploads/}")
    private String uploadPath;

    @Override
    public User findByUserName(String userName) {
        return userRepository.findUserByUsername(userName);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponseDTO> userResponseDTOs = users.stream()
                .map(user -> modelMapper.map(user, UserResponseDTO.class))
                .toList();
        return userResponseDTOs;
    }

    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public UserResponseDTO getInfo() {
        try {
            String username =  SecurityContextHolder.getContext().getAuthentication().getName();
            User userValid = userRepository.findUserByUsername(username);
            if (userValid == null) {
               throw new RuntimeException("User not found");
            }
            
            UserResponseDTO userResponse = modelMapper.map(userValid, UserResponseDTO.class);
            
            // Validate và cập nhật avatar URL
            if (userValid.getAvatarPublicId() != null && !userValid.getAvatarPublicId().trim().isEmpty()) {
                String validatedUrl = cloudinaryService.getImageUrlWithFallback(
                    userValid.getAvatarPublicId(), 
                    getDefaultAvatarUrl()
                );
                userResponse.setAvatarUrl(validatedUrl);
            } else {
                userResponse.setAvatarUrl(getDefaultAvatarUrl());
            }
            
            return userResponse;
        } catch (Exception e) {
            System.out.println("Error fetching user info: " + e.getMessage());
        }
        return null;
    }

    @Override
    public UserResponseDTO updateUser(UserProfileRequest userProfile) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findUserByUsername(username);
            if (user == null) {
                throw new RuntimeException("User not found");
            }

            if (userProfile != null) {
            if(userProfile.getFullName() != null) {
                user.setFullName(userProfile.getFullName());
            }
            if(userProfile.getEmail() != null) {
                user.setEmail(userProfile.getEmail());
            }
            if(userProfile.getPhoneNumber() != null) {
                user.setPhoneNumber(userProfile.getPhoneNumber());
            }
            if (userProfile.getAddress() != null) {
                user.setAddress(userProfile.getAddress());
            }

            if (userProfile.getGender() != null) {
                user.setGender(userProfile.getGender());
            }
            if (userProfile.getDateOfBirth() != null) {
               user.setDateOfBirth(userProfile.getDateOfBirth());
            }
            if (userProfile.getDescription() != null) {
                user.setDescription(userProfile.getDescription());
            }
            if (userProfile.getMedicalHistory() != null) {
                user.setMedicalHistory(userProfile.getMedicalHistory());
            }
                user.setUpdatedAt(LocalDateTime.now());
                userRepository.save(user);
                return modelMapper.map(user, UserResponseDTO.class);
            }
        } catch (Exception e) {
            System.out.println("Error updating user profile: " + e.getMessage());
            throw new RuntimeException("Failed to update user profile: " + e.getMessage());
        }
        return null;
    }

    @Override
    public UserResponseDTO updateUserByAdmin(Integer userId, AdminUpdateUserRequestDTO updateUserDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServiceNotFoundException("User not found with ID: " + userId));

        if (updateUserDTO.getFullName() != null) {
            user.setFullName(updateUserDTO.getFullName());
        }
        if (updateUserDTO.getEmail() != null) {
            user.setEmail(updateUserDTO.getEmail());
        }
        if (updateUserDTO.getPhoneNumber() != null) {
            user.setPhoneNumber(updateUserDTO.getPhoneNumber());
        }
        if (updateUserDTO.getAddress() != null) {
            user.setAddress(updateUserDTO.getAddress());
        }
        if (updateUserDTO.getGender() != null) {
            user.setGender(updateUserDTO.getGender());
        }
        if (updateUserDTO.getDateOfBirth() != null) {
            try {
                user.setDateOfBirth(LocalDate.parse(updateUserDTO.getDateOfBirth()));
            } catch (DateTimeParseException e) {
                throw new RuntimeException("Invalid date of birth format. Please use YYYY-MM-DD.", e);
            }
        }
        if (updateUserDTO.getRoleName() != null) {

            user.setRoleName(updateUserDTO.getRoleName());
        }
        if (updateUserDTO.getIsDeleted() != null) {
            user.setIsDeleted(updateUserDTO.getIsDeleted());
        }
        User updatedUser = userRepository.save(user);
        return modelMapper.map(updatedUser, UserResponseDTO.class);
    }

    @Override
    public void deleteUserByAdmin(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServiceNotFoundException("User not found with ID: " + userId));


        user.setIsDeleted(true);

        userRepository.save(user);

    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public User findById(Integer userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public String uploadAvatar(MultipartFile file, Integer userId) {
        try {
            User user = findById(userId);
            if (user == null) {
                throw new RuntimeException("User not found with ID: " + userId);
            }

            String oldPublicId = user.getAvatarPublicId();
            if (oldPublicId != null && !oldPublicId.trim().isEmpty()) {
                cloudinaryService.deleteOldImage(oldPublicId);
            }

            Map<String, Object> uploadResult = cloudinaryService.uploadImage(file);
            String secureUrl = (String) uploadResult.get("secure_url");
            String publicId = (String) uploadResult.get("public_id");
            
            user.setAvatarUrl(secureUrl);
            user.setAvatarPublicId(publicId);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);

            log.info("Avatar uploaded successfully for user {}: {}", userId, publicId);
            return secureUrl;
        } catch (Exception e) {
            log.error("Upload avatar failed for user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Upload avatar failed: " + e.getMessage());
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

    private String getDefaultAvatarUrl() {
        return "https://res.cloudinary.com/demo/image/upload/v1/samples/people/boy-snow-hoodie.jpg";
    }

    @Override
    public boolean deleteAvatar(Integer userId) {
        try {
            User user = findById(userId);
            if (user == null) {
                throw new RuntimeException("User not found with ID: " + userId);
            }

            String publicId = user.getAvatarPublicId();
            if (publicId != null && !publicId.trim().isEmpty()) {
                cloudinaryService.deleteOldImage(publicId);
            }

            user.setAvatarUrl(null);
            user.setAvatarPublicId(null);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);

            log.info("Avatar deleted successfully for user: {}", userId);
            return true;
        } catch (Exception e) {
            log.error("Delete avatar failed for user {}: {}", userId, e.getMessage());
            return false;
        }
    }

    private String getExtension(String filename) {
        if (filename == null) return "";
        int dot = filename.lastIndexOf('.');
        return (dot == -1) ? "" : filename.substring(dot + 1);
    }

    @Override
    public UserResponseDTO updateUserAvatar(String publicId, String avatarUrl) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findUserByUsername(username);
            if (user == null) {
                throw new RuntimeException("User not found");
            }

            // Xóa avatar cũ nếu có
            String oldPublicId = user.getAvatarPublicId();
            if (oldPublicId != null && !oldPublicId.trim().isEmpty()) {
                cloudinaryService.deleteOldImage(oldPublicId);
            }

            // Cập nhật avatar mới
            user.setAvatarUrl(avatarUrl);
            user.setAvatarPublicId(publicId);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);

            log.info("Avatar updated successfully for user {}: {}", user.getId(), publicId);
            return modelMapper.map(user, UserResponseDTO.class);
        } catch (Exception e) {
            log.error("Update avatar failed: {}", e.getMessage());
            throw new RuntimeException("Update avatar failed: " + e.getMessage());
        }
    }

    @Override
    public UserResponseDTO deleteUserAvatar() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findUserByUsername(username);
            if (user == null) {
                throw new RuntimeException("User not found");
            }

            // Xóa avatar trên Cloudinary nếu có
            String publicId = user.getAvatarPublicId();
            if (publicId != null && !publicId.trim().isEmpty()) {
                cloudinaryService.deleteOldImage(publicId);
            }

            // Xóa thông tin avatar trong database
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
}
