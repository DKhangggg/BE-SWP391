package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.dto.request.AdminUpdateUserRequestDTO;
import com.example.gender_healthcare_service.dto.request.UserProfileRequest;
import com.example.gender_healthcare_service.dto.response.UserResponseDTO;
import com.example.gender_healthcare_service.dto.response.UserProfileTrendResponseDTO;
import com.example.gender_healthcare_service.exception.ServiceNotFoundException;
import com.example.gender_healthcare_service.repository.UserRepository;
import com.example.gender_healthcare_service.repository.BookingRepository;
import com.example.gender_healthcare_service.repository.ConsultationRepository;
import com.example.gender_healthcare_service.repository.QuestionRepository;
import com.example.gender_healthcare_service.repository.PaymentRepository;
import com.example.gender_healthcare_service.entity.User;
import com.example.gender_healthcare_service.entity.Booking;
import com.example.gender_healthcare_service.entity.Consultation;
import com.example.gender_healthcare_service.entity.Question;
import com.example.gender_healthcare_service.entity.Payment;
import com.example.gender_healthcare_service.service.UserService;
import com.example.gender_healthcare_service.service.CloudinaryService;
import com.example.gender_healthcare_service.service.MenstrualCycleService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
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
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ConsultationRepository consultationRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private ModelMapper modelMapper;
    
    @Autowired
    private CloudinaryService cloudinaryService;
    
    @Autowired
    private MenstrualCycleService menstrualCycleService;

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
               throw new RuntimeException("Không tìm thấy người dùng");
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
                throw new RuntimeException("Không tìm thấy người dùng");
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
            throw new RuntimeException("Không thể cập nhật thông tin người dùng: " + e.getMessage());
        }
        return null;
    }

    @Override
    public UserResponseDTO updateUserByAdmin(Integer userId, AdminUpdateUserRequestDTO updateUserDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServiceNotFoundException("Không tìm thấy người dùng với ID: " + userId));

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
                throw new RuntimeException("Định dạng ngày sinh không hợp lệ. Vui lòng sử dụng YYYY-MM-DD.", e);
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

    @Override
    public List<UserResponseDTO> getCustomers() {
        List<User> customers = userRepository.findUserByRoleName("ROLE_CUSTOMER");
        return customers.stream()
                .map(user -> {
                    UserResponseDTO dto = modelMapper.map(user, UserResponseDTO.class);
                    // Validate và cập nhật avatar URL
                    if (user.getAvatarPublicId() != null && !user.getAvatarPublicId().trim().isEmpty()) {
                        String validatedUrl = cloudinaryService.getImageUrlWithFallback(
                            user.getAvatarPublicId(), 
                            getDefaultAvatarUrl()
                        );
                        dto.setAvatarUrl(validatedUrl);
                    } else {
                        dto.setAvatarUrl(getDefaultAvatarUrl());
                    }
                    return dto;
                })
                .toList();
    }

    @Override
    public User getCurrentUser() {
        return userRepository.findUserByUsername(
            SecurityContextHolder.getContext().getAuthentication().getName()
        );
    }
    
    @Override
    public UserProfileTrendResponseDTO getUserProfileWithTrends() {
        try {
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                throw new RuntimeException("Không tìm thấy người dùng");
            }
            
            UserResponseDTO userInfo = getInfo();
            
            // Tính toán profile completion rate
            int profileCompletionRate = calculateProfileCompletionRate(currentUser);
            
            // Lấy dữ liệu booking
            List<Booking> userBookings = bookingRepository.findByCustomerIDAndIsDeletedFalse(currentUser);
            int totalBookings = userBookings.size();
            int completedBookings = (int) userBookings.stream()
                .filter(b -> "COMPLETED".equals(b.getStatus()))
                .count();
            int pendingBookings = (int) userBookings.stream()
                .filter(b -> "PENDING".equals(b.getStatus()) || "CONFIRMED".equals(b.getStatus()))
                .count();
            int cancelledBookings = (int) userBookings.stream()
                .filter(b -> "CANCELLED".equals(b.getStatus()))
                .count();
            
            // Lấy dữ liệu consultation
            List<Consultation> userConsultations = consultationRepository.findConsultationsByCustomer(currentUser);
            int totalConsultations = userConsultations.size();
            int completedConsultations = (int) userConsultations.stream()
                .filter(c -> "COMPLETED".equals(c.getStatus().name()))
                .count();
            
            // Lấy dữ liệu questions
            List<Question> userQuestions = questionRepository.findQuestionsByUser(currentUser, org.springframework.data.domain.Pageable.unpaged()).getContent();
            int totalQuestions = userQuestions.size();
            int answeredQuestions = (int) userQuestions.stream()
                .filter(q -> "ANSWERED".equals(q.getStatus().name()))
                .count();
            
            // Lấy dữ liệu payments
            List<Payment> userPayments = paymentRepository.findByCustomer(currentUser);
            int totalPayments = userPayments.size();
            double totalSpent = userPayments.stream()
                .filter(p -> "COMPLETED".equals(p.getPaymentStatus()))
                .mapToDouble(p -> p.getAmount() != null ? p.getAmount().doubleValue() : 0.0)
                .sum();
            
            // Tính average rating (giả định từ feedback hoặc booking)
            double averageRating = calculateAverageRating(userBookings, userConsultations);
            
            // Tạo monthly trends (6 tháng gần nhất)
            List<UserProfileTrendResponseDTO.MonthlyTrend> monthlyTrends = generateMonthlyTrends(userBookings, userConsultations, userPayments);
            
            // Tạo top services
            List<UserProfileTrendResponseDTO.ServiceUsage> topServices = generateTopServices(userBookings);
            
            // Tạo recent activities
            List<UserProfileTrendResponseDTO.RecentActivity> recentActivities = generateRecentActivities(userBookings, userConsultations, userQuestions, userPayments);
            
            // Tạo health insights
            UserProfileTrendResponseDTO.HealthInsights healthInsights = generateHealthInsights(currentUser);
            
            UserProfileTrendResponseDTO.ProfileTrendData trendData = UserProfileTrendResponseDTO.ProfileTrendData.builder()
                .profileCompletionRate(profileCompletionRate)
                .totalBookings(totalBookings)
                .completedBookings(completedBookings)
                .pendingBookings(pendingBookings)
                .cancelledBookings(cancelledBookings)
                .averageRating(averageRating)
                .totalConsultations(totalConsultations)
                .completedConsultations(completedConsultations)
                .totalQuestions(totalQuestions)
                .answeredQuestions(answeredQuestions)
                .totalPayments(totalPayments)
                .totalSpent(totalSpent)
                .monthlyTrends(monthlyTrends)
                .topServices(topServices)
                .recentActivities(recentActivities)
                .healthInsights(healthInsights)
                .build();
            
            return UserProfileTrendResponseDTO.builder()
                .userInfo(userInfo)
                .trendData(trendData)
                .build();
                
        } catch (Exception e) {
            log.error("Error getting user profile with trends: {}", e.getMessage());
            throw new RuntimeException("Lỗi khi lấy thông tin hồ sơ với trends: " + e.getMessage());
        }
    }
    
    private int calculateProfileCompletionRate(User user) {
        int totalFields = 6;
        int filledFields = 0;
        
        if (user.getFullName() != null && !user.getFullName().trim().isEmpty()) filledFields++;
        if (user.getPhoneNumber() != null && !user.getPhoneNumber().trim().isEmpty()) filledFields++;
        if (user.getDateOfBirth() != null) filledFields++;
        if (user.getAddress() != null && !user.getAddress().trim().isEmpty()) filledFields++;
        if (user.getGender() != null && !user.getGender().trim().isEmpty()) filledFields++;
        if (user.getDescription() != null && !user.getDescription().trim().isEmpty()) filledFields++;
        
        return Math.round((float) filledFields / totalFields * 100);
    }
    
    private double calculateAverageRating(List<Booking> bookings, List<Consultation> consultations) {
        // Giả định rating từ 1-5, mặc định 4.5 nếu chưa có đánh giá
        if (bookings.isEmpty() && consultations.isEmpty()) {
            return 4.5;
        }
        
        // Tính trung bình từ các booking và consultation đã hoàn thành
        double totalRating = 0;
        int ratingCount = 0;
        
        // Có thể mở rộng để lấy rating thực từ feedback table
        for (Booking booking : bookings) {
            if ("COMPLETED".equals(booking.getStatus())) {
                totalRating += 4.5; // Giả định rating
                ratingCount++;
            }
        }
        
        for (Consultation consultation : consultations) {
            if ("COMPLETED".equals(consultation.getStatus())) {
                totalRating += 4.5; // Giả định rating
                ratingCount++;
            }
        }
        
        return ratingCount > 0 ? totalRating / ratingCount : 4.5;
    }
    
    private List<UserProfileTrendResponseDTO.MonthlyTrend> generateMonthlyTrends(
            List<Booking> bookings, List<Consultation> consultations, List<Payment> payments) {
        List<UserProfileTrendResponseDTO.MonthlyTrend> trends = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
        
        // Tạo 6 tháng gần nhất
        for (int i = 5; i >= 0; i--) {
            LocalDate monthStart = LocalDate.now().minusMonths(i).withDayOfMonth(1);
            LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);
            
            int monthBookings = (int) bookings.stream()
                .filter(b -> b.getCreatedAt() != null && 
                    b.getCreatedAt().toLocalDate().isAfter(monthStart.minusDays(1)) &&
                    b.getCreatedAt().toLocalDate().isBefore(monthEnd.plusDays(1)))
                .count();
                
            int monthConsultations = (int) consultations.stream()
                .filter(c -> c.getCreatedAt() != null && 
                    c.getCreatedAt().toLocalDate().isAfter(monthStart.minusDays(1)) &&
                    c.getCreatedAt().toLocalDate().isBefore(monthEnd.plusDays(1)))
                .count();
                
            double monthSpending = payments.stream()
                .filter(p -> p.getCreatedAt() != null && 
                    p.getCreatedAt().toLocalDate().isAfter(monthStart.minusDays(1)) &&
                    p.getCreatedAt().toLocalDate().isBefore(monthEnd.plusDays(1)) &&
                    "COMPLETED".equals(p.getPaymentStatus()))
                .mapToDouble(p -> p.getAmount() != null ? p.getAmount().doubleValue() : 0.0)
                .sum();
            
            trends.add(UserProfileTrendResponseDTO.MonthlyTrend.builder()
                .month(monthStart.format(formatter))
                .bookings(monthBookings)
                .consultations(monthConsultations)
                .spending(monthSpending)
                .rating(4.5) // Giả định rating
                .build());
        }
        
        return trends;
    }
    
    private List<UserProfileTrendResponseDTO.ServiceUsage> generateTopServices(List<Booking> bookings) {
        Map<String, UserProfileTrendResponseDTO.ServiceUsage> serviceMap = new HashMap<>();
        
        for (Booking booking : bookings) {
            if (booking.getService() != null) {
                String serviceName = booking.getService().getServiceName();
                serviceMap.computeIfAbsent(serviceName, k -> UserProfileTrendResponseDTO.ServiceUsage.builder()
                    .serviceName(serviceName)
                    .usageCount(0)
                    .totalSpent(0.0)
                    .averageRating(4.5)
                    .build());
                
                UserProfileTrendResponseDTO.ServiceUsage usage = serviceMap.get(serviceName);
                usage.setUsageCount(usage.getUsageCount() + 1);
                if (booking.getService().getPrice() != null) {
                    usage.setTotalSpent(usage.getTotalSpent() + booking.getService().getPrice().doubleValue());
                }
            }
        }
        
        return serviceMap.values().stream()
            .sorted((a, b) -> Integer.compare(b.getUsageCount(), a.getUsageCount()))
            .limit(5)
            .collect(Collectors.toList());
    }
    
    private List<UserProfileTrendResponseDTO.RecentActivity> generateRecentActivities(
            List<Booking> bookings, List<Consultation> consultations, 
            List<Question> questions, List<Payment> payments) {
        List<UserProfileTrendResponseDTO.RecentActivity> activities = new ArrayList<>();
        
        // Thêm booking activities
        bookings.stream()
            .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
            .limit(3)
            .forEach(booking -> {
                activities.add(UserProfileTrendResponseDTO.RecentActivity.builder()
                    .type("BOOKING")
                    .title("Đặt lịch xét nghiệm")
                    .description(booking.getService() != null ? booking.getService().getServiceName() : "Dịch vụ xét nghiệm")
                    .date(booking.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                    .status(booking.getStatus())
                    .icon("test-tube")
                    .build());
            });
        
                    // Thêm consultation activities
            consultations.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(3)
                .forEach(consultation -> {
                    activities.add(UserProfileTrendResponseDTO.RecentActivity.builder()
                        .type("CONSULTATION")
                        .title("Đặt lịch tư vấn")
                        .description("Tư vấn với chuyên gia")
                        .date(consultation.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                        .status(consultation.getStatus().name())
                        .icon("user-md")
                        .build());
                });
            
            // Thêm question activities
            questions.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(2)
                .forEach(question -> {
                    activities.add(UserProfileTrendResponseDTO.RecentActivity.builder()
                        .type("QUESTION")
                        .title("Đặt câu hỏi")
                        .description(question.getContent())
                        .date(question.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                        .status(question.getStatus().name())
                        .icon("question-circle")
                        .build());
                });
        
        // Sắp xếp theo thời gian và lấy 5 hoạt động gần nhất
        return activities.stream()
            .sorted((a, b) -> {
                try {
                    LocalDateTime dateA = LocalDateTime.parse(a.getDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                    LocalDateTime dateB = LocalDateTime.parse(b.getDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                    return dateB.compareTo(dateA);
                } catch (Exception e) {
                    return 0;
                }
            })
            .limit(5)
            .collect(Collectors.toList());
    }
    
    private UserProfileTrendResponseDTO.HealthInsights generateHealthInsights(User user) {
        // Giả định dữ liệu health insights
        return UserProfileTrendResponseDTO.HealthInsights.builder()
            .cycleStatus("REGULAR")
            .averageCycleLength(28)
            .nextPeriodPrediction("15/01/2025")
            .fertilityWindow("10/01/2025 - 14/01/2025")
            .commonSymptoms(List.of("Chuột rút", "Đau lưng", "Mệt mỏi"))
            .healthRecommendation("Duy trì chế độ ăn uống lành mạnh và tập thể dục đều đặn")
            .wellnessScore(85)
            .build();
    }
}
