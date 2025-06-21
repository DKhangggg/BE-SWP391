package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.dto.request.*;
import com.example.gender_healthcare_service.dto.response.AuthResponseDTO;
import com.example.gender_healthcare_service.service.AuthenticationService;
import com.example.gender_healthcare_service.service.impl.AuthenticationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;



@RequestMapping("/api/auth")
@RestController
public class AuthController {
    @Autowired
    private AuthenticationService authenticationService;


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

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequestDTO requestDTO) {
        System.out.println("[AUTH_CONTROLLER_LOG] Received request for /forgot-password.");
        if (requestDTO == null) {
            System.out.println("[AUTH_CONTROLLER_LOG] ForgotPasswordRequestDTO is null.");
            return ResponseEntity.badRequest().body("Email is required (DTO is null).");
        }
        if (requestDTO.getEmail() == null || requestDTO.getEmail().isEmpty()) {
            System.out.println("[AUTH_CONTROLLER_LOG] Email in ForgotPasswordRequestDTO is null or empty. Email: '" + requestDTO.getEmail() + "'");
            return ResponseEntity.badRequest().body("Email is required (email field is null/empty).");
        }
        System.out.println("[AUTH_CONTROLLER_LOG] Email from DTO: " + requestDTO.getEmail() + ". Calling authenticationService.handleForgotPassword...");
        return authenticationService.handleForgotPassword(requestDTO.getEmail());
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequestDTO requestDTO) {
        if (requestDTO == null || requestDTO.getToken() == null || requestDTO.getToken().isEmpty() ||
            requestDTO.getNewPassword() == null || requestDTO.getNewPassword().isEmpty()) {
            return ResponseEntity.badRequest().body("Token and new password are required.");
        }
        return authenticationService.handleResetPassword(requestDTO.getToken(), requestDTO.getNewPassword());
    }

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
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {

        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().body("Logout successful. Please clear your tokens client-side.");
    }

}
