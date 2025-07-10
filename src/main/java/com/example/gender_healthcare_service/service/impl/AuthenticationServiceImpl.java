package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.dto.request.LoginRequest;
import com.example.gender_healthcare_service.dto.request.OTPRequestDTO;
import com.example.gender_healthcare_service.dto.request.RegisterRequest;
import com.example.gender_healthcare_service.dto.request.SocialLoginRequestDTO;
import com.example.gender_healthcare_service.dto.response.AuthResponseDTO;
import com.example.gender_healthcare_service.dto.response.UserResponseDTO;
import com.example.gender_healthcare_service.entity.PasswordResetOTP;
import com.example.gender_healthcare_service.entity.Consultant;
import com.example.gender_healthcare_service.entity.enumpackage.Role;
import com.example.gender_healthcare_service.entity.User;
import com.example.gender_healthcare_service.repository.ConsultantRepository;
import com.example.gender_healthcare_service.repository.PasswordResetOTPRepository;
import com.example.gender_healthcare_service.repository.UserRepository;
import com.example.gender_healthcare_service.service.AuthenticationService;
import com.example.gender_healthcare_service.service.EmailService;
import com.example.gender_healthcare_service.service.JwtService;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

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
    private PasswordResetOTPRepository passwordResetOTPRepository;
    @Autowired
    private ConsultantRepository consultantRepository;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;

    public ResponseEntity<?> registerUser(RegisterRequest RegisterUser) {
        if(RegisterUser != null && RegisterUser.getUsername() != null) {
            if (userRepository.findUserByUsername(RegisterUser.getUsername()) != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Tên người dùng đã tồn tại: " + RegisterUser.getUsername());
            }
            if(userRepository.findUserByEmail(RegisterUser.getEmail())!= null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Email đã tồn tại: " + RegisterUser.getEmail());
            }
            User newUser = new User();
            newUser.setUsername(RegisterUser.getUsername());
            newUser.setEmail(RegisterUser.getEmail());
            newUser.setFullName(RegisterUser.getFullName());
            newUser.setPasswordHash(passwordEncoder.encode(RegisterUser.getPassword()));
            newUser.setRoleName(Role.ROLE_CUSTOMER.name());
            newUser.setIsDeleted(false);
            newUser.setCreatedAt(LocalDateTime.now());
            newUser.setUpdatedAt(LocalDateTime.now());
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
            return ResponseEntity.ok(new AuthResponseDTO(jwt, refreshToken, newUser.getUsername(), newUser.getRoleName(),newUser.getEmail()));
        }
        return ResponseEntity.badRequest().body("Yêu cầu đăng ký không hợp lệ");
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
            return ResponseEntity.ok(new AuthResponseDTO(jwt, refreshToken,user.getFullName(), user.getRoleName(),user.getEmail()));
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


    @Transactional
    public void setConsultantUser(Integer Userid){
        User user = userRepository.findUserById(Userid);
        if (user != null) {
            user.setRoleName(Role.ROLE_CONSULTANT.name());
            userRepository.save(user);

            Consultant existingConsultant = consultantRepository.findByUserId(Userid);
            if (existingConsultant == null) {
                Consultant consultant = new Consultant();
                user.setUpdatedAt(LocalDateTime.now());
                consultant.setUser(user);
                consultant.setIsDeleted(false);
                consultantRepository.save(consultant);
            }
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

    private LocalDateTime generateOTPExpiry() {
        return LocalDateTime.now().plusMinutes(10);
    }

    public void setPasswordResetOTPExpiry(PasswordResetOTP otp) {
        otp.setExpiryDate(generateOTPExpiry());
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

        if (requestDTO == null || requestDTO.getCode() == null || requestDTO.getCode().isEmpty()) {
            return ResponseEntity.badRequest().body("Authorization code is missing.");
        }

        try {
            // Exchange authorization code for an ID token
            GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance(),
                    googleClientId,
                    googleClientSecret,
                    requestDTO.getCode(),
                    googleRedirectUri)
                    .execute();

            String idTokenString = tokenResponse.getIdToken();

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                   new NetHttpTransport(), GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();
            GoogleIdToken idToken = verifier.verify(idTokenString);

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
                    user.getRoleName(),
                    user.getEmail()
            );

            return ResponseEntity.ok(response);
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing Google login: " + e.getMessage());
        }
    }
    @Override
    public boolean isUserExists(Integer userId) {
        User user = userRepository.findUserById(userId);
        if (user != null) {
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public ResponseEntity<?> sendResetPasswordEmail(String email, String otpVerificationLink) {
        System.out.println("[LOG] Received password reset request for email: " + email);
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            System.out.println("[LOG] User not found with email: " + email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with email: " + email);
        }
        System.out.println("[LOG] User found: " + user.getUsername() + " (ID: " + user.getId() + ")");

        System.out.println("[LOG] Deleting existing OTP codes for user ID: " + user.getId());
        passwordResetOTPRepository.deleteByUser(user);

        String otpCode = generateOTP();
        System.out.println("[LOG] Generated new OTP code: " + otpCode);
        PasswordResetOTP passwordResetOTP = new PasswordResetOTP(otpCode, user);
        passwordResetOTPRepository.save(passwordResetOTP);
        System.out.println("[LOG] Saved new OTP code to database. OTP ID: " + passwordResetOTP.getId() + ", Expiry: " + passwordResetOTP.getExpiryDate());
        System.out.println("[LOG] Calling emailService.sendOTPEmail for email: " + user.getEmail() + " with OTP: " + otpCode);
        emailService.sendOTPEmail(user.getEmail(), user.getFullName(), otpCode, otpVerificationLink);
        System.out.println("[LOG] sendOTPEmail service call completed.");

        return ResponseEntity.ok().body("If your email address is in our database, you will receive an OTP code shortly.");
    }

    @Transactional
    public ResponseEntity<?> validateOtp(String email, String otp) {
        System.out.println("[LOG] Validating OTP code: " + otp + " for email: " + email);

        PasswordResetOTP resetOTP = passwordResetOTPRepository.findByOtpCodeAndUser_Email(otp, email);

        if (resetOTP == null) {
            System.out.println("[LOG] Invalid OTP code for email: " + email);
            return ResponseEntity.badRequest().body("Mã OTP không hợp lệ.");
        }

        if (resetOTP.isExpired()) {
            System.out.println("[LOG] OTP code expired for email: " + email);
            passwordResetOTPRepository.delete(resetOTP);
            return ResponseEntity.badRequest().body("Mã OTP đã hết hạn.");
        }

        System.out.println("[LOG] OTP code valid for email: " + email);
        return ResponseEntity.ok().body("Mã OTP hợp lệ.");
    }

    @Transactional
    public ResponseEntity<?> resetPassword(OTPRequestDTO otpRequest) {
        System.out.println("[LOG] Resetting password with OTP for email: " + otpRequest.getEmail());

        PasswordResetOTP resetOTP = passwordResetOTPRepository.findByOtpCodeAndUser_Email(
                otpRequest.getOtpCode(), otpRequest.getEmail());

        if (resetOTP == null) {
            System.out.println("[LOG] Invalid OTP code for email: " + otpRequest.getEmail());
            return ResponseEntity.badRequest().body("Invalid OTP code.");
        }

        if (resetOTP.isExpired()) {
            System.out.println("[LOG] OTP code expired for email: " + otpRequest.getEmail());
            passwordResetOTPRepository.delete(resetOTP);
            return ResponseEntity.badRequest().body("OTP code has expired.");
        }

        User user = resetOTP.getUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User associated with OTP not found.");
        }

        user.setPasswordHash(passwordEncoder.encode(otpRequest.getNewPassword()));
        userRepository.save(user);

        passwordResetOTPRepository.delete(resetOTP);

        emailService.resetPasswordEmail(user.getEmail(), null);

        return ResponseEntity.ok().body("Password has been reset successfully.");
    }
}
