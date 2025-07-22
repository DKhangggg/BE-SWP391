# RULE.md - Gender Healthcare Service Development Guidelines

## 🎯 Project Overview
This document defines the development standards, coding conventions, and architectural principles for the Gender Healthcare Service project. All developers must follow these guidelines to ensure code quality, maintainability, and team collaboration.

**Ngôn ngữ phát triển:** Tiếng Việt (Vietnamese) - Tất cả comments, documentation, và communication trong team sử dụng tiếng Việt.
**Java Version:** JDK 21 (LTS) - Sử dụng các tính năng mới nhất của Java 21 để tối ưu performance và code quality.

## 📋 Table of Contents
1. [Java 21 Features & Requirements](#java-21-features--requirements)
2. [Vietnamese Language Standards](#vietnamese-language-standards)
3. [Coding Standards](#coding-standards)
4. [Architecture Principles](#architecture-principles)
5. [Security Guidelines](#security-guidelines)
6. [Database Standards](#database-standards)
7. [API Design Rules](#api-design-rules)
8. [Testing Requirements](#testing-requirements)
9. [Code Quality Rules](#code-quality-rules)
10. [Git Workflow](#git-workflow)
11. [Documentation Standards](#documentation-standards)
12. [Performance Guidelines](#performance-guidelines)

## ☕ Java 21 Features & Requirements

### JDK 21 Setup Requirements
```bash
# Kiểm tra Java version
java -version
# Output phải là: openjdk version "21.0.x" hoặc java version "21.0.x"

# Maven compiler configuration (đã có trong pom.xml)
<properties>
    <java.version>21</java.version>
    <maven.compiler.source>21</maven.compiler.source>
    <maven.compiler.target>21</maven.compiler.target>
</properties>
```

### Java 21 Features to Use

#### 1. **Record Classes (Thay thế cho DTOs đơn giản)**
```java
// ✅ SỬ DỤNG: Record cho DTOs đơn giản
public record UserSummaryDTO(
    Long id,
    String username,
    String email,
    String fullName,
    Role role
) {
    // Tự động tạo constructor, getters, equals, hashCode, toString
}

// ✅ SỬ DỤNG: Record với validation
public record CreateBookingRequest(
    @NotNull(message = "User ID không được để trống")
    Long userId,

    @NotNull(message = "Consultant ID không được để trống")
    Long consultantId,

    @Future(message = "Ngày booking phải là ngày trong tương lai")
    LocalDateTime bookingDate,

    @NotBlank(message = "Ghi chú không được để trống")
    String notes
) {}
```

#### 2. **Pattern Matching với Switch Expressions**
```java
// ✅ SỬ DỤNG: Pattern matching cho xử lý enum
public String getBookingStatusMessage(BookingStatus status) {
    return switch (status) {
        case PENDING -> "Đang chờ xác nhận";
        case CONFIRMED -> "Đã xác nhận";
        case COMPLETED -> "Đã hoàn thành";
        case CANCELLED -> "Đã hủy";
        case RESCHEDULED -> "Đã đổi lịch";
    };
}

// ✅ SỬ DỤNG: Pattern matching cho xử lý exception
public ApiResponse<Object> handleException(Exception ex) {
    return switch (ex) {
        case ValidationException ve ->
            ApiResponse.error("Lỗi validation: " + ve.getMessage(), null);
        case ResourceNotFoundException rnfe ->
            ApiResponse.error("Không tìm thấy tài nguyên: " + rnfe.getMessage(), null);
        case SecurityException se ->
            ApiResponse.error("Lỗi bảo mật: " + se.getMessage(), null);
        default ->
            ApiResponse.error("Lỗi hệ thống: " + ex.getMessage(), null);
    };
}
```

#### 3. **Text Blocks cho SQL và JSON**
```java
// ✅ SỬ DỤNG: Text blocks cho complex queries
@Query("""
    SELECT u FROM User u
    JOIN u.consultant c
    WHERE c.specialization = :specialization
    AND c.isAvailable = true
    AND u.isActive = true
    ORDER BY c.rating DESC, u.createdAt ASC
    """)
List<User> findAvailableConsultantsBySpecialization(@Param("specialization") String specialization);

// ✅ SỬ DỤNG: Text blocks cho email templates
private static final String EMAIL_TEMPLATE = """
    Xin chào %s,

    Lịch hẹn của bạn đã được xác nhận:
    - Bác sĩ: %s
    - Thời gian: %s
    - Địa điểm: %s

    Vui lòng đến đúng giờ.

    Trân trọng,
    Gender Healthcare Service
    """;
```

#### 4. **Sealed Classes cho Type Safety**
```java
// ✅ SỬ DỤNG: Sealed classes cho payment result
public sealed interface PaymentResult
    permits PaymentSuccess, PaymentFailure, PaymentPending {
}

public record PaymentSuccess(String transactionId, BigDecimal amount) implements PaymentResult {}
public record PaymentFailure(String errorCode, String message) implements PaymentResult {}
public record PaymentPending(String pendingId, LocalDateTime expiry) implements PaymentResult {}

// Sử dụng với pattern matching
public String processPaymentResult(PaymentResult result) {
    return switch (result) {
        case PaymentSuccess(var txId, var amount) ->
            "Thanh toán thành công. Mã GD: " + txId + ", Số tiền: " + amount;
        case PaymentFailure(var code, var msg) ->
            "Thanh toán thất bại. Mã lỗi: " + code + ", Lý do: " + msg;
        case PaymentPending(var pendingId, var expiry) ->
            "Thanh toán đang xử lý. ID: " + pendingId + ", Hết hạn: " + expiry;
    };
}
```

#### 5. **Virtual Threads (Project Loom)**
```java
// ✅ SỬ DỤNG: Virtual threads cho I/O intensive operations
@Service
public class EmailServiceImpl implements EmailService {

    @Async("virtualThreadExecutor")
    public CompletableFuture<Void> sendEmailAsync(String to, String subject, String content) {
        // Gửi email không đồng bộ với virtual thread
        return CompletableFuture.runAsync(() -> {
            sendEmail(to, subject, content);
        }, Executors.newVirtualThreadPerTaskExecutor());
    }
}

// Configuration cho Virtual Thread Executor
@Configuration
public class AsyncConfig {

    @Bean("virtualThreadExecutor")
    public Executor virtualThreadExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
```

### JDK 21 Performance Optimizations
- **Sử dụng G1GC** (default trong JDK 21) cho better performance
- **Enable preview features** nếu cần: `--enable-preview`
- **JVM tuning** cho production environment
- **Memory optimization** với compact strings và compressed OOPs

## 🇻🇳 Vietnamese Language Standards

### Code Comments & Documentation
```java
/**
 * Tạo tài khoản người dùng mới với thông tin đăng ký được cung cấp.
 *
 * @param request yêu cầu đăng ký chứa thông tin người dùng
 * @return UserResponseDTO chứa thông tin người dùng đã tạo
 * @throws UserAlreadyExistsException nếu username hoặc email đã tồn tại
 * @throws ValidationException nếu dữ liệu yêu cầu không hợp lệ
 */
@Transactional
public UserResponseDTO createUser(RegisterRequest request) {
    // Kiểm tra username đã tồn tại chưa
    if (userRepository.existsByUsername(request.getUsername())) {
        throw new UserAlreadyExistsException("Tên đăng nhập đã tồn tại");
    }

    // Kiểm tra email đã tồn tại chưa
    if (userRepository.existsByEmail(request.getEmail())) {
        throw new UserAlreadyExistsException("Email đã được sử dụng");
    }

    // Mã hóa mật khẩu
    String encodedPassword = passwordEncoder.encode(request.getPassword());

    // Tạo entity User mới
    User user = User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .password(encodedPassword)
            .fullName(request.getFullName())
            .role(Role.USER)
            .isActive(true)
            .build();

    // Lưu vào database
    User savedUser = userRepository.save(user);

    // Gửi email chào mừng
    emailService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getFullName());

    // Chuyển đổi sang DTO và trả về
    return modelMapper.map(savedUser, UserResponseDTO.class);
}
```

### Error Messages & Validation
```java
public class RegisterRequest {
    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 3, max = 50, message = "Tên đăng nhập phải từ 3 đến 50 ký tự")
    private String username;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Định dạng email không hợp lệ")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
             message = "Mật khẩu phải chứa ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt")
    private String password;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100, message = "Họ tên không được vượt quá 100 ký tự")
    private String fullName;

    @Pattern(regexp = "^[0-9+\\-\\s()]+$", message = "Định dạng số điện thoại không hợp lệ")
    private String phone;

    @Past(message = "Ngày sinh phải là ngày trong quá khứ")
    private LocalDate dateOfBirth;

    @NotNull(message = "Vai trò không được để trống")
    private Role role;
}
```

### API Response Messages
```java
// ✅ SỬ DỤNG: Thông báo tiếng Việt cho API responses
public class ApiMessages {
    // User messages
    public static final String USER_CREATED_SUCCESS = "Tạo tài khoản thành công";
    public static final String USER_UPDATED_SUCCESS = "Cập nhật thông tin thành công";
    public static final String USER_NOT_FOUND = "Không tìm thấy người dùng";
    public static final String USER_ALREADY_EXISTS = "Tài khoản đã tồn tại";

    // Booking messages
    public static final String BOOKING_CREATED_SUCCESS = "Đặt lịch thành công";
    public static final String BOOKING_CANCELLED_SUCCESS = "Hủy lịch thành công";
    public static final String BOOKING_NOT_FOUND = "Không tìm thấy lịch hẹn";
    public static final String BOOKING_CONFLICT = "Lịch hẹn bị trung thời gian";

    // Payment messages
    public static final String PAYMENT_SUCCESS = "Thanh toán thành công";
    public static final String PAYMENT_FAILED = "Thanh toán thất bại";
    public static final String PAYMENT_PENDING = "Thanh toán đang xử lý";

    // Authentication messages
    public static final String LOGIN_SUCCESS = "Đăng nhập thành công";
    public static final String LOGIN_FAILED = "Sai tên đăng nhập hoặc mật khẩu";
    public static final String ACCESS_DENIED = "Không có quyền truy cập";
    public static final String TOKEN_EXPIRED = "Token đã hết hạn";
}
```

### Logging Standards (Vietnamese)
```java
// ✅ SỬ DỤNG: Log messages bằng tiếng Việt
@Service
public class BookingServiceImpl implements BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);

    @Transactional
    public BookingResponseDTO createBooking(BookingRequestDTO request) {
        log.info("Bắt đầu tạo booking cho user: {}, consultant: {}",
                request.getUserId(), request.getConsultantId());

        try {
            // Kiểm tra user tồn tại
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> {
                        log.warn("Không tìm thấy user với ID: {}", request.getUserId());
                        return new ResourceNotFoundException("Không tìm thấy người dùng");
                    });

            // Kiểm tra consultant có sẵn không
            if (!consultantService.isAvailable(request.getConsultantId(), request.getBookingDate())) {
                log.warn("Consultant {} không có sẵn vào thời gian: {}",
                        request.getConsultantId(), request.getBookingDate());
                throw new BookingConflictException("Bác sĩ không có sẵn vào thời gian này");
            }

            // Tạo booking
            Booking booking = createBookingEntity(request);
            Booking savedBooking = bookingRepository.save(booking);

            log.info("Tạo booking thành công với ID: {}", savedBooking.getId());

            // Gửi email xác nhận
            emailService.sendBookingConfirmation(user.getEmail(), savedBooking);

            return modelMapper.map(savedBooking, BookingResponseDTO.class);

        } catch (Exception e) {
            log.error("Lỗi khi tạo booking cho user: {}, lý do: {}",
                     request.getUserId(), e.getMessage(), e);
            throw e;
        }
    }
}
```

### Database Comments (Vietnamese)
```sql
-- Bảng người dùng
CREATE TABLE users (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(50) UNIQUE NOT NULL,           -- Tên đăng nhập
    email NVARCHAR(100) UNIQUE NOT NULL,             -- Địa chỉ email
    password NVARCHAR(255) NOT NULL,                 -- Mật khẩu đã mã hóa
    full_name NVARCHAR(100),                         -- Họ và tên
    phone NVARCHAR(20),                              -- Số điện thoại
    date_of_birth DATE,                              -- Ngày sinh
    gender NVARCHAR(10),                             -- Giới tính
    role NVARCHAR(20) NOT NULL,                      -- Vai trò (USER, CONSULTANT, ADMIN, STAFF)
    is_active BIT DEFAULT 1,                         -- Trạng thái hoạt động
    created_at DATETIME2 DEFAULT GETDATE(),          -- Ngày tạo
    updated_at DATETIME2 DEFAULT GETDATE()           -- Ngày cập nhật
);

-- Bảng bác sĩ tư vấn
CREATE TABLE consultants (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT FOREIGN KEY REFERENCES users(id), -- ID người dùng
    specialization NVARCHAR(100),                     -- Chuyên khoa
    experience_years INT,                             -- Số năm kinh nghiệm
    qualification NVARCHAR(500),                      -- Bằng cấp
    bio NTEXT,                                        -- Tiểu sử
    consultation_fee DECIMAL(10,2),                   -- Phí tư vấn
    rating DECIMAL(3,2) DEFAULT 0.0,                  -- Đánh giá
    is_available BIT DEFAULT 1,                       -- Có sẵn hay không
    created_at DATETIME2 DEFAULT GETDATE()            -- Ngày tạo
);
```

## 🔤 Coding Standards

### Naming Conventions
```java
// Classes: PascalCase
public class UserService { }
public class BookingController { }
public class ConsultationRequestDTO { }

// Methods: camelCase
public UserResponseDTO getUserById(Long id) { }
public void createBooking(BookingRequestDTO request) { }

// Variables: camelCase
private String userName;
private LocalDateTime bookingDate;
private List<ConsultantDTO> availableConsultants;

// Constants: UPPER_SNAKE_CASE
public static final int MAX_BOOKING_DAYS = 30;
public static final String DEFAULT_ROLE = "USER";

// Package names: lowercase with dots
com.example.gender_healthcare_service.controller
com.example.gender_healthcare_service.service.impl
```

### Database Naming
```sql
-- Tables: snake_case
users, consultants, booking_history, menstrual_cycles

-- Columns: snake_case
user_id, full_name, created_at, is_active

-- Foreign Keys: table_name + _id
user_id, consultant_id, booking_id

-- Indexes: idx_table_column
idx_users_email, idx_bookings_date, idx_consultants_specialization
```

### File Organization
```
- Controllers: Suffix with "Controller" (UserController.java)
- Services: Suffix with "Service" (UserService.java)
- Service Implementations: Suffix with "ServiceImpl" (UserServiceImpl.java)
- DTOs: Suffix with "DTO" (UserRequestDTO.java, UserResponseDTO.java)
- Entities: No suffix (User.java, Booking.java)
- Repositories: Suffix with "Repository" (UserRepository.java)
- Exceptions: Suffix with "Exception" (ResourceNotFoundException.java)
```

## 🏗️ Architecture Principles

### Layer Responsibilities

#### 1. Controller Layer (Presentation)
```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    // ✅ DO: Handle HTTP requests/responses only
    // ✅ DO: Validate request parameters
    // ✅ DO: Return standardized API responses
    // ❌ DON'T: Include business logic
    // ❌ DON'T: Direct database access
    // ❌ DON'T: Exception handling beyond HTTP status
}
```

#### 2. Service Layer (Business Logic)
```java
@Service
public class UserServiceImpl implements UserService {
    // ✅ DO: Implement business rules
    // ✅ DO: Coordinate between repositories
    // ✅ DO: Handle transactions
    // ✅ DO: Validate business rules
    // ❌ DON'T: Handle HTTP concerns
    // ❌ DON'T: Direct SQL queries
}
```

#### 3. Repository Layer (Data Access)
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // ✅ DO: Data access operations only
    // ✅ DO: Custom queries when needed
    // ❌ DON'T: Business logic
    // ❌ DON'T: Data transformation
}
```

#### 4. DTO Layer (Data Transfer)
```java
public class UserRequestDTO {
    // ✅ DO: Include validation annotations
    // ✅ DO: Keep it simple (data only)
    // ❌ DON'T: Include business logic
    // ❌ DON'T: Direct entity references
}
```

### Design Patterns to Follow
- **Repository Pattern**: For data access abstraction
- **Service Layer Pattern**: For business logic encapsulation
- **DTO Pattern**: For data transfer between layers
- **Factory Pattern**: For complex object creation
- **Strategy Pattern**: For algorithm variations
- **Observer Pattern**: For event handling

### SOLID Principles
1. **Single Responsibility**: Each class has one reason to change
2. **Open/Closed**: Open for extension, closed for modification
3. **Liskov Substitution**: Subtypes must be substitutable for base types
4. **Interface Segregation**: Many specific interfaces better than one general
5. **Dependency Inversion**: Depend on abstractions, not concretions

## 🔐 Security Guidelines

### Authentication & Authorization
```java
// ✅ DO: Use method-level security
@PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
public UserResponseDTO updateUser(@PathVariable Long userId, @RequestBody UserRequestDTO request) {
    // Implementation
}

// ✅ DO: Validate all inputs
@Valid @RequestBody UserRequestDTO request

// ✅ DO: Use proper HTTP status codes
return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
```

### Data Protection Rules
- **Never expose entity objects** directly in API responses
- **Always use DTOs** for data transfer
- **Encrypt sensitive data** (passwords, personal info)
- **Validate all inputs** at controller and service levels
- **Log security events** (login attempts, access violations)
- **Use HTTPS** for all communications
- **Implement rate limiting** for API endpoints

### Password Security
```java
// ✅ DO: Use BCrypt for password hashing
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12); // Strong cost factor
}

// ✅ DO: Validate password strength
@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", 
         message = "Password must contain at least 8 characters, including uppercase, lowercase, number and special character")
private String password;
```

## 🗄️ Database Standards

### Entity Design Rules
```java
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    // ✅ DO: Use proper JPA annotations
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // ✅ DO: Add database constraints
    @Column(unique = true, nullable = false, length = 50)
    private String username;
    
    // ✅ DO: Use appropriate data types
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    // ✅ DO: Use enums for fixed values
    @Enumerated(EnumType.STRING)
    private Role role;
    
    // ✅ DO: Define relationships properly
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings = new ArrayList<>();
}
```

### Query Optimization
- **Use pagination** for large datasets
- **Implement proper indexing** on frequently queried columns
- **Use @Query** for complex queries instead of method names
- **Avoid N+1 queries** with proper fetch strategies
- **Use projections** for read-only operations

### Transaction Management
```java
// ✅ DO: Use declarative transactions
@Transactional
public UserResponseDTO createUser(RegisterRequest request) {
    // Implementation
}

// ✅ DO: Handle rollback scenarios
@Transactional(rollbackFor = Exception.class)
public void processPayment(PaymentRequest request) throws PaymentException {
    // Implementation
}
```

## 🌐 API Design Rules

### RESTful Conventions
```java
// ✅ DO: Follow REST conventions
GET    /api/users           // Get all users
GET    /api/users/{id}      // Get specific user
POST   /api/users           // Create new user
PUT    /api/users/{id}      // Update entire user
PATCH  /api/users/{id}      // Partial update
DELETE /api/users/{id}      // Delete user

// ✅ DO: Use nested resources appropriately
GET    /api/users/{id}/bookings     // Get user's bookings
POST   /api/users/{id}/bookings     // Create booking for user
```

### Response Format Standards
```java
// ✅ DO: Use consistent response format
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    private Map<String, Object> metadata; // For pagination, etc.
}

// ✅ DO: Return appropriate HTTP status codes
200 OK          // Successful GET, PUT, PATCH
201 Created     // Successful POST
204 No Content  // Successful DELETE
400 Bad Request // Validation errors
401 Unauthorized // Authentication required
403 Forbidden   // Access denied
404 Not Found   // Resource not found
500 Internal Server Error // Server errors
```

### Input Validation
```java
// ✅ DO: Validate at multiple levels
@RestController
public class UserController {
    
    @PostMapping("/users")
    public ResponseEntity<ApiResponse<UserResponseDTO>> createUser(
            @Valid @RequestBody RegisterRequest request) { // Controller validation
        
        UserResponseDTO user = userService.createUser(request); // Service validation
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created successfully", user));
    }
}
```

## 🧪 Testing Requirements

### Test Coverage Standards
- **Minimum 80% code coverage** for service layer
- **100% coverage** for critical business logic
- **Integration tests** for all API endpoints
- **Unit tests** for all service methods
- **Mock external dependencies** in tests

### Testing Patterns
```java
// ✅ DO: Follow AAA pattern (Arrange, Act, Assert)
@Test
void shouldCreateUserSuccessfully() {
    // Arrange
    RegisterRequest request = new RegisterRequest();
    request.setUsername("testuser");
    request.setEmail("test@example.com");
    
    // Act
    UserResponseDTO result = userService.createUser(request);
    
    // Assert
    assertThat(result.getUsername()).isEqualTo("testuser");
    assertThat(result.getEmail()).isEqualTo("test@example.com");
}

// ✅ DO: Test edge cases and error scenarios
@Test
void shouldThrowExceptionWhenUserAlreadyExists() {
    // Arrange
    RegisterRequest request = createValidRegisterRequest();
    when(userRepository.existsByUsername(anyString())).thenReturn(true);
    
    // Act & Assert
    assertThatThrownBy(() -> userService.createUser(request))
            .isInstanceOf(UserAlreadyExistsException.class)
            .hasMessage("Username already exists");
}
```

### Test Organization
```
src/test/java/
├── unit/                    // Unit tests
│   ├── service/            // Service layer tests
│   ├── repository/         // Repository tests
│   └── util/              // Utility tests
├── integration/            // Integration tests
│   ├── controller/        // API endpoint tests
│   └── database/          // Database integration tests
└── e2e/                   // End-to-end tests
```

## 📊 Code Quality Rules

### Code Complexity
- **Maximum cyclomatic complexity**: 10 per method
- **Maximum method length**: 50 lines
- **Maximum class length**: 500 lines
- **Maximum parameter count**: 5 per method

### Code Duplication
- **No duplicate code blocks** > 5 lines
- **Extract common logic** into utility methods
- **Use inheritance/composition** for shared behavior
- **Create constants** for repeated values

### Error Handling
```java
// ✅ DO: Handle exceptions appropriately
@Service
public class UserServiceImpl implements UserService {
    
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        return modelMapper.map(user, UserResponseDTO.class);
    }
    
    // ✅ DO: Log errors with context
    @Transactional
    public void processPayment(PaymentRequest request) {
        try {
            // Payment processing logic
        } catch (PaymentException e) {
            log.error("Payment processing failed for user: {}, amount: {}", 
                     request.getUserId(), request.getAmount(), e);
            throw new PaymentProcessingException("Payment failed", e);
        }
    }
}
```

### Logging Standards
```java
// ✅ DO: Use appropriate log levels
log.trace("Entering method getUserById with id: {}", id);  // Detailed debugging
log.debug("Processing user registration for: {}", username); // Development debugging
log.info("User created successfully: {}", userId);          // Important events
log.warn("User login attempt with invalid credentials: {}", username); // Warnings
log.error("Database connection failed", exception);         // Errors

// ✅ DO: Use structured logging
log.info("User action completed - userId: {}, action: {}, duration: {}ms", 
         userId, action, duration);
```

## 🔄 Git Workflow

### Branch Naming Convention
```
main                    // Production branch
develop                 // Development branch
feature/user-auth      // Feature branches
bugfix/login-error     // Bug fix branches
hotfix/security-patch  // Hotfix branches
release/v1.2.0         // Release branches
```

### Commit Message Format
```
type(scope): description

feat(auth): add JWT token refresh functionality
fix(booking): resolve double booking conflict
docs(api): update authentication endpoints documentation
style(user): fix code formatting in UserController
refactor(payment): extract payment validation logic
test(booking): add unit tests for booking service
chore(deps): update Spring Boot to version 3.5.0
```

### Code Review Checklist
- [ ] Code follows naming conventions
- [ ] Proper error handling implemented
- [ ] Unit tests added/updated
- [ ] Documentation updated
- [ ] No security vulnerabilities
- [ ] Performance considerations addressed
- [ ] Code is readable and maintainable

## 📚 Documentation Standards

### Code Documentation
```java
/**
 * Creates a new user account with the provided registration details.
 * 
 * @param request the registration request containing user details
 * @return UserResponseDTO containing the created user information
 * @throws UserAlreadyExistsException if username or email already exists
 * @throws ValidationException if request data is invalid
 */
@Transactional
public UserResponseDTO createUser(RegisterRequest request) {
    // Implementation
}
```

### API Documentation
- **Use OpenAPI/Swagger** annotations
- **Document all endpoints** with examples
- **Include error responses** and status codes
- **Provide request/response schemas**
- **Add authentication requirements**

### README Requirements
- Project setup instructions
- Environment configuration
- API endpoint documentation
- Testing guidelines
- Deployment procedures

## ⚡ Performance Guidelines

### Database Performance
- **Use connection pooling** (HikariCP)
- **Implement query optimization**
- **Add proper indexes** on frequently queried columns
- **Use pagination** for large datasets
- **Implement caching** for frequently accessed data

### API Performance
- **Implement rate limiting**
- **Use compression** for responses
- **Optimize JSON serialization**
- **Implement proper caching headers**
- **Monitor response times**

### Memory Management
- **Use appropriate collection types**
- **Implement proper resource cleanup**
- **Avoid memory leaks** in long-running processes
- **Monitor heap usage**
- **Use lazy loading** for large datasets

## 🚫 Common Anti-Patterns to Avoid

### Code Anti-Patterns
- **God Classes**: Classes that do too much
- **Long Parameter Lists**: More than 5 parameters
- **Magic Numbers**: Use constants instead
- **Deep Nesting**: More than 3 levels of nesting
- **Copy-Paste Programming**: Duplicate code blocks

### Architecture Anti-Patterns
- **Circular Dependencies**: Services depending on each other
- **Tight Coupling**: Classes too dependent on each other
- **Anemic Domain Model**: Entities with no behavior
- **Transaction Script**: All logic in one method
- **Big Ball of Mud**: No clear architecture

### Database Anti-Patterns
- **N+1 Queries**: Multiple queries for related data
- **Missing Indexes**: Slow query performance
- **Over-normalization**: Too many joins required
- **Under-normalization**: Data duplication
- **Ignoring Transactions**: Data consistency issues

## ✅ Development Checklist

### Before Starting Development
- [ ] Understand the requirements clearly
- [ ] Review existing code and architecture
- [ ] Set up development environment
- [ ] Create feature branch from develop
- [ ] Write failing tests first (TDD)

### During Development
- [ ] Follow coding standards
- [ ] Write clean, readable code
- [ ] Add appropriate logging
- [ ] Handle errors gracefully
- [ ] Write unit tests
- [ ] Update documentation

### Before Code Review
- [ ] Run all tests locally
- [ ] Check code coverage
- [ ] Review your own code
- [ ] Update API documentation
- [ ] Write meaningful commit messages

### Before Deployment
- [ ] All tests passing
- [ ] Code review approved
- [ ] Documentation updated
- [ ] Performance tested
- [ ] Security reviewed

---

## 📞 Support & Questions

For questions about these guidelines or clarifications on best practices, please:
1. Check existing documentation first
2. Ask in team chat/meetings
3. Create an issue in the project repository
4. Update this document if new patterns emerge

**Remember: These rules exist to maintain code quality, ensure team collaboration, and create maintainable software. Follow them consistently!** 🎯
