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
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;

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
    public UserResponseDTO getInfo() {
        try {
            User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User userValid = userRepository.findUserByUsername(currentUser.getUsername());
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
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user != null && userProfile != null) {
            user.setFullName(userProfile.getFullName());
            user.setEmail(userProfile.getEmail());
            user.setPhoneNumber(userProfile.getPhoneNumber());
            user.setGender(userProfile.getGender());
            try {
                LocalDate birthDate = LocalDate.parse(userProfile.getDateOfBirth().toString());
                user.setDateOfBirth(birthDate);
            } catch (Exception e) {
                System.out.println("Invalid date format: " + e.getMessage());
            }
            user.setUpdatedAt(new Date().toInstant());
            userRepository.save(user);
            return modelMapper.map(user, UserResponseDTO.class);
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
}
