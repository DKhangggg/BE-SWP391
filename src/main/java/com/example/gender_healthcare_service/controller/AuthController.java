package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.dto.request.*;
import com.example.gender_healthcare_service.dto.response.AuthResponseDTO;
import com.example.gender_healthcare_service.service.AuthenticationService;
import com.example.gender_healthcare_service.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;



@RequestMapping("/api/auth")
@RestController
public class AuthController {
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private JwtService jwtService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody(required = false) LoginRequest loginRequestBody) {
        System.out.println("------------------------");
        System.out.println("DEBUG - Login request received");
        System.out.println("Request body is null: " + (loginRequestBody == null));

        if (loginRequestBody != null) {
            System.out.println("Username: " + loginRequestBody.getUsername());
            System.out.println("Password field is null: " + (loginRequestBody.getPassword() == null));
            if (loginRequestBody.getPassword() != null) {
                System.out.println("Password length: " + loginRequestBody.getPassword().length());
                System.out.println("Password: " + loginRequestBody.getPassword());
            }

            System.out.println("------------------------");

            return authenticationService.loginUser(loginRequestBody);
        } else {
            System.out.println("WARNING: LoginRequest object is null!");
            System.out.println("------------------------");
            System.out.println("Returning 400 Bad Request - loginRequest is null");
            return ResponseEntity.badRequest().body("Request body is missing or empty");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterRequest register) {
        ResponseEntity<?> serviceResponse = authenticationService.registerUser(register);
        if (serviceResponse.getStatusCode().is2xxSuccessful()) {
            AuthResponseDTO authResponseDTO = (AuthResponseDTO) serviceResponse.getBody();
            return ResponseEntity.ok(authResponseDTO);
        } else {
            return ResponseEntity.status(serviceResponse.getStatusCode()).body(null);
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequestDTO requestDTO) {
        if (requestDTO == null || requestDTO.getRefreshToken() == null || requestDTO.getRefreshToken().isEmpty()) {
            return ResponseEntity.badRequest().body("Refresh token is required.");
        }
        return authenticationService.refreshAccessToken(requestDTO.getRefreshToken());
    }
    // Initiate Password Reset - Generates and sends OTP
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequestDTO requestDTO) {
        System.out.println("[PASSWORD_RESET_CONTROLLER_LOG] Received request for /forgot-password");
        if (requestDTO == null) {
            return ResponseEntity.badRequest().body("Email is required (DTO is null).");
        }
        if (requestDTO.getEmail() == null || requestDTO.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required (email field is null/empty).");
        }
        System.out.println("[PASSWORD_RESET_CONTROLLER_LOG] Email from DTO: " + requestDTO.getEmail());
        return authenticationService.sendResetPasswordEmail(requestDTO.getEmail());
    }

    // Validate OTP - Checks if OTP is valid without changing password
    @PostMapping("/validate-otp")
    public ResponseEntity<?> validateOtp(@RequestBody ValidateOtpRequestDTO requestDTO) {
        System.out.println("[PASSWORD_RESET_CONTROLLER_LOG] Received request for /validate-otp");
        if (requestDTO == null) {
            return ResponseEntity.badRequest().body("Email and OTP are required (DTO is null).");
        }
        if (requestDTO.getEmail() == null || requestDTO.getEmail().isEmpty() ||
                requestDTO.getOtpCode() == null || requestDTO.getOtpCode().isEmpty()) {
            return ResponseEntity.badRequest().body("Email and OTP are required (fields are null/empty).");
        }
        System.out.println("[PASSWORD_RESET_CONTROLLER_LOG] Email: " + requestDTO.getEmail() + ", OTP: " + requestDTO.getOtpCode());
        return authenticationService.validateOtp(requestDTO.getEmail(), requestDTO.getOtpCode());
    }
    // Reset Password - Resets password using OTP
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody OTPRequestDTO requestDTO) {
        System.out.println("[PASSWORD_RESET_CONTROLLER_LOG] Received request for /reset-password");
        if (requestDTO == null) {
            return ResponseEntity.badRequest().body("Email, OTP, and new password are required (DTO is null).");
        }
        if (requestDTO.getEmail() == null || requestDTO.getEmail().isEmpty() ||
                requestDTO.getOtpCode() == null || requestDTO.getOtpCode().isEmpty() ||
                requestDTO.getNewPassword() == null || requestDTO.getNewPassword().isEmpty()) {
            return ResponseEntity.badRequest().body("Email, OTP, and new password are required (fields are null/empty).");
        }
        System.out.println("[PASSWORD_RESET_CONTROLLER_LOG] Email: " + requestDTO.getEmail());
        return authenticationService.resetPassword(requestDTO);
    }
    //login by google
    @PostMapping("/login-by-google")
    public ResponseEntity<?> loginByGoogle(@RequestBody SocialLoginRequestDTO requestDTO) {
        System.out.println("DEBUG: AuthController.loginByGoogle called.");
        if (requestDTO != null && requestDTO.getIdToken() != null) {
            System.out.println("DEBUG: AuthController received ID Token (first 30 chars): " + requestDTO.getIdToken().substring(0, Math.min(requestDTO.getIdToken().length(), 30)));
        } else {
            System.out.println("DEBUG: AuthController: SocialLoginRequestDTO or ID Token is null.");
        }
        return authenticationService.loginByGoogle(requestDTO);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Invalid authorization header format.");
        }
        String token = authHeader.substring(7);
        if (!jwtService.validateToken(token)) {
            return ResponseEntity.status(401).body("Invalid or expired token.");
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().body("Logout successful. Please clear your tokens client-side.");
    }

}
