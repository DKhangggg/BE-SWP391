package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.dto.request.*;
import com.example.gender_healthcare_service.dto.response.AuthResponseDTO;
import com.example.gender_healthcare_service.service.AuthenticationService;
import com.example.gender_healthcare_service.service.JwtService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RequestMapping("/api/auth")
@RestController
@Tag(name = "Authentication", description = "API endpoints for user authentication and authorization")
public class AuthController {
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private JwtService jwtService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody(required = false) LoginRequest loginRequestBody) {
        if (loginRequestBody != null) {
            return authenticationService.loginUser(loginRequestBody);
        } else {
            return ResponseEntity.badRequest().body("Yêu cầu đăng nhập không hợp lệ");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest register) {
        try {
            ResponseEntity<?> serviceResponse = authenticationService.registerUser(register);
            return serviceResponse;
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Đã xảy ra lỗi không mong muốn trong quá trình đăng ký.");
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequestDTO requestDTO) {
        if (requestDTO == null || requestDTO.getRefreshToken() == null || requestDTO.getRefreshToken().isEmpty()) {
            return ResponseEntity.badRequest().body("Refresh token là bắt buộc.");
        }
        return authenticationService.refreshAccessToken(requestDTO.getRefreshToken());
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequestDTO requestDTO) {
        if (requestDTO == null) {
            return ResponseEntity.badRequest().body("Email là bắt buộc (DTO là null).");
        }
        if (requestDTO.getEmail() == null || requestDTO.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body("Email là bắt buộc (trường email là null/trống).");
        }
        return authenticationService.sendResetPasswordEmail(requestDTO.getEmail(), requestDTO.getOtpVerificationLink());
    }

    // Validate OTP - Checks if OTP is valid without changing password
    @PostMapping("/validate-otp")
    public ResponseEntity<?> validateOtp(@RequestBody ValidateOtpRequestDTO requestDTO) {
        if (requestDTO == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Vui lòng cung cấp email và OTP."));
        }
        if (requestDTO.getEmail() == null || requestDTO.getEmail().isEmpty() ||
                requestDTO.getOtpCode() == null || requestDTO.getOtpCode().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Vui lòng cung cấp đầy đủ email và OTP."));
        }

        ResponseEntity<?> serviceResponse = authenticationService.validateOtp(requestDTO.getEmail(), requestDTO.getOtpCode());

        if (serviceResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok(Map.of("success", true, "message", "Mã OTP hợp lệ."));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("success", false, "message", "Mã OTP không hợp lệ hoặc đã hết hạn."));
        }
    }


    // Reset Password - Resets password using OTP
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody OTPRequestDTO requestDTO) {
        if (requestDTO == null) {
            return ResponseEntity.badRequest().body("Email, OTP và mật khẩu mới là bắt buộc (DTO là null).");
        }
        if (requestDTO.getEmail() == null || requestDTO.getEmail().isEmpty() ||
                requestDTO.getOtpCode() == null || requestDTO.getOtpCode().isEmpty() ||
                requestDTO.getNewPassword() == null || requestDTO.getNewPassword().isEmpty()) {
            return ResponseEntity.badRequest().body("Email, OTP và mật khẩu mới là bắt buộc (các trường là null/trống).");
        }
        return authenticationService.resetPassword(requestDTO);
    }
    //login by google
    @PostMapping("/login-by-google")
    public ResponseEntity<?> loginByGoogle(@RequestBody SocialLoginRequestDTO requestDTO) {
        return authenticationService.loginByGoogle(requestDTO);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Định dạng header Authorization không hợp lệ.");
        }
        String token = authHeader.substring(7);
        if (!jwtService.validateToken(token)) {
            return ResponseEntity.status(401).body("Token không hợp lệ hoặc đã hết hạn.");
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().body("Đăng xuất thành công. Vui lòng xóa token ở phía client.");
    }

}
