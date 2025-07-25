package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.dto.request.AdminUpdateUserRequestDTO;
import com.example.gender_healthcare_service.dto.request.UserProfileRequest;
import com.example.gender_healthcare_service.dto.response.UserResponseDTO;
import com.example.gender_healthcare_service.exception.ServiceNotFoundException; // Assuming you have this for user not found
import com.example.gender_healthcare_service.repository.UserRepository;
import com.example.gender_healthcare_service.entity.User;
import com.example.gender_healthcare_service.service.UserService;

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

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;

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
            return modelMapper.map(userValid, UserResponseDTO.class);
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
            // Consider adding email uniqueness validation if it's a requirement
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
}
