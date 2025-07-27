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


    public ResponseEntity<?>loginUser(LoginRequest loginRequest) {
        try {
            User user = userRepository.findUserByUsername(loginRequest.getUsername());

            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Tên đăng nhập hoặc mật khẩu không đúng");
            }

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            );

            Authentication authentication = authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            if(user.getIsDeleted()) {
                throw new RuntimeException("Tài khoản người dùng đã bị xóa");
            }

            UserResponseDTO userDTO = modelMapper.map(user, UserResponseDTO.class);
            String jwt = jwtService.generateToken(authentication);
            String refreshToken = jwtService.generateRefreshToken(authentication);
            return ResponseEntity.ok(new AuthResponseDTO(jwt, refreshToken,user.getFullName(), user.getRoleName(),user.getEmail()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Tên đăng nhập hoặc mật khẩu không đúng");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Không tìm thấy người dùng với tên đăng nhập: " + username);
        }
        String role = user.getRoleName();
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }

        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                authorities
        );
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
            throw new RuntimeException("Không tìm thấy người dùng với ID: " + Userid);
        }
    }

    public ResponseEntity<?> refreshAccessToken(String refreshTokenString) {
        try {
            if (refreshTokenString == null || refreshTokenString.isEmpty()) {
                return ResponseEntity.badRequest().body("Refresh token bị thiếu");
            }

            jwtService.validateToken(refreshTokenString);

            String username = jwtService.getUserNameFromJWT(refreshTokenString);
            UserDetails userDetails = this.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            String newAccessToken = jwtService.generateToken(authentication);

            return ResponseEntity.ok(new AuthResponseDTO(newAccessToken, refreshTokenString));

        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Không tìm thấy người dùng cho refresh token được cung cấp");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token không hợp lệ hoặc đã hết hạn: " + e.getMessage());
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
            return ResponseEntity.badRequest().body("Mã xác thực bị thiếu.");
        }

        try {
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
                        .body("Token Google ID không hợp lệ");
            }
            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String nameFromGoogle = (String) payload.get("name");
            String fullNameToSet;
            if (nameFromGoogle != null && !nameFromGoogle.trim().isEmpty()) {
                fullNameToSet = nameFromGoogle;
            } else if (email != null && !email.isEmpty()) {
                fullNameToSet = email.split("@")[0];
            } else {
                fullNameToSet = "Người dùng mới";
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi xử lý đăng nhập Google: " + e.getMessage());
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
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người dùng với email: " + email);
        }

        passwordResetOTPRepository.deleteByUser(user);

        String otpCode = generateOTP();
        PasswordResetOTP passwordResetOTP = new PasswordResetOTP(otpCode, user);
        passwordResetOTPRepository.save(passwordResetOTP);
        emailService.sendOTPEmail(user.getEmail(), user.getFullName(), otpCode, otpVerificationLink);

        return ResponseEntity.ok().body("Nếu địa chỉ email của bạn có trong cơ sở dữ liệu, bạn sẽ nhận được mã OTP trong thời gian ngắn.");
    }

    @Transactional
    public ResponseEntity<?> validateOtp(String email, String otp) {

        PasswordResetOTP resetOTP = passwordResetOTPRepository.findByOtpCodeAndUser_Email(otp, email);

        if (resetOTP == null) {
            return ResponseEntity.badRequest().body("Mã OTP không hợp lệ.");
        }

        if (resetOTP.isExpired()) {
            passwordResetOTPRepository.delete(resetOTP);
            return ResponseEntity.badRequest().body("Mã OTP đã hết hạn.");
        }

        return ResponseEntity.ok().body("Mã OTP hợp lệ.");
    }

    @Transactional
    public ResponseEntity<?> resetPassword(OTPRequestDTO otpRequest) {

        PasswordResetOTP resetOTP = passwordResetOTPRepository.findByOtpCodeAndUser_Email(
                otpRequest.getOtpCode(), otpRequest.getEmail());

        if (resetOTP == null) {
            return ResponseEntity.badRequest().body("Mã OTP không hợp lệ.");
        }

        if (resetOTP.isExpired()) {
            passwordResetOTPRepository.delete(resetOTP);
            return ResponseEntity.badRequest().body("Mã OTP đã hết hạn.");
        }

        User user = resetOTP.getUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Không tìm thấy người dùng liên kết với OTP.");
        }

        user.setPasswordHash(passwordEncoder.encode(otpRequest.getNewPassword()));
        userRepository.save(user);

        passwordResetOTPRepository.delete(resetOTP);

        emailService.resetPasswordEmail(user.getEmail(), null);

        return ResponseEntity.ok().body("Mật khẩu đã được đặt lại thành công.");
    }
}
