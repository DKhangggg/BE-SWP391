package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.dto.request.LoginRequest;
import com.example.gender_healthcare_service.dto.request.RegisterRequest;
import com.example.gender_healthcare_service.dto.request.SocialLoginRequestDTO;
import com.example.gender_healthcare_service.dto.response.AuthResponseDTO;
import com.example.gender_healthcare_service.dto.response.UserResponseDTO;
import com.example.gender_healthcare_service.entity.enumpackage.Role;
import com.example.gender_healthcare_service.entity.User;
import com.example.gender_healthcare_service.repository.UserRepository;
import com.example.gender_healthcare_service.service.AuthenticationService;
import com.example.gender_healthcare_service.service.EmailService;
import com.example.gender_healthcare_service.service.JwtService;
import com.example.gender_healthcare_service.repository.PasswordResetTokenRepository;
import com.example.gender_healthcare_service.entity.PasswordResetToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Value; // Added import
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements UserDetailsService, AuthenticationService {
    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private  ModelMapper modelMapper;
    @Autowired
    private  PasswordEncoder passwordEncoder;
    @Autowired
    private  AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    public ResponseEntity<?> registerUser(RegisterRequest RegisterUser) {
        if(RegisterUser != null && RegisterUser.getUsername() != null) {
            if (userRepository.findUserByUsername(RegisterUser.getUsername()) != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("User already exists with username: " + RegisterUser.getUsername());
            }
            if(userRepository.findUserByEmail(RegisterUser.getEmail())!= null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Email already exists: " + RegisterUser.getEmail());
            }
            User newUser = new User();
            newUser.setUsername(RegisterUser.getUsername());
            newUser.setEmail(RegisterUser.getEmail());
            newUser.setFullName(RegisterUser.getFullName());
            newUser.setPasswordHash(passwordEncoder.encode(RegisterUser.getPassword()));
            newUser.setRoleName(Role.ROLE_CUSTOMER.name());
            newUser.setIsDeleted(false);
            newUser.setCreatedAt(new Date().toInstant());
            newUser.setUpdatedAt(new Date().toInstant());
            userRepository.save(newUser);
            UserResponseDTO response = modelMapper.map(newUser, UserResponseDTO.class);

            Authentication authentication = authenticationManager.authenticate(
                    new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                            RegisterUser.getUsername(),
                            RegisterUser.getPassword()
                    )
            );
            String jwt = jwtService.generateToken(authentication);
            String refreshToken = jwtService.generateRefreshToken(authentication);
            emailService.welcomeEmail(newUser.getEmail(), newUser.getFullName());
            return ResponseEntity.ok(new AuthResponseDTO(jwt, refreshToken, newUser.getUsername(), newUser.getRoleName()));
        }
        return ResponseEntity.badRequest().body("Invalid registration request");
    }


    public ResponseEntity<?> loginUser(LoginRequest loginRequest) {
        try {
            System.out.println("Attempting login for user: " + loginRequest.getUsername());
            System.out.println("Password provided: " + (loginRequest.getPassword() != null ? "YES (length: " + loginRequest.getPassword().length() + ")" : "NO"));

            User user = userRepository.findUserByUsername(loginRequest.getUsername());

            if (user == null) {
                System.out.println("User not found: " + loginRequest.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
            }

            System.out.println("Found user in DB. Stored password hash: " + user.getPasswordHash());
            System.out.println("User role: " + user.getRoleName());

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            );
            System.out.println("Authentication token created with credentials: " + (authToken.getCredentials() != null));

            Authentication authentication = authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            System.out.println("Authentication successful for user: " + loginRequest.getUsername());

            if(user.getIsDeleted()) {
                throw new RuntimeException("User account is deleted");
            }

            UserResponseDTO userDTO = modelMapper.map(user, UserResponseDTO.class);
            String jwt = jwtService.generateToken(authentication);
            String refreshToken = jwtService.generateRefreshToken(authentication);
            return ResponseEntity.ok(new AuthResponseDTO(jwt, refreshToken));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Login failed. Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRoleName()));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPasswordHash())
                .disabled(user.getIsDeleted() != null && user.getIsDeleted())
                .authorities(authorities)
                .build();
    }
    public void setConsultantUser(Integer Userid){
        User user = userRepository.findUserById(Userid);
        if (user != null) {
            user.setRoleName(Role.ROLE_CONSULTANT.name());
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found with ID: " + Userid);
        }
    }

    public ResponseEntity<?> refreshAccessToken(String refreshTokenString) {
        try {
            if (refreshTokenString == null || refreshTokenString.isEmpty()) {
                return ResponseEntity.badRequest().body("Refresh token is missing");
            }

            jwtService.validateToken(refreshTokenString);

            String username = jwtService.getUserNameFromJWT(refreshTokenString);
            UserDetails userDetails = this.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            String newAccessToken = jwtService.generateToken(authentication);

            return ResponseEntity.ok(new AuthResponseDTO(newAccessToken, refreshTokenString));

        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found for the provided refresh token");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token: " + e.getMessage());
        }
    }

    @Transactional
    public ResponseEntity<?> handleForgotPassword(String email) {
        System.out.println("[LOG] Received forgot password request for email: " + email);
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            System.out.println("[LOG] User not found with email: " + email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with email: " + email);
        }
        System.out.println("[LOG] User found: " + user.getUsername() + " (ID: " + user.getId() + ")");

        System.out.println("[LOG] Deleting existing password reset tokens for user ID: " + user.getId());
        passwordResetTokenRepository.deleteByUser(user);

        String token = UUID.randomUUID().toString();
        System.out.println("[LOG] Generated new password reset token: " + token);
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.save(passwordResetToken);
        System.out.println("[LOG] Saved new password reset token to database. Token ID: " + passwordResetToken.getId() + ", Expiry: " + passwordResetToken.getExpiryDate());

        System.out.println("[LOG] Calling emailService.forgotPasswordEmail for email: " + user.getEmail() + " with token: " + token);
        emailService.forgotPasswordEmail(user.getEmail(), token);
        System.out.println("[LOG] forgotPasswordEmail service call completed.");

        return ResponseEntity.ok().body("If your email address is in our database, you will receive a password reset link shortly.");
    }
    private String generateOTP() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder otp = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(characters.length());
            otp.append(characters.charAt(index));
        }
        return otp.toString();
    }

    @Override
    public UserResponseDTO findUserById(Integer userId) {
        User user = userRepository.findUserById(userId);
        if(user == null) {
            return null;
        }else  {
            UserResponseDTO userResponseDTO = modelMapper.map(user, UserResponseDTO.class);
            return userResponseDTO;
        }
    }

    @Override
    public ResponseEntity<?> loginByGoogle(SocialLoginRequestDTO requestDTO) {

        System.out.println("DEBUG: Attempting Google login. Client ID: " + googleClientId);
        if (requestDTO != null && requestDTO.getIdToken() != null) {
            System.out.println("DEBUG: Received ID Token (first 30 chars): " + requestDTO.getIdToken().substring(0, Math.min(requestDTO.getIdToken().length(), 30)));
        } else {
            System.out.println("DEBUG: SocialLoginRequestDTO or ID Token is null.");
            return ResponseEntity.badRequest().body("ID token is missing.");
        }
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                   new NetHttpTransport(),new GsonFactory())
                    .setAudience(List.of(googleClientId))
                    .build();
            GoogleIdToken idToken = verifier.verify(requestDTO.getIdToken());
            if (idToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid Google ID token");
            }
            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String nameFromGoogle = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");

            String fullNameToSet;
            if (nameFromGoogle != null && !nameFromGoogle.trim().isEmpty()) {
                fullNameToSet = nameFromGoogle;
            } else if (email != null && !email.isEmpty()) {
                fullNameToSet = email.split("@")[0];
            } else {
                fullNameToSet = "New User";
            }

            User user = userRepository.findUserByEmail(email);
            if (user == null) {
                user = new User();
                user.setEmail(email);
                user.setUsername(email);
                user.setFullName(fullNameToSet);
                user.setPasswordHash(passwordEncoder.encode("123456"));
                user.setRoleName(Role.ROLE_CUSTOMER.name());
                user.setIsDeleted(false);
                userRepository.save(user);
            }
            UserDetails userDetails = this.loadUserByUsername(user.getUsername());
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String accessToken = jwtService.generateToken(authentication);
            String refreshToken = jwtService.generateRefreshToken(authentication);
            AuthResponseDTO response = new AuthResponseDTO(
                    accessToken,
                    refreshToken,
                    user.getUsername(),
                    user.getRoleName()
            );

            return ResponseEntity.ok(response);
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing Google login: " + e.getMessage());
        }
    }

    public ResponseEntity<?> handleResetPassword(String token, String newPassword) {
        PasswordResetToken resetTokenEntity = passwordResetTokenRepository.findByToken(token);

        if (resetTokenEntity == null) {
            return ResponseEntity.badRequest().body("Invalid password reset token.");
        }

        if (resetTokenEntity.isExpired()) {
            passwordResetTokenRepository.delete(resetTokenEntity);
            return ResponseEntity.badRequest().body("Password reset token has expired.");
        }

        User user = resetTokenEntity.getUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User associated with token not found.");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        passwordResetTokenRepository.delete(resetTokenEntity);

        emailService.resetPasswordEmail(user.getEmail(), null);

        return ResponseEntity.ok().body("Password has been reset successfully.");
    }
}
