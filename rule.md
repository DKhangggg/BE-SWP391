# RULE.md - Backend SWP391 Gender Healthcare Service

## 🤖 AI DEVELOPMENT GUIDELINES

### Project Recreation Checklist cho AI

#### 1. **Dependencies & Versions**
```xml
<!-- Core Dependencies -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.5.0</version>
</parent>

<properties>
    <java.version>21</java.version>
</properties>

<!-- Essential Dependencies -->
<dependencies>
    <!-- Spring Boot Starters -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-websocket</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-mail</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-oauth2-client</artifactId>
    </dependency>
    
    <!-- Database -->
    <dependency>
        <groupId>com.microsoft.sqlserver</groupId>
        <artifactId>mssql-jdbc</artifactId>
        <scope>runtime</scope>
    </dependency>
    
    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.12.5</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.12.5</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.12.5</version>
        <scope>runtime</scope>
    </dependency>
    
    <!-- Utilities -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.38</version>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>org.modelmapper</groupId>
        <artifactId>modelmapper</artifactId>
        <version>3.1.1</version>
    </dependency>
    <dependency>
        <groupId>io.github.cdimascio</groupId>
        <artifactId>dotenv-java</artifactId>
        <version>3.0.0</version>
    </dependency>
    
    <!-- Google APIs -->
    <dependency>
        <groupId>com.google.api-client</groupId>
        <artifactId>google-api-client</artifactId>
        <version>2.2.0</version>
    </dependency>
    <dependency>
        <groupId>com.google.oauth-client</groupId>
        <artifactId>google-oauth-client</artifactId>
        <version>1.34.1</version>
    </dependency>
    <dependency>
        <groupId>com.google.http-client</groupId>
        <artifactId>google-http-client-jackson2</artifactId>
        <version>1.34.1</version>
    </dependency>
    
    <!-- Documentation -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.2.0</version>
    </dependency>
</dependencies>
```

#### 2. **Database Schema Requirements**
```sql
-- Core Tables Structure
CREATE TABLE Users (
    UserID INT IDENTITY(1,1) PRIMARY KEY,
    Username NVARCHAR(50) NOT NULL UNIQUE,
    PasswordHash NVARCHAR(68) NOT NULL,
    Email NVARCHAR(100) NOT NULL UNIQUE,
    FullName NVARCHAR(100) NOT NULL,
    PhoneNumber NVARCHAR(20),
    RoleName NVARCHAR(255) NOT NULL,
    Description NVARCHAR(200),
    DateOfBirth DATE,
    Address NVARCHAR(200),
    Gender NVARCHAR(20),
    MedicalHistory NVARCHAR(1000),
    CreatedAt DATETIME2 DEFAULT GETDATE(),
    UpdatedAt DATETIME2 DEFAULT GETDATE(),
    IsDeleted BIT DEFAULT 0
);

CREATE TABLE Consultants (
    ConsultantID INT PRIMARY KEY,
    Biography NVARCHAR(1000),
    Qualifications NVARCHAR(500),
    ExperienceYears INT,
    Specialization NVARCHAR(100),
    IsDeleted BIT DEFAULT 0,
    FOREIGN KEY (ConsultantID) REFERENCES Users(UserID)
);

CREATE TABLE TimeSlots (
    TimeSlotID INT IDENTITY(1,1) PRIMARY KEY,
    SlotDate DATE NOT NULL,
    SlotNumber INT NOT NULL,
    StartTime TIME NOT NULL,
    EndTime TIME NOT NULL,
    Duration INT NOT NULL,
    Description NVARCHAR(100),
    IsActive BIT DEFAULT 1,
    ConsultantID INT,
    Capacity INT DEFAULT 1,
    BookedCount INT DEFAULT 0,
    SlotType NVARCHAR(50) NOT NULL,
    CreatedAt DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (ConsultantID) REFERENCES Consultants(ConsultantID)
);

CREATE TABLE TestingServices (
    ServiceID INT IDENTITY(1,1) PRIMARY KEY,
    ServiceName NVARCHAR(100) NOT NULL,
    Description NVARCHAR(500),
    Price DECIMAL(10,2) NOT NULL,
    DurationMinutes INT,
    CreatedAt DATETIME2 DEFAULT GETDATE(),
    UpdatedAt DATETIME2 DEFAULT GETDATE(),
    IsDeleted BIT DEFAULT 0
);

CREATE TABLE Bookings (
    BookingID INT IDENTITY(1,1) PRIMARY KEY,
    CustomerID INT NOT NULL,
    ServiceID INT NOT NULL,
    TimeSlotID INT NOT NULL,
    Status NVARCHAR(255) NOT NULL,
    Result NVARCHAR(500),
    ResultDate DATETIME2,
    CreatedAt DATETIME2 DEFAULT GETDATE(),
    IsDeleted BIT DEFAULT 0,
    FOREIGN KEY (CustomerID) REFERENCES Users(UserID),
    FOREIGN KEY (ServiceID) REFERENCES TestingServices(ServiceID),
    FOREIGN KEY (TimeSlotID) REFERENCES TimeSlots(TimeSlotID)
);

CREATE TABLE Consultations (
    ConsultationID INT IDENTITY(1,1) PRIMARY KEY,
    CustomerID INT NOT NULL,
    ConsultantID INT NOT NULL,
    TimeSlotID INT NOT NULL,
    Status NVARCHAR(255) NOT NULL,
    MeetingLink NVARCHAR(200),
    Notes NVARCHAR(500),
    CreatedAt DATETIME2 DEFAULT GETDATE(),
    IsDeleted BIT DEFAULT 0,
    FOREIGN KEY (CustomerID) REFERENCES Users(UserID),
    FOREIGN KEY (ConsultantID) REFERENCES Users(UserID),
    FOREIGN KEY (TimeSlotID) REFERENCES TimeSlots(TimeSlotID)
);

-- Additional required tables...
```

#### 3. **Environment Variables Template**
```properties
# Database Configuration
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=HS_New;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=12345

# JWT Configuration
jwt.secret.key=MySuperSecretKeyForJWTGenerationThatIsDefinitelyLongEnoughAndSecure123!
jwt.expiration=86400000
jwt.refresh.expiration=604800000

# Email Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Google OAuth2 Configuration
spring.security.oauth2.client.registration.google.client-id=your-google-client-id
spring.security.oauth2.client.registration.google.client-secret=your-google-client-secret
spring.security.oauth2.client.registration.google.redirect-uri=postmessage

# Server Configuration
server.address=0.0.0.0
server.port=8080

# JPA Configuration
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl

# Logging Configuration
logging.level.org.springframework.security=DEBUG
logging.level.root=warn
spring.main.banner-mode=off

# Swagger Configuration (temporarily disabled)
springdoc.api-docs.enabled=false
springdoc.swagger-ui.enabled=false
```

#### 4. **Required Entity Classes Structure**
```java
// Core Entity Classes (must implement)
1. User.java - implements UserDetails
2. Consultant.java - OneToOne with User
3. TimeSlot.java - Time slot management
4. TestingService.java - Service offerings
5. Booking.java - Appointment bookings
6. Consultation.java - Online consultations
7. MenstrualCycle.java - Cycle tracking
8. MenstrualLog.java - Daily logs
9. Symptom.java - Symptom definitions
10. SymptomLog.java - Symptom tracking
11. BlogPost.java - Content management
12. BlogCategory.java - Content categories
13. Question.java - QA system
14. Answer.java - QA responses
15. Payment.java - Payment tracking
16. Feedback.java - User feedback
17. Notification.java - Real-time notifications
18. Reminder.java - Appointment reminders
19. TransactionHistory.java - Financial tracking
20. ReportLog.java - Analytics reports
21. ConsultantAvailability.java - Schedule templates
22. ConsultantSchedule.java - Daily schedules
23. ConsultantUnavailability.java - Unavailable periods
24. Chat.java - Messaging system
25. PasswordResetOTP.java - Password recovery
26. ForgetForm.java - Password reset forms
27. Email.java - Email templates
```

#### 5. **Required Service Interfaces**
```java
// Service Layer Interfaces (must implement)
1. UserService.java
2. ConsultantService.java
3. BookingService.java
4. ConsultationService.java
5. TestingServiceService.java
6. TimeSlotService.java
7. MenstrualCycleService.java
8. MenstrualCycleAnalyticsService.java
9. BlogService.java
10. BlogCategoryService.java
11. QAService.java
12. PaymentService.java
13. FeedbackService.java
14. NotificationService.java
15. ReminderService.java
16. TransactionHistoryService.java
17. ReportService.java
18. ConsultantAvailabilityService.java
19. ConsultantScheduleService.java
20. AuthenticationService.java
21. JwtService.java
22. EmailService.java
23. BookingTrackingService.java
24. AuthorizationService.java
```

#### 6. **Required Controller Classes**
```java
// Controller Classes (must implement)
1. AuthController.java - Authentication endpoints
2. UserController.java - User management
3. AdminController.java - Admin functions
4. ConsultantController.java - Consultant functions
5. BookingController.java - Booking management
6. ConsultationController.java - Consultation management
7. ServiceController.java - Service management
8. TimeSlotController.java - Time slot management
9. BlogController.java - Content management
10. QAController.java - QA system
11. PaymentController.java - Payment processing
12. FeedbackController.java - Feedback system
13. HomepageController.java - Public content
14. EnhancedMenstrualCycleController.java - Cycle tracking
15. BookingTrackingController.java - Real-time tracking
```

#### 7. **Required Configuration Classes**
```java
// Configuration Classes (must implement)
1. SecurityConfig.java - Spring Security configuration
2. CorsConfig.java - CORS configuration
3. WebSocketConfig.java - WebSocket configuration
4. JwtAuthEntryPoint.java - JWT authentication entry point
5. ModelMapperConfig.java - Model mapper configuration
```

#### 8. **Required DTO Classes**
```java
// Request DTOs (must implement)
1. LoginRequest.java
2. RegisterRequest.java
3. UserProfileRequest.java
4. BookingRequestDTO.java
5. ConsultationBookingRequestDTO.java
6. MenstrualCycleRequestDTO.java
7. MenstrualLogRequestDTO.java
8. EnhancedMenstrualLogRequestDTO.java
9. BlogPostRequestDTO.java
10. QuestionRequestDTO.java
11. AnswerRequestDTO.java
12. ConsultantUpdateDTO.java
13. AdminUpdateUserRequestDTO.java
14. AddUnavailabilityRequestDTO.java
15. ReminderRequestDTO.java
16. FeedbackRequestDTO.java

// Response DTOs (must implement)
1. AuthResponseDTO.java
2. UserResponseDTO.java
3. BookingResponseDTO.java
4. ConsultationBookingResponseDTO.java
5. MenstrualCycleResponseDTO.java
6. BlogPostResponseDTO.java
7. QuestionResponseDTO.java
8. AnswerResponseDTO.java
9. ConsultantDTO.java
10. TestingServiceResponseDTO.java
11. TimeSlotResponseDTO.java
12. FeedbackResponseDTO.java
13. DashboardReportDTO.java
14. PageResponse.java
15. ApiResponse.java
```

#### 9. **Required Repository Interfaces**
```java
// Repository Interfaces (must implement)
1. UserRepository.java
2. ConsultantRepository.java
3. BookingRepository.java
4. ConsultationRepository.java
5. TestingServiceRepository.java
6. TimeSlotRepository.java
7. MenstrualCycleRepository.java
8. MenstrualLogRepository.java
9. SymptomRepository.java
10. SymptomLogRepository.java
11. BlogPostRepository.java
12. BlogCategoryRepository.java
13. QuestionRepository.java
14. AnswerRepository.java
15. PaymentRepository.java
16. FeedbackRepository.java
17. NotificationRepository.java
18. ReminderRepository.java
19. TransactionHistoryRepository.java
20. ReportLogRepository.java
21. ConsultantAvailabilityRepository.java
22. ConsultantScheduleRepository.java
23. ConsultantUnavailabilityRepository.java
24. ChatRepository.java
25. PasswordResetOTPRepository.java
26. ForgetFormRepository.java
27. EmailRepository.java
```

#### 10. **Required Exception Classes**
```java
// Exception Classes (must implement)
1. GlobalExceptionHandler.java
2. ServiceNotFoundException.java
3. Custom exceptions for each domain
```

#### 11. **Required Filter Classes**
```java
// Filter Classes (must implement)
1. JwtTokenFilter.java - JWT token validation
```

#### 12. **Required Mapper Classes**
```java
// Mapper Classes (must implement)
1. BlogMapper.java
2. ModelMapperConfig.java
```

#### 13. **Development Steps for AI**
```bash
# Step 1: Setup Project Structure
1. Create Spring Boot project with Java 21
2. Add all required dependencies to pom.xml
3. Setup application.properties with database connection
4. Create package structure

# Step 2: Database Setup
1. Create SQL Server database 'HS_New'
2. Run database schema scripts
3. Insert sample data

# Step 3: Entity Layer
1. Create all entity classes with JPA annotations
2. Implement relationships between entities
3. Add validation annotations
4. Implement UserDetails interface for User entity

# Step 4: Repository Layer
1. Create all repository interfaces
2. Extend JpaRepository
3. Add custom query methods
4. Implement soft delete patterns

# Step 5: Service Layer
1. Create all service interfaces
2. Implement service classes with business logic
3. Add transaction annotations
4. Implement security checks

# Step 6: DTO Layer
1. Create all request DTOs
2. Create all response DTOs
3. Add validation annotations
4. Implement mapping logic

# Step 7: Controller Layer
1. Create all REST controllers
2. Add security annotations
3. Implement pagination
4. Add proper error handling

# Step 8: Security Configuration
1. Configure Spring Security
2. Implement JWT authentication
3. Setup role-based access control
4. Configure CORS

# Step 9: WebSocket Configuration
1. Setup WebSocket for real-time features
2. Configure message brokers
3. Implement notification system

# Step 10: Testing
1. Create unit tests for services
2. Create integration tests for controllers
3. Test security configurations
4. Test database operations

# Step 11: Documentation
1. Add API documentation
2. Create deployment guide
3. Add code comments
4. Create user manual
```

#### 14. **Key Business Rules for AI**
```java
// Authentication Rules
- JWT token expires in 24 hours
- Refresh token expires in 7 days
- Password must be BCrypt encoded
- OAuth2 Google integration required

// Booking Rules
- User cannot book overlapping time slots
- Time slot must be available
- Service must be active
- Booking status flow: PENDING → SAMPLE_COLLECTED → TESTING → COMPLETED

// Consultation Rules
- Consultant must be available
- Max bookings per slot enforced
- Real-time status updates via WebSocket
- Status flow: SCHEDULED → IN_PROGRESS → COMPLETED

// Menstrual Cycle Rules
- Minimum 3 cycles for prediction
- Irregular cycle threshold: 7 days
- Default cycle length: 28 days
- Symptom tracking with severity levels

// Payment Rules
- Amount must be positive
- Payment method validation required
- Transaction ID must be unique
- Status flow: PENDING → PROCESSING → COMPLETED

// Content Management Rules
- Blog posts require author
- Categories can have multiple posts
- Soft delete for all content
- Public/private content filtering

// QA System Rules
- Questions require user authentication
- Answers require consultant role
- Public questions visible to all
- Status flow: PENDING → ANSWERED → CLOSED
```

#### 15. **Performance Requirements**
```java
// Database Performance
- Use indexes on frequently queried columns
- Implement pagination for large datasets
- Use lazy loading for relationships
- Cache frequently accessed data

// API Performance
- Response time < 2 seconds
- Handle concurrent requests
- Implement rate limiting
- Use connection pooling

// Security Performance
- JWT token validation < 100ms
- Password encoding with BCrypt
- Session management optimization
- CORS preflight caching
```

#### 16. **Testing Requirements**
```java
// Unit Tests
- Service layer business logic
- Repository data access
- DTO validation
- Exception handling

// Integration Tests
- Controller endpoints
- Security configurations
- Database operations
- WebSocket connections

// Security Tests
- Authentication flows
- Authorization checks
- JWT token validation
- Role-based access control
```

#### 17. **Deployment Checklist**
```bash
# Pre-deployment
- [ ] All tests passing
- [ ] Code review completed
- [ ] Security scan passed
- [ ] Performance tests passed
- [ ] Documentation updated

# Deployment
- [ ] Database migrations applied
- [ ] Environment variables set
- [ ] SSL certificates configured
- [ ] Monitoring configured
- [ ] Backup strategy in place

# Post-deployment
- [ ] Health checks passing
- [ ] Smoke tests completed
- [ ] Performance monitoring active
- [ ] Error tracking configured
- [ ] User acceptance testing completed
```

---

## 📋 Tổng Quan Hệ Thống

### Thông Tin Cơ Bản
- **Framework**: Spring Boot 3.5.0
- **Java Version**: 21
- **Database**: SQL Server (HS_New)
- **Security**: JWT + Spring Security
- **Architecture**: RESTful API với phân trang chuẩn

### Các Module Chính
1. **Authentication & Authorization** - JWT, OAuth2 Google
2. **User Management** - Quản lý người dùng, roles
3. **Booking System** - Đặt lịch khám, xét nghiệm
4. **Consultation System** - Tư vấn trực tuyến
5. **Menstrual Cycle Tracking** - Theo dõi chu kỳ kinh nguyệt
6. **Blog & Content Management** - Quản lý nội dung
7. **QA System** - Hỏi đáp
8. **Payment System** - Thanh toán
9. **Notification System** - Thông báo real-time
10. **Reporting & Analytics** - Báo cáo và phân tích

---

## 🔐 SECURITY RULES

### JWT Configuration
```properties
jwt.secret.key=MySuperSecretKeyForJWTGenerationThatIsDefinitelyLongEnoughAndSecure123!
jwt.expiration=86400000
jwt.refresh.expiration=604800000
```

### Role-Based Access Control
```java
// Các roles trong hệ thống:
- ROLE_CUSTOMER    // Khách hàng
- ROLE_CONSULTANT  // Tư vấn viên
- ROLE_STAFF       // Nhân viên
- ROLE_MANAGER     // Quản lý
- ROLE_ADMIN       // Admin
```

### API Security Rules
```java
// PUBLIC APIs (không cần authentication)
- POST /api/auth/login
- POST /api/auth/register
- POST /api/auth/forgot-password
- POST /api/auth/reset-password
- POST /api/auth/validate-otp
- POST /api/auth/refresh-token
- GET /api/homepage/**
- GET /api/blog/posts/**
- GET /api/qa/faq
- GET /api/services/testing-services

// CUSTOMER APIs
- POST /api/booking (ROLE_CUSTOMER, ROLE_ADMIN)
- GET /api/user/** (ROLE_CUSTOMER, ROLE_CONSULTANT, ROLE_STAFF, ROLE_MANAGER, ROLE_ADMIN)
- POST /api/qa/questions (ROLE_CUSTOMER, ROLE_ADMIN)
- POST /api/consultation/book (ROLE_CUSTOMER, ROLE_ADMIN)

// CONSULTANT APIs
- /api/consultant/** (ROLE_CONSULTANT, ROLE_MANAGER, ROLE_ADMIN)
- POST /api/qa/questions/*/answers (ROLE_CONSULTANT, ROLE_ADMIN)
- POST /api/blog/posts (ROLE_CONSULTANT, ROLE_STAFF, ROLE_MANAGER, ROLE_ADMIN)

// STAFF APIs
- PATCH /api/booking/*/status (ROLE_STAFF, ROLE_MANAGER, ROLE_ADMIN)
- GET /api/booking/*/admin (ROLE_STAFF, ROLE_MANAGER, ROLE_ADMIN)

// MANAGER APIs
- GET /api/admin/consultants/** (ROLE_MANAGER, ROLE_ADMIN)
- POST /api/blog/categories (ROLE_MANAGER, ROLE_ADMIN)

// ADMIN ONLY APIs
- /api/admin/users/** (ROLE_ADMIN)
- DELETE /api/admin/consultants/* (ROLE_ADMIN)
- PUT /api/admin/setUserToConsultant/* (ROLE_ADMIN)
```

---

## 🗄️ DATABASE RULES

### Entity Relationships
```java
// Core Entities
User (1) ←→ (1) Consultant
User (1) ←→ (N) Booking
User (1) ←→ (N) Consultation
User (1) ←→ (N) MenstrualCycle
User (1) ←→ (N) Question
User (1) ←→ (N) Payment
User (1) ←→ (N) Feedback

// Booking System
TimeSlot (1) ←→ (N) Booking
TestingService (1) ←→ (N) Booking
ConsultantSchedule (1) ←→ (N) Booking

// Consultation System
TimeSlot (1) ←→ (N) Consultation
Consultant (1) ←→ (N) Consultation

// Content Management
BlogPost (N) ←→ (N) BlogCategory
User (1) ←→ (N) BlogPost

// Tracking System
MenstrualCycle (1) ←→ (N) MenstrualLog
MenstrualLog (1) ←→ (N) SymptomLog
Symptom (1) ←→ (N) SymptomLog
```

### Soft Delete Pattern
```java
// Tất cả entities đều có field isDeleted
@ColumnDefault("0")
@Column(name = "IsDeleted")
private Boolean isDeleted = false;

// Repository methods phải filter isDeleted = false
findAllByIsDeletedFalse()
findActiveById()
```

### Audit Fields
```java
// Standard audit fields
@ColumnDefault("getdate()")
@Column(name = "CreatedAt")
private LocalDateTime createdAt;

@ColumnDefault("getdate()")
@Column(name = "UpdatedAt")
private LocalDateTime updatedAt;
```

---

## 📄 API RULES

### Response Format Standards
```json
// Success Response
{
  "content": [...],
  "pageNumber": 1,
  "pageSize": 10,
  "totalElements": 100,
  "totalPages": 10,
  "hasNext": true,
  "hasPrevious": false
}

// Error Response
{
  "error": "Error message",
  "timestamp": "2024-01-15T10:30:00",
  "status": 400
}
```

### Pagination Rules
```java
// Query Parameters
pageNumber (default: 1) - Số trang (bắt đầu từ 1)
pageSize (default: 10) - Số item trên mỗi trang

// APIs có phân trang
- GET /api/admin/users
- GET /api/admin/orders
- GET /api/services/testing-services
- GET /api/blog/posts
- GET /api/qa/user/questions
- GET /api/qa/consultant/questions
```

### HTTP Status Codes
```java
200 OK - Success
201 Created - Resource created successfully
400 Bad Request - Invalid input
401 Unauthorized - Authentication required
403 Forbidden - Insufficient permissions
404 Not Found - Resource not found
500 Internal Server Error - Server error
```

---

## 🏗️ ARCHITECTURE RULES

### Package Structure
```
com.example.gender_healthcare_service/
├── config/          # Configuration classes
├── controller/      # REST controllers
├── dto/            # Data Transfer Objects
│   ├── request/    # Request DTOs
│   └── response/   # Response DTOs
├── entity/         # JPA entities
├── exception/      # Custom exceptions
├── Filter/         # JWT filters
├── mapper/         # Model mappers
├── repository/     # Data access layer
├── service/        # Business logic
│   └── impl/      # Service implementations
└── GenderHealthcareServiceApplication.java
```

### Service Layer Rules
```java
// Service Interface
public interface UserService {
    User findByUserName(String userName);
    List<UserResponseDTO> getAllUsers();
    Page<User> getAllUsers(Pageable pageable);
    UserResponseDTO getInfo();
    UserResponseDTO updateUser(UserProfileRequest user);
}

// Service Implementation
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    
    @Override
    @Transactional
    public UserResponseDTO updateUser(UserProfileRequest user) {
        // Business logic
    }
}
```

### Exception Handling
```java
// Global Exception Handler
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ServiceNotFoundException.class)
    public ResponseEntity<?> handleServiceNotFound(ServiceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Service not found: " + ex.getMessage());
    }
}
```

---

## 🔄 BUSINESS LOGIC RULES

### Booking System Rules
```java
// Booking Status Flow
PENDING → SAMPLE_COLLECTED → TESTING → COMPLETED
PENDING → CANCELLED

// Validation Rules
- User không thể book trùng time slot
- Time slot phải available
- Service phải tồn tại và active
```

### Consultation System Rules
```java
// Consultation Status Flow
SCHEDULED → IN_PROGRESS → COMPLETED
SCHEDULED → CANCELLED
SCHEDULED → NO_SHOW

// Availability Rules
- Consultant phải có availability cho time slot
- Max bookings per slot được kiểm tra
- Real-time status updates
```

### Menstrual Cycle Tracking Rules
```java
// Cycle Prediction
- Minimum 3 cycles để predict
- Irregular cycle threshold: 7 days variance
- Default cycle length: 28 days

// Symptom Tracking
- Multiple symptoms per log
- Severity levels: MILD, MODERATE, SEVERE
- Mood tracking: HAPPY, SAD, ANXIOUS, IRRITATED, NORMAL
```

### Payment System Rules
```java
// Payment Status Flow
PENDING → PROCESSING → COMPLETED
PENDING → FAILED
PENDING → CANCELLED

// Validation
- Amount phải > 0
- Payment method phải valid
- Transaction ID unique
```

---

## 🔧 CONFIGURATION RULES

### Application Properties
```properties
# Database
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=HS_New
spring.datasource.username=sa
spring.datasource.password=12345

# JPA
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true

# Email
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=lekhangmc12@gmail.com

# OAuth2 Google
spring.security.oauth2.client.registration.google.client-id=...
spring.security.oauth2.client.registration.google.client-secret=...

# Server
server.address=0.0.0.0
server.port=8080
```

### CORS Configuration
```java
// Allow all origins for development
configuration.setAllowedOriginPatterns(Arrays.asList("*"));
configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
configuration.setAllowCredentials(true);
```

---

## 📊 DATA VALIDATION RULES

### Entity Validation
```java
// User Entity
@Size(max = 50) @NotNull
private String username;

@Size(max = 100) @NotNull
private String email;

@Size(max = 100)
private String fullName;

// Booking Entity
@NotNull
private User customerID;

@NotNull
private TestingService service;

@NotNull
private TimeSlot timeSlot;
```

### Business Validation
```java
// Booking Validation
- User không được book trùng slot
- Time slot phải available
- Service phải active

// Consultation Validation
- Consultant phải available
- Max bookings không vượt quá limit
- Date phải trong tương lai

// Payment Validation
- Amount > 0
- Payment method valid
- User authenticated
```

---

## 🔔 NOTIFICATION RULES

### Real-time Notifications
```java
// WebSocket Topics
/topic/booking-updates
/topic/consultation-updates
/topic/payment-updates
/user/queue/notifications

// Notification Types
- BOOKING_CREATED
- BOOKING_STATUS_CHANGED
- CONSULTATION_SCHEDULED
- PAYMENT_COMPLETED
- REMINDER_SENT
```

### Email Notifications
```java
// Email Templates
- Welcome email
- Password reset
- Booking confirmation
- Consultation reminder
- Payment receipt
```

---

## 📈 REPORTING RULES

### Dashboard Metrics
```java
// Overview Stats
- Total users
- Total bookings
- Total consultants
- Total revenue
- Active users (30 days)

// Booking Stats
- Completed bookings
- Pending bookings
- Cancelled bookings
- Revenue by date

// Consultant Stats
- Average rating
- Total consultations
- Revenue generated
- Availability rate
```

### Analytics Rules
```java
// Menstrual Cycle Analytics
- Cycle length average
- Irregularity detection
- Symptom patterns
- Fertility window prediction

// Financial Analytics
- Daily revenue
- Service popularity
- Payment method distribution
- Refund rate
```

---

## 🚀 DEPLOYMENT RULES

### Build Configuration
```xml
<!-- Maven Configuration -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.13.0</version>
    <configuration>
        <release>21</release>
    </configuration>
</plugin>
```

### Environment Variables
```bash
# Required Environment Variables
JWT_SECRET_KEY=your-secret-key
DATABASE_URL=jdbc:sqlserver://localhost:1433;databaseName=HS_New
DATABASE_USERNAME=sa
DATABASE_PASSWORD=12345
EMAIL_USERNAME=your-email@gmail.com
EMAIL_PASSWORD=your-app-password
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
```

### Health Checks
```java
// Health Check Endpoints
GET /actuator/health
GET /actuator/info
GET /actuator/metrics

// Custom Health Indicators
- Database connectivity
- Email service availability
- JWT service status
```

---

## 🔍 TESTING RULES

### Unit Testing
```java
// Service Layer Testing
@Test
public void testCreateBooking() {
    // Arrange
    BookingRequestDTO request = new BookingRequestDTO();
    
    // Act
    BookingResponseDTO response = bookingService.createBooking(request);
    
    // Assert
    assertNotNull(response);
    assertEquals("PENDING", response.getStatus());
}
```

### Integration Testing
```java
// API Testing
@Test
public void testBookingAPI() {
    // Given
    BookingRequestDTO request = createValidBookingRequest();
    
    // When
    ResponseEntity<BookingResponseDTO> response = 
        restTemplate.postForEntity("/api/bookings", request, BookingResponseDTO.class);
    
    // Then
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
}
```

---

## 📝 CODING STANDARDS

### Naming Conventions
```java
// Classes
- Controllers: *Controller
- Services: *Service
- Repositories: *Repository
- DTOs: *RequestDTO, *ResponseDTO
- Entities: PascalCase

// Methods
- Controllers: camelCase
- Services: camelCase
- Repositories: camelCase

// Variables
- camelCase
- Descriptive names
- No abbreviations
```

### Code Organization
```java
// Controller Structure
@RestController
@RequestMapping("/api/resource")
public class ResourceController {
    
    private final ResourceService resourceService;
    
    // Constructor injection
    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }
    
    // CRUD operations
    @GetMapping
    @PostMapping
    @PutMapping
    @DeleteMapping
}
```

### Documentation
```java
/**
 * Creates a new booking for the authenticated user
 * 
 * @param bookingRequest The booking request containing service and time slot
 * @return BookingResponseDTO with booking details
 * @throws ServiceNotFoundException if service or time slot not found
 * @throws IllegalStateException if time slot not available
 */
@PostMapping
public ResponseEntity<BookingResponseDTO> createBooking(@RequestBody BookingRequestDTO bookingRequest) {
    // Implementation
}
```

---

## 🔄 VERSION CONTROL RULES

### Git Commit Messages
```
feat: add new booking system
fix: resolve JWT token validation issue
docs: update API documentation
refactor: improve service layer structure
test: add unit tests for booking service
style: format code according to standards
```

### Branch Naming
```
feature/booking-system
bugfix/jwt-validation
hotfix/security-patch
release/v1.2.0
```

---

## 🚨 SECURITY BEST PRACTICES

### Password Security
```java
// BCrypt password encoding
@Bean
public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}

// Password validation
- Minimum 8 characters
- At least one uppercase letter
- At least one lowercase letter
- At least one number
- At least one special character
```

### JWT Security
```java
// Token validation
- Verify signature
- Check expiration
- Validate issuer
- Validate audience

// Token refresh
- Refresh token rotation
- Blacklist old tokens
- Secure token storage
```

### API Security
```java
// Rate limiting
- 100 requests per minute per IP
- 1000 requests per hour per user

// Input validation
- Sanitize all inputs
- Validate file uploads
- Prevent SQL injection
- Prevent XSS attacks
```

---

## 📊 PERFORMANCE RULES

### Database Optimization
```java
// Indexing
- Primary keys automatically indexed
- Foreign keys should be indexed
- Frequently queried columns indexed
- Composite indexes for complex queries

// Query Optimization
- Use pagination for large datasets
- Avoid N+1 queries
- Use lazy loading appropriately
- Cache frequently accessed data
```

### Caching Strategy
```java
// Cache Levels
- Application cache (Redis)
- Database query cache
- Static content cache
- Session cache

// Cache Invalidation
- Time-based expiration
- Event-based invalidation
- Manual cache clearing
```

---

## 🔧 MAINTENANCE RULES

### Logging Standards
```java
// Log Levels
- ERROR: System errors, exceptions
- WARN: Potential issues, deprecated features
- INFO: Important business events
- DEBUG: Detailed debugging information
- TRACE: Very detailed debugging

// Log Format
[timestamp] [level] [class] [method] - message
```

### Monitoring
```java
// Health Checks
- Database connectivity
- External service availability
- Memory usage
- CPU usage
- Response times

// Alerts
- Error rate > 5%
- Response time > 2 seconds
- Memory usage > 80%
- CPU usage > 90%
```

---

## 📋 DEPLOYMENT CHECKLIST

### Pre-Deployment
- [ ] All tests passing
- [ ] Code review completed
- [ ] Security scan passed
- [ ] Performance tests passed
- [ ] Documentation updated

### Deployment
- [ ] Database migrations applied
- [ ] Environment variables set
- [ ] SSL certificates configured
- [ ] Monitoring configured
- [ ] Backup strategy in place

### Post-Deployment
- [ ] Health checks passing
- [ ] Smoke tests completed
- [ ] Performance monitoring active
- [ ] Error tracking configured
- [ ] User acceptance testing completed

---

## 🎯 CONCLUSION

Backend SWP391 được thiết kế theo kiến trúc microservices với các module rõ ràng, bảo mật cao và khả năng mở rộng tốt. Hệ thống tuân thủ các best practices của Spring Boot và có thể handle được traffic lớn với proper caching và database optimization.

Các điểm mạnh:
- ✅ Security mạnh mẽ với JWT + OAuth2
- ✅ Architecture rõ ràng, dễ maintain
- ✅ API documentation đầy đủ
- ✅ Error handling comprehensive
- ✅ Performance optimization
- ✅ Real-time features với WebSocket
- ✅ Comprehensive testing strategy

Cần cải thiện:
- 🔄 Implement rate limiting
- 🔄 Add more comprehensive logging
- 🔄 Enhance caching strategy
- 🔄 Add more unit tests
- 🔄 Implement API versioning 