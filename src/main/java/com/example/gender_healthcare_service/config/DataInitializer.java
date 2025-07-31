package com.example.gender_healthcare_service.config;

import com.example.gender_healthcare_service.entity.*;
import com.example.gender_healthcare_service.entity.enumpackage.QuestionStatus;
import com.example.gender_healthcare_service.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BlogCategoryRepository blogCategoryRepository;
    private final BlogPostRepository blogPostRepository;
    private final TestingServiceRepository testingServiceRepository;
    private final ConsultantRepository consultantRepository;
    private final SymptomRepository symptomRepository;
    private final LocationRepository locationRepository;
    private final ConsultantScheduleRepository consultantScheduleRepository;
    private final ReminderRepository reminderRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final FeedbackRepository feedbackRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("🚀 Bắt đầu khởi tạo dữ liệu mẫu...");
        
        try {
            // 1. Tạo Users (nếu chưa có)
            if (userRepository.count() == 0) {
                createUsers();
            } else {
                log.info("✅ Users đã tồn tại, bỏ qua tạo users");
            }
            
            // 2. Tạo Blog Categories (nếu chưa có)
            if (blogCategoryRepository.count() == 0) {
                createBlogCategories();
            } else {
                log.info("✅ Blog categories đã tồn tại, bỏ qua tạo categories");
            }
            
            // 2.1. Tạo Blog Posts (nếu chưa có)
            if (blogPostRepository.count() == 0) {
                createBlogPosts();
            } else {
                log.info("✅ Blog posts đã tồn tại, bỏ qua tạo posts");
            }
            
            // 3. Tạo Testing Services (nếu chưa có)
            if (testingServiceRepository.count() == 0) {
                createTestingServices();
            } else {
                log.info("✅ Testing services đã tồn tại, bỏ qua tạo services");
            }
            
            // 4. Tạo Consultants (nếu chưa có)
            if (consultantRepository.count() == 0) {
                createConsultants();
            } else {
                log.info("✅ Consultants đã tồn tại, bỏ qua tạo consultants");
            }
            
            // 5. Tạo Symptoms (nếu chưa có)
            if (symptomRepository.count() == 0) {
                createSymptoms();
            } else {
                log.info("✅ Symptoms đã tồn tại, bỏ qua tạo symptoms");
            }
            
            // 6. Tạo Locations (nếu chưa có)
            if (locationRepository.count() == 0) {
                createLocations();
            } else {
                log.info("✅ Locations đã tồn tại, bỏ qua tạo locations");
            }
            
            // 7. Tạo Consultant Schedules (nếu chưa có)
            // if (consultantScheduleRepository.count() == 0) {
            //     createConsultantSchedules();
            // } else {
            //     log.info("✅ Consultant schedules đã tồn tại, bỏ qua tạo schedules");
            // }
            
            // 8. Tạo Reminders (nếu chưa có)
            if (reminderRepository.count() == 0) {
                createReminders();
            } else {
                log.info("✅ Reminders đã tồn tại, bỏ qua tạo reminders");
            }
            
            // 9. Tạo Feedback (nếu chưa có)
            if (feedbackRepository.count() == 0) {
                createFeedbacks();
            } else {
                log.info("✅ Feedbacks đã tồn tại, bỏ qua tạo feedbacks");
            }
            
            // 10. Tạo Questions và Answers (nếu chưa có)
            if (questionRepository.count() == 0) {
                createQuestionsAndAnswers();
            } else {
                log.info("✅ Questions và Answers đã tồn tại, bỏ qua tạo questions");
            }
            
            log.info("✅ Khởi tạo dữ liệu mẫu thành công!");
            
        } catch (Exception e) {
            log.error("❌ Lỗi khi khởi tạo dữ liệu mẫu: {}", e.getMessage(), e);
        }
    }

    private void createUsers() {
        log.info("📝 Tạo Users...");
        
        // Admin User
        User admin = new User();
        admin.setUsername("admin");
        admin.setPasswordHash(passwordEncoder.encode("1"));
        admin.setEmail("admin@gynexa.com");
        admin.setFullName("Administrator");
        admin.setPhoneNumber("0123456789");
        admin.setRoleName("ROLE_ADMIN");
        admin.setDescription("System Administrator");
        admin.setDateOfBirth(LocalDate.of(1990, 1, 1));
        admin.setAddress("Hà Nội, Việt Nam");
        admin.setGender("Male");
        admin.setMedicalHistory("Không có");
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());
        admin.setIsDeleted(false);
        
        // Manager User
        User manager = new User();
        manager.setUsername("manager");
        manager.setPasswordHash(passwordEncoder.encode("1"));
        manager.setEmail("manager@gynexa.com");
        manager.setFullName("Manager");
        manager.setPhoneNumber("0123456790");
        manager.setRoleName("ROLE_MANAGER");
        manager.setDescription("System Manager");
        manager.setDateOfBirth(LocalDate.of(1985, 5, 15));
        manager.setAddress("TP.HCM, Việt Nam");
        manager.setGender("Female");
        manager.setMedicalHistory("Không có");
        manager.setCreatedAt(LocalDateTime.now());
        manager.setUpdatedAt(LocalDateTime.now());
        manager.setIsDeleted(false);
        
        // Staff User
        User staff = new User();
        staff.setUsername("staff");
        staff.setPasswordHash(passwordEncoder.encode("1"));
        staff.setEmail("staff@gynexa.com");
        staff.setFullName("Staff Member");
        staff.setPhoneNumber("0123456791");
        staff.setRoleName("ROLE_STAFF");
        staff.setDescription("Medical Staff");
        staff.setDateOfBirth(LocalDate.of(1992, 8, 20));
        staff.setAddress("Đà Nẵng, Việt Nam");
        staff.setGender("Female");
        staff.setMedicalHistory("Không có");
        staff.setCreatedAt(LocalDateTime.now());
        staff.setUpdatedAt(LocalDateTime.now());
        staff.setIsDeleted(false);
        
        // Customer User
        User customer = new User();
        customer.setUsername("customer");
        customer.setPasswordHash(passwordEncoder.encode("1"));
        customer.setEmail("customer@example.com");
        customer.setFullName("Nguyễn Thị Anh");
        customer.setPhoneNumber("0123456792");
        customer.setRoleName("ROLE_CUSTOMER");
        customer.setDescription("Regular Customer");
        customer.setDateOfBirth(LocalDate.of(1995, 12, 10));
        customer.setAddress("Hải Phòng, Việt Nam");
        customer.setGender("Female");
        customer.setMedicalHistory("Không có");
        customer.setCreatedAt(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());
        customer.setIsDeleted(false);
        
        // Customer User
        User customer2 = new User();
        customer2.setUsername("customer2");
        customer2.setPasswordHash(passwordEncoder.encode("1"));
        customer2.setEmail("customer2@example.com");
        customer2.setFullName("Nggọc Anh");
        customer2.setPhoneNumber("0123456122");
        customer2.setRoleName("ROLE_CUSTOMER");
        customer2.setDescription("Regular Customer");
        customer2.setDateOfBirth(LocalDate.of(1995, 11, 25));
        customer2.setAddress("HCM, Việt Nam");
        customer2.setGender("Female");
        customer2.setMedicalHistory("Không có");
        customer2.setCreatedAt(LocalDateTime.now());
        customer2.setUpdatedAt(LocalDateTime.now());
        customer2.setIsDeleted(false);
        
         // Customer User
         User customer3 = new User();
         customer3.setUsername("customer3");
         customer3.setPasswordHash(passwordEncoder.encode("1"));
         customer3.setEmail("customer3@example.com");
         customer3.setFullName("Mina Anh");
         customer3.setPhoneNumber("0123456169");
         customer3.setRoleName("ROLE_CUSTOMER");
         customer3.setDescription("Regular Customer");
         customer3.setDateOfBirth(LocalDate.of(1995, 11, 24));
         customer3.setAddress("Tây Ninh, Việt Nam");
         customer3.setGender("Female");
         customer3.setMedicalHistory("Không có");
         customer3.setCreatedAt(LocalDateTime.now());
         customer3.setUpdatedAt(LocalDateTime.now());
         customer3.setIsDeleted(false);
    

        userRepository.saveAll(Arrays.asList(admin, manager, staff, customer,customer2,customer3));
        log.info("✅ Đã tạo {} users", userRepository.count());
    }

    private void createBlogCategories() {
        log.info("📂 Tạo Blog Categories...");
        
        BlogCategory category1 = new BlogCategory();
        category1.setCategoryName("Sức khỏe sinh sản");
        category1.setSlug("suc-khoe-sinh-san");
        category1.setDescription("Thông tin và lời khuyên về sức khỏe sinh sản phụ nữ, bao gồm các vấn đề về sinh sản, thai kỳ và các bệnh phụ khoa.");
        category1.setCreatedAt(LocalDateTime.now());
        category1.setUpdatedAt(LocalDateTime.now());
        category1.setIsDeleted(false);
        
        BlogCategory category2 = new BlogCategory();
        category2.setCategoryName("Chu kỳ kinh nguyệt");
        category2.setSlug("chu-ky-kinh-nguyet");
        category2.setDescription("Kiến thức về chu kỳ kinh nguyệt và các vấn đề liên quan, cách theo dõi và quản lý chu kỳ.");
        category2.setCreatedAt(LocalDateTime.now());
        category2.setUpdatedAt(LocalDateTime.now());
        category2.setIsDeleted(false);
        
        BlogCategory category3 = new BlogCategory();
        category3.setCategoryName("Mang thai & Sinh nở");
        category3.setSlug("mang-thai-sinh-no");
        category3.setDescription("Hướng dẫn và chăm sóc trong thời kỳ mang thai, chuẩn bị sinh nở và chăm sóc sau sinh.");
        category3.setCreatedAt(LocalDateTime.now());
        category3.setUpdatedAt(LocalDateTime.now());
        category3.setIsDeleted(false);
        
        BlogCategory category4 = new BlogCategory();
        category4.setCategoryName("Dinh dưỡng & Lối sống");
        category4.setSlug("dinh-duong-loi-song");
        category4.setDescription("Dinh dưỡng và lối sống healthy cho phụ nữ, bao gồm chế độ ăn uống và tập luyện.");
        category4.setCreatedAt(LocalDateTime.now());
        category4.setUpdatedAt(LocalDateTime.now());
        category4.setIsDeleted(false);
        
        BlogCategory category5 = new BlogCategory();
        category5.setCategoryName("Tâm lý & Tinh thần");
        category5.setSlug("tam-ly-tinh-than");
        category5.setDescription("Chăm sóc sức khỏe tinh thần và tâm lý phụ nữ, bao gồm stress, trầm cảm và các vấn đề tâm lý khác.");
        category5.setCreatedAt(LocalDateTime.now());
        category5.setUpdatedAt(LocalDateTime.now());
        category5.setIsDeleted(false);
        
        BlogCategory category6 = new BlogCategory();
        category6.setCategoryName("Bệnh phụ khoa");
        category6.setSlug("benh-phu-khoa");
        category6.setDescription("Thông tin về các bệnh phụ khoa thường gặp, cách phòng ngừa và điều trị.");
        category6.setCreatedAt(LocalDateTime.now());
        category6.setUpdatedAt(LocalDateTime.now());
        category6.setIsDeleted(false);
        
        blogCategoryRepository.saveAll(Arrays.asList(category1, category2, category3, category4, category5, category6));
        log.info("✅ Đã tạo {} blog categories", blogCategoryRepository.count());
    }

    private void createTestingServices() {
        log.info("🔬 Tạo Testing Services...");
        
        TestingService service1 = new TestingService();
        service1.setServiceName("Xét nghiệm hormone sinh sản");
        service1.setDescription("Xét nghiệm đầy đủ các hormone sinh sản bao gồm FSH, LH, Estrogen, Progesterone và Testosterone để đánh giá chức năng sinh sản.");
        service1.setPrice(new BigDecimal("850000.0"));
        service1.setDurationMinutes(119);
        service1.setStatus("ACTIVE");
        service1.setCreatedAt(LocalDateTime.now());
        service1.setUpdatedAt(LocalDateTime.now());
        service1.setIsDeleted(false);
        
        TestingService service2 = new TestingService();
        service2.setServiceName("Tầm soát ung thư cổ tử cung (Pap test)");
        service2.setDescription("Xét nghiệm tế bào học cổ tử cung để phát hiện sớm các tế bào bất thường và nguy cơ ung thư cổ tử cung.");
        service2.setPrice(new BigDecimal("450000.0"));
        service2.setDurationMinutes(30);
        service2.setStatus("ACTIVE");
        service2.setCreatedAt(LocalDateTime.now());
        service2.setUpdatedAt(LocalDateTime.now());
        service2.setIsDeleted(false);
        
        TestingService service3 = new TestingService();
        service3.setServiceName("Siêu âm tử cung buồng trứng");
        service3.setDescription("Siêu âm đánh giá cấu trúc và chức năng của tử cung, buồng trứng và các cơ quan sinh sản khác.");
        service3.setPrice(new BigDecimal("320000.0"));
        service3.setDurationMinutes(45);
        service3.setStatus("ACTIVE");
        service3.setCreatedAt(LocalDateTime.now());
        service3.setUpdatedAt(LocalDateTime.now());
        service3.setIsDeleted(false);
        
        TestingService service4 = new TestingService();
        service4.setServiceName("Xét nghiệm nhiễm khuẩn sinh dục");
        service4.setDescription("Xét nghiệm toàn diện các tác nhân gây nhiễm khuẩn đường sinh dục như Chlamydia, Gonorrhea, Trichomonas và các vi khuẩn khác.");
        service4.setPrice(new BigDecimal("680000.0"));
        service4.setDurationMinutes(90);
        service4.setStatus("ACTIVE");
        service4.setCreatedAt(LocalDateTime.now());
        service4.setUpdatedAt(LocalDateTime.now());
        service4.setIsDeleted(false);
        
        TestingService service5 = new TestingService();
        service5.setServiceName("Xét nghiệm vitamin và khoáng chất");
        service5.setDescription("Đánh giá mức độ vitamin D, B12, Folate, sắt, canxi và các khoáng chất quan trọng khác cho sức khỏe phụ nữ.");
        service5.setPrice(new BigDecimal("520000.0"));
        service5.setDurationMinutes(60);
        service5.setStatus("ACTIVE");
        service5.setCreatedAt(LocalDateTime.now());
        service5.setUpdatedAt(LocalDateTime.now());
        service5.setIsDeleted(false);
        
        TestingService service6 = new TestingService();
        service6.setServiceName("Xét nghiệm tiền mãn kinh");
        service6.setDescription("Bộ xét nghiệm chuyên biệt đánh giá tình trạng tiền mãn kinh và các thay đổi hormone liên quan.");
        service6.setPrice(new BigDecimal("420000.0"));
        service6.setDurationMinutes(75);
        service6.setStatus("ACTIVE");
        service6.setCreatedAt(LocalDateTime.now());
        service6.setUpdatedAt(LocalDateTime.now());
        service6.setIsDeleted(false);
        
        testingServiceRepository.saveAll(Arrays.asList(service1, service2, service3, service4, service5, service6));
        log.info("✅ Đã tạo {} testing services", testingServiceRepository.count());
    }

    private void createConsultants() {
        log.info("👨‍⚕️ Tạo Consultants...");
        
        // Kiểm tra xem consultant đã tồn tại chưa
        if (consultantRepository.count() > 0) {
            log.info("✅ Consultants đã tồn tại, bỏ qua tạo consultants");
            return;
        }
        
        // Tạo User consultant1
        User consultantUser1 = userRepository.findUserByUsername("consultant1");
        if (consultantUser1 == null) {
            consultantUser1 = new User();
            consultantUser1.setUsername("consultant1");
            consultantUser1.setPasswordHash(passwordEncoder.encode("1"));
            consultantUser1.setEmail("consultant1@gynexa.com");
            consultantUser1.setFullName("Bác sĩ Nguyễn Thị Hương");
            consultantUser1.setPhoneNumber("0123456793");
            consultantUser1.setRoleName("ROLE_CONSULTANT");
            consultantUser1.setDescription("Bác sĩ chuyên khoa Sản Phụ khoa");
            consultantUser1.setDateOfBirth(LocalDate.of(1980, 3, 15));
            consultantUser1.setAddress("TP.HCM, Việt Nam");
            consultantUser1.setGender("Female");
            consultantUser1.setMedicalHistory("Không có");
            consultantUser1.setCreatedAt(LocalDateTime.now());
            consultantUser1.setUpdatedAt(LocalDateTime.now());
            consultantUser1.setIsDeleted(false);
            consultantUser1 = userRepository.save(consultantUser1);
        }
        
        // Tạo User consultant2
        User consultantUser2 = userRepository.findUserByUsername("consultant2");
        if (consultantUser2 == null) {
            consultantUser2 = new User();
            consultantUser2.setUsername("consultant2");
            consultantUser2.setPasswordHash(passwordEncoder.encode("1"));
            consultantUser2.setEmail("consultant2@gynexa.com");
            consultantUser2.setFullName("Bác sĩ Trần Văn Minh");
            consultantUser2.setPhoneNumber("0123456794");
            consultantUser2.setRoleName("ROLE_CONSULTANT");
            consultantUser2.setDescription("Bác sĩ chuyên khoa Nội tiết - Sinh sản");
            consultantUser2.setDateOfBirth(LocalDate.of(1985, 7, 22));
            consultantUser2.setAddress("Hà Nội, Việt Nam");
            consultantUser2.setGender("Male");
            consultantUser2.setMedicalHistory("Không có");
            consultantUser2.setCreatedAt(LocalDateTime.now());
            consultantUser2.setUpdatedAt(LocalDateTime.now());
            consultantUser2.setIsDeleted(false);
            consultantUser2 = userRepository.save(consultantUser2);
        }
        
        // Tạo User consultant3
        User consultantUser3 = userRepository.findUserByUsername("consultant3");
        if (consultantUser3 == null) {
            consultantUser3 = new User();
            consultantUser3.setUsername("consultant3");
            consultantUser3.setPasswordHash(passwordEncoder.encode("1"));
            consultantUser3.setEmail("consultant3@gynexa.com");
            consultantUser3.setFullName("Bác sĩ Lê Thị Lan");
            consultantUser3.setPhoneNumber("0123456795");
            consultantUser3.setRoleName("ROLE_CONSULTANT");
            consultantUser3.setDescription("Bác sĩ chuyên về tâm lý và sức khỏe tinh thần phụ nữ");
            consultantUser3.setDateOfBirth(LocalDate.of(1988, 11, 8));
            consultantUser3.setAddress("Đà Nẵng, Việt Nam");
            consultantUser3.setGender("Female");
            consultantUser3.setMedicalHistory("Không có");
            consultantUser3.setCreatedAt(LocalDateTime.now());
            consultantUser3.setUpdatedAt(LocalDateTime.now());
            consultantUser3.setIsDeleted(false);
            consultantUser3 = userRepository.save(consultantUser3);
        }
        
        // Tạo Consultant records - KHÔNG set ID cứng
        Consultant consultant1 = new Consultant();
        consultant1.setUser(consultantUser1);
        consultant1.setBiography("Bác sĩ chuyên khoa Sản Phụ khoa với hơn 10 năm kinh nghiệm trong việc chăm sóc sức khỏe phụ nữ. Chuyên về các vấn đề sinh sản, thai kỳ và các bệnh phụ khoa.");
        consultant1.setQualifications("Đại học Y khoa TP.HCM, Chuyên khoa cấp 1 Sản Phụ khoa, Chứng chỉ siêu âm sản phụ khoa");
        consultant1.setExperienceYears(10);
        consultant1.setSpecialization("Sản Phụ khoa");
        consultant1.setIsDeleted(false);
        
        Consultant consultant2 = new Consultant();
        consultant2.setUser(consultantUser2);
        consultant2.setBiography("Bác sĩ chuyên khoa Nội tiết - Sinh sản với 8 năm kinh nghiệm. Chuyên về các rối loạn nội tiết, vô sinh và các vấn đề sinh sản.");
        consultant2.setQualifications("Đại học Y Dược TP.HCM, Thạc sĩ Y học, Chứng chỉ chuyên khoa Nội tiết");
        consultant2.setExperienceYears(8);
        consultant2.setSpecialization("Nội tiết - Sinh sản");
        consultant2.setIsDeleted(false);
        
        Consultant consultant3 = new Consultant();
        consultant3.setUser(consultantUser3);
        consultant3.setBiography("Bác sĩ chuyên về tâm lý và sức khỏe tinh thần phụ nữ. Có kinh nghiệm trong việc tư vấn tâm lý cho phụ nữ trong các giai đoạn khác nhau của cuộc sống.");
        consultant3.setQualifications("Đại học Y khoa Hà Nội, Chứng chỉ tâm lý lâm sàng. Chuyên về tâm lý phụ nữ và gia đình.");
        consultant3.setExperienceYears(6);
        consultant3.setSpecialization("Tâm lý sức khỏe phụ nữ");
        consultant3.setIsDeleted(false);
        
        // Save từng consultant một
        consultantRepository.save(consultant1);
        consultantRepository.save(consultant2);
        consultantRepository.save(consultant3);
        
        log.info("✅ Đã tạo {} consultants", consultantRepository.count());
    }

    private void createSymptoms() {
        log.info("🏥 Tạo Symptoms...");
        
        Symptom symptom1 = new Symptom();
        symptom1.setSymptomName("Đau bụng kinh");
        symptom1.setCategory("Chu kỳ kinh nguyệt");
        symptom1.setDescription("Đau bụng dưới trong thời kỳ kinh nguyệt");
        symptom1.setIsActive(true);
        
        Symptom symptom2 = new Symptom();
        symptom2.setSymptomName("Chảy máu bất thường");
        symptom2.setCategory("Chu kỳ kinh nguyệt");
        symptom2.setDescription("Chảy máu âm đạo không theo chu kỳ");
        symptom2.setIsActive(true);
        
        Symptom symptom3 = new Symptom();
        symptom3.setSymptomName("Mệt mỏi");
        symptom3.setCategory("Triệu chứng chung");
        symptom3.setDescription("Cảm giác mệt mỏi, thiếu năng lượng");
        symptom3.setIsActive(true);
        
        Symptom symptom4 = new Symptom();
        symptom4.setSymptomName("Đau đầu");
        symptom4.setCategory("Triệu chứng chung");
        symptom4.setDescription("Đau đầu, có thể kèm theo buồn nôn");
        symptom4.setIsActive(true);
        
        Symptom symptom5 = new Symptom();
        symptom5.setSymptomName("Thay đổi tâm trạng");
        symptom5.setCategory("Tâm lý");
        symptom5.setDescription("Thay đổi tâm trạng, dễ cáu gắt hoặc buồn bã");
        symptom5.setIsActive(true);
        
        Symptom symptom6 = new Symptom();
        symptom6.setSymptomName("Đau ngực");
        symptom6.setCategory("Triệu chứng chung");
        symptom6.setDescription("Đau hoặc căng tức vùng ngực");
        symptom6.setIsActive(true);
        
        Symptom symptom7 = new Symptom();
        symptom7.setSymptomName("Khó ngủ");
        symptom7.setCategory("Giấc ngủ");
        symptom7.setDescription("Khó đi vào giấc ngủ hoặc ngủ không sâu");
        symptom7.setIsActive(true);
        
        Symptom symptom8 = new Symptom();
        symptom8.setSymptomName("Chán ăn");
        symptom8.setCategory("Tiêu hóa");
        symptom8.setDescription("Giảm cảm giác thèm ăn");
        symptom8.setIsActive(true);
        
        Symptom symptom9 = new Symptom();
        symptom9.setSymptomName("Buồn nôn");
        symptom9.setCategory("Tiêu hóa");
        symptom9.setDescription("Cảm giác buồn nôn, có thể kèm theo nôn");
        symptom9.setIsActive(true);
        
        symptomRepository.saveAll(Arrays.asList(symptom1, symptom2, symptom3, symptom4, symptom5, symptom6, symptom7, symptom8, symptom9));
        log.info("✅ Đã tạo {} symptoms", symptomRepository.count());
    }

    private void createLocations() {
        log.info("🏥 Tạo Locations...");
        
        Location location1 = new Location();
        location1.setName("Phòng khám Đa khoa Nguyễn Trãi");
        location1.setAddress("123 Nguyễn Trãi, Quận 1, TP.HCM");
        location1.setPhone("0283 888 9999");
        location1.setHours("T2 - CN: 7h00 - 20h00");
        location1.setStatus("Hoạt động");
        location1.setIsDeleted(false);
        location1.setCreateAt(LocalDateTime.now());
        location1.setUpdateAt(LocalDateTime.now());
        
        Location location2 = new Location();
        location2.setName("Phòng khám Phụ khoa Tân Bình");
        location2.setAddress("456 Lê Văn Việt, Tân Bình, TP.HCM");
        location2.setPhone("0283 777 8888");
        location2.setHours("T2 - T7: 8h00 - 17h00");
        location2.setStatus("Hoạt động");
        location2.setIsDeleted(false);
        location2.setCreateAt(LocalDateTime.now());
        location2.setUpdateAt(LocalDateTime.now());
        
        Location location3 = new Location();
        location3.setName("Bệnh viện Phụ sản Hùng Vương");
        location3.setAddress("789 Lý Thường Kiệt, Quận 10, TP.HCM");
        location3.setPhone("0283 666 7777");
        location3.setHours("T2 - CN: 24/24");
        location3.setStatus("Hoạt động");
        location3.setIsDeleted(false);
        location3.setCreateAt(LocalDateTime.now());
        location3.setUpdateAt(LocalDateTime.now());
        
        Location location4 = new Location();
        location4.setName("Trạm Y tế Phường Linh Trung");
        location4.setAddress("12 Lê Văn Việt, Thủ Đức, TP.HCM");
        location4.setPhone("0283 111 2233");
        location4.setHours("T2 - T6: 7h30 - 16h00");
        location4.setStatus("Đang bảo trì");
        location4.setIsDeleted(false);
        location4.setCreateAt(LocalDateTime.now());
        location4.setUpdateAt(LocalDateTime.now());
        
        Location location5 = new Location();
        location5.setName("Phòng khám Tư nhân Dr. Hoa");
        location5.setAddress("321 Võ Văn Tần, Quận 3, TP.HCM");
        location5.setPhone("0283 444 5555");
        location5.setHours("T2 - T7: 9h00 - 18h00");
        location5.setStatus("Hoạt động");
        location5.setIsDeleted(false);
        location5.setCreateAt(LocalDateTime.now());
        location5.setUpdateAt(LocalDateTime.now());
        
        locationRepository.saveAll(Arrays.asList(location1, location2, location3, location4, location5));
        log.info("✅ Đã tạo {} locations", locationRepository.count());
    }

    private void createConsultantSchedules() {
        log.info("📅 Tạo Consultant Schedules...");
        
        // Lấy consultants
        List<Consultant> consultants = consultantRepository.findAll();
        if (consultants.isEmpty()) {
            log.warn("⚠️ Không có consultants để tạo schedule");
            return;
        }
        
        LocalDate startDate = LocalDate.now().plusDays(1);
        List<ConsultantSchedule> schedules = new ArrayList<>();
        
        for (Consultant consultant : consultants) {
            // Tạo schedule cho 3 ngày tiếp theo
            for (int i = 0; i < 3; i++) {
                LocalDate scheduleDate = startDate.plusDays(i);
                
                // Tạo 2 time slots mỗi ngày
                for (int slot = 1; slot <= 2; slot++) {
                    ConsultantSchedule schedule = new ConsultantSchedule();
                    schedule.setConsultant(consultant);
                    schedule.setScheduleDate(scheduleDate);
                    schedule.setStatus("AVAILABLE");
                    schedule.setNotes("Sẵn sàng khám");
                    schedule.setCreatedAt(LocalDateTime.now());
                    schedule.setIsDeleted(false);
                    schedules.add(schedule);
                }
            }
        }
        
        consultantScheduleRepository.saveAll(schedules);
        log.info("✅ Đã tạo {} consultant schedules", consultantScheduleRepository.count());
    }

    private void createReminders() {
        log.info("⏰ Tạo Reminders...");
        
        // Lấy customer user
        User customer = userRepository.findUserByUsername("customer");
        if (customer == null) {
            log.warn("⚠️ Không có customer để tạo reminders");
            return;
        }
        
        List<Reminder> reminders = new ArrayList<>();
        
        for (int i = 1; i <= 5; i++) {
            Reminder reminder = new Reminder();
            reminder.setUser(customer);
            reminder.setReminderType("Khám định kỳ");
            reminder.setReminderDate(LocalDateTime.now().plusDays(i * 7));
            reminder.setReminderTime(LocalDateTime.now().plusDays(i * 7).withHour(9).withMinute(0));
            reminder.setMessage("Nhắc nhở: Lịch khám định kỳ vào tuần tới");
            reminder.setIsSent(false);
            reminder.setCreatedAt(LocalDateTime.now());
            reminder.setIsDeleted(false);
            reminders.add(reminder);
        }
        
        reminderRepository.saveAll(reminders);
        log.info("✅ Đã tạo {} reminders", reminderRepository.count());
    }

    private void createFeedbacks() {
        log.info("💬 Tạo Feedbacks...");
        
        // Lấy customers
        User customer1 = userRepository.findUserByUsername("customer");
        User customer2 = userRepository.findUserByUsername("customer2");
        User customer3 = userRepository.findUserByUsername("customer3");
        
        // Lấy consultants
        List<Consultant> consultants = consultantRepository.findAll();
        
        if (customer1 == null || customer2 == null || customer3 == null) {
            log.warn("⚠️ Không có đủ customers để tạo feedbacks");
            return;
        }
        
        if (consultants.isEmpty()) {
            log.warn("⚠️ Không có consultants để tạo feedbacks");
            return;
        }
        
        List<Feedback> feedbacks = new ArrayList<>();
        
        // Feedback từ customer1 cho consultant1
        Feedback feedback1 = new Feedback();
        feedback1.setCustomer(customer1);
        feedback1.setConsultant(consultants.get(0).getUser());
        feedback1.setRating(5);
        feedback1.setComment("Bác sĩ rất tận tâm và chuyên nghiệp. Tôi rất hài lòng với dịch vụ tư vấn!");
        feedback1.setCreatedAt(LocalDateTime.now().minusDays(5));
        feedback1.setIsDeleted(false);
        feedbacks.add(feedback1);
        
        // Feedback từ customer2 cho consultant2
        Feedback feedback2 = new Feedback();
        feedback2.setCustomer(customer2);
        feedback2.setConsultant(consultants.get(1).getUser());
        feedback2.setRating(4);
        feedback2.setComment("Bác sĩ giải thích rất rõ ràng và dễ hiểu. Cảm ơn bác sĩ đã tư vấn!");
        feedback2.setCreatedAt(LocalDateTime.now().minusDays(3));
        feedback2.setIsDeleted(false);
        feedbacks.add(feedback2);
        
        // Feedback từ customer3 cho consultant3
        Feedback feedback3 = new Feedback();
        feedback3.setCustomer(customer3);
        feedback3.setConsultant(consultants.get(2).getUser());
        feedback3.setRating(5);
        feedback3.setComment("Dịch vụ rất tốt, bác sĩ rất kiên nhẫn và tận tâm. Tôi sẽ giới thiệu cho bạn bè!");
        feedback3.setCreatedAt(LocalDateTime.now().minusDays(1));
        feedback3.setIsDeleted(false);
        feedbacks.add(feedback3);
        
        // Feedback từ customer1 cho consultant2
        Feedback feedback4 = new Feedback();
        feedback4.setCustomer(customer1);
        feedback4.setConsultant(consultants.get(1).getUser());
        feedback4.setRating(4);
        feedback4.setComment("Bác sĩ rất giỏi và có kinh nghiệm. Tôi rất tin tưởng!");
        feedback4.setCreatedAt(LocalDateTime.now().minusDays(2));
        feedback4.setIsDeleted(false);
        feedbacks.add(feedback4);
        
        // Feedback từ customer2 cho consultant3
        Feedback feedback5 = new Feedback();
        feedback5.setCustomer(customer2);
        feedback5.setConsultant(consultants.get(2).getUser());
        feedback5.setRating(5);
        feedback5.setComment("Bác sĩ rất tận tâm và chuyên nghiệp. Tôi rất hài lòng!");
        feedback5.setCreatedAt(LocalDateTime.now().minusDays(4));
        feedback5.setIsDeleted(false);
        feedbacks.add(feedback5);
        
        feedbackRepository.saveAll(feedbacks);
        log.info("✅ Đã tạo {} feedbacks", feedbackRepository.count());
    }

    private void createQuestionsAndAnswers() {
        log.info("❓ Tạo Questions và Answers...");
        
        // Lấy customers
        User customer1 = userRepository.findUserByUsername("customer");
        User customer2 = userRepository.findUserByUsername("customer2");
        User customer3 = userRepository.findUserByUsername("customer3");
        
        // Lấy consultants
        List<Consultant> consultants = consultantRepository.findAll();
        
        if (customer1 == null || customer2 == null || customer3 == null) {
            log.warn("⚠️ Không có đủ customers để tạo questions");
            return;
        }
        
        if (consultants.isEmpty()) {
            log.warn("⚠️ Không có consultants để tạo answers");
            return;
        }
        
        List<Question> questions = new ArrayList<>();
        List<Answer> answers = new ArrayList<>();
        
        // Question 1 từ customer1
        Question question1 = new Question();
        question1.setUser(customer1);
        question1.setCategory("general");
        question1.setContent("Tôi muốn hỏi về chu kỳ kinh nguyệt không đều. Có cách nào để điều hòa không?");
        question1.setStatus(QuestionStatus.ANSWERED);
        question1.setPublic(true);
        question1.setAnswered(true);
        question1.setCreatedAt(LocalDateTime.now().minusDays(10));
        question1.setUpdatedAt(LocalDateTime.now().minusDays(8));
        question1.setDeleted(false);
        questions.add(question1);
        
        // Answer 1 từ consultant1
        Answer answer1 = new Answer();
        answer1.setQuestion(question1);
        answer1.setConsultant(consultants.get(0));
        answer1.setContent("Chu kỳ kinh nguyệt không đều có thể do stress, ăn uống, hoặc hormone. Bạn nên duy trì lối sống lành mạnh và khám bác sĩ nếu kéo dài.");
        answer1.setCreatedAt(LocalDateTime.now().minusDays(8));
        answer1.setUpdatedAt(LocalDateTime.now().minusDays(8));
        answer1.setDeleted(false);
        answers.add(answer1);
        
        // Question 2 từ customer2
        Question question2 = new Question();
        question2.setUser(customer2);
        question2.setCategory("contraception");
        question2.setContent("Tôi đang tìm hiểu về các phương pháp tránh thai. Bác sĩ có thể tư vấn giúp tôi không?");
        question2.setStatus(QuestionStatus.ANSWERED);
        question2.setPublic(true);
        question2.setAnswered(true);
        question2.setCreatedAt(LocalDateTime.now().minusDays(7));
        question2.setUpdatedAt(LocalDateTime.now().minusDays(6));
        question2.setDeleted(false);
        questions.add(question2);
        
        // Answer 2 từ consultant2
        Answer answer2 = new Answer();
        answer2.setQuestion(question2);
        answer2.setConsultant(consultants.get(1));
        answer2.setContent("Có nhiều phương pháp tránh thai như thuốc, vòng, bao cao su. Bạn nên gặp bác sĩ để chọn phương pháp phù hợp.");
        answer2.setCreatedAt(LocalDateTime.now().minusDays(6));
        answer2.setUpdatedAt(LocalDateTime.now().minusDays(6));
        answer2.setDeleted(false);
        answers.add(answer2);
        
        // Question 3 từ customer3
        Question question3 = new Question();
        question3.setUser(customer3);
        question3.setCategory("menstruation");
        question3.setContent("Tôi thường bị đau bụng kinh rất nhiều. Có cách nào giảm đau hiệu quả không?");
        question3.setStatus(QuestionStatus.ANSWERED);
        question3.setPublic(true);
        question3.setAnswered(true);
        question3.setCreatedAt(LocalDateTime.now().minusDays(5));
        question3.setUpdatedAt(LocalDateTime.now().minusDays(4));
        question3.setDeleted(false);
        questions.add(question3);
        
        // Answer 3 từ consultant3
        Answer answer3 = new Answer();
        answer3.setQuestion(question3);
        answer3.setConsultant(consultants.get(2));
        answer3.setContent("Đau bụng kinh có thể giảm bằng chườm ấm, tập thể dục nhẹ, ăn uống đủ chất. Nếu đau nhiều, nên đi khám bác sĩ.");
        answer3.setCreatedAt(LocalDateTime.now().minusDays(4));
        answer3.setUpdatedAt(LocalDateTime.now().minusDays(4));
        answer3.setDeleted(false);
        answers.add(answer3);
        
        // Question 4 từ customer1 (chưa trả lời)
        Question question4 = new Question();
        question4.setUser(customer1);
        question4.setCategory("pregnancy");
        question4.setContent("Tôi đang mang thai tháng thứ 3. Cần lưu ý gì về dinh dưỡng không?");
        question4.setStatus(QuestionStatus.PENDING);
        question4.setPublic(true);
        question4.setAnswered(false);
        question4.setCreatedAt(LocalDateTime.now().minusDays(2));
        question4.setUpdatedAt(LocalDateTime.now().minusDays(2));
        question4.setDeleted(false);
        questions.add(question4);
        
        // Question 5 từ customer2 (chưa trả lời)
        Question question5 = new Question();
        question5.setUser(customer2);
        question5.setCategory("sti");
        question5.setContent("Tôi muốn tìm hiểu về các bệnh lây truyền qua đường tình dục. Cách phòng ngừa hiệu quả là gì?");
        question5.setStatus(QuestionStatus.PENDING);
        question5.setPublic(true);
        question5.setAnswered(false);
        question5.setCreatedAt(LocalDateTime.now().minusDays(1));
        question5.setUpdatedAt(LocalDateTime.now().minusDays(1));
        question5.setDeleted(false);
        questions.add(question5);
        
        questionRepository.saveAll(questions);
        answerRepository.saveAll(answers);
        
        log.info("✅ Đã tạo {} questions và {} answers", questionRepository.count(), answerRepository.count());
    }

    private void createBlogPosts() {
        log.info("📝 Tạo Blog Posts...");
        
        // Lấy categories và authors
        List<BlogCategory> categories = blogCategoryRepository.findAll();
        List<User> authors = new ArrayList<>();
        authors.addAll(userRepository.findUserByRoleName("ROLE_ADMIN"));
        authors.addAll(userRepository.findUserByRoleName("ROLE_CONSULTANT"));
        authors.addAll(userRepository.findUserByRoleName("ROLE_STAFF"));
        
        if (categories.isEmpty()) {
            log.warn("⚠️ Không có categories để tạo blog posts");
            return;
        }
        
        if (authors.isEmpty()) {
            log.warn("⚠️ Không có authors để tạo blog posts");
            return;
        }
        
        List<BlogPost> posts = new ArrayList<>();
        
        // Post 1: Sức khỏe sinh sản
        BlogPost post1 = new BlogPost();
        post1.setTitle("Sức khỏe sinh sản nữ giới: Những điều cần biết");
        post1.setSlug("suc-khoe-sinh-san-nu-gioi-nhung-dieu-can-biet");
        post1.setSummary("Bài viết cung cấp thông tin cơ bản về sức khỏe sinh sản nữ giới và cách chăm sóc.");
        post1.setContent("<h2>Sức khỏe sinh sản là gì?</h2><p>Sức khỏe sinh sản là một phần quan trọng trong cuộc sống của phụ nữ. Bài viết này sẽ cung cấp những thông tin cơ bản về sức khỏe sinh sản và cách chăm sóc...</p><h3>1. Khám phụ khoa định kỳ</h3><p>Việc khám phụ khoa định kỳ rất quan trọng để phát hiện sớm các vấn đề về sức khỏe sinh sản...</p><h3>2. Vệ sinh cá nhân</h3><p>Vệ sinh cá nhân đúng cách giúp phòng ngừa các bệnh nhiễm trùng...</p>");
        post1.setTags("sức khỏe sinh sản, phụ khoa, chăm sóc");
        post1.setViews(1250);
        post1.setLikes(89);
        post1.setCommentsCount(12);
        post1.setAuthor(authors.get(0));
        post1.setCategories(new HashSet<>(Arrays.asList(categories.get(0)))); // Sức khỏe sinh sản
        post1.setIsPublished(true);
        post1.setCreatedAt(LocalDateTime.now().minusDays(5));
        post1.setUpdatedAt(LocalDateTime.now().minusDays(5));
        post1.setIsDeleted(false);
        posts.add(post1);
        
        // Post 2: Chu kỳ kinh nguyệt
        BlogPost post2 = new BlogPost();
        post2.setTitle("Chu kỳ kinh nguyệt: Hiểu đúng để chăm sóc tốt hơn");
        post2.setSlug("chu-ky-kinh-nguyet-hieu-dung-de-cham-soc-tot-hon");
        post2.setSummary("Hiểu rõ về chu kỳ kinh nguyệt sẽ giúp bạn chăm sóc sức khỏe tốt hơn.");
        post2.setContent("<h2>Chu kỳ kinh nguyệt là gì?</h2><p>Chu kỳ kinh nguyệt là một hiện tượng sinh lý bình thường của cơ thể phụ nữ. Hiểu rõ về chu kỳ kinh nguyệt sẽ giúp bạn chăm sóc sức khỏe tốt hơn...</p><h3>1. Các giai đoạn của chu kỳ</h3><p>Chu kỳ kinh nguyệt thường kéo dài 28-35 ngày và được chia thành 4 giai đoạn chính...</p><h3>2. Dấu hiệu bất thường</h3><p>Một số dấu hiệu bất thường cần lưu ý như đau bụng dữ dội, rong kinh...</p>");
        post2.setTags("chu kỳ kinh nguyệt, theo dõi, sức khỏe");
        post2.setViews(890);
        post2.setLikes(67);
        post2.setCommentsCount(8);
        post2.setAuthor(authors.get(0));
        post2.setCategories(new HashSet<>(Arrays.asList(categories.get(1)))); // Chu kỳ kinh nguyệt
        post2.setIsPublished(true);
        post2.setCreatedAt(LocalDateTime.now().minusDays(4));
        post2.setUpdatedAt(LocalDateTime.now().minusDays(4));
        post2.setIsDeleted(false);
        posts.add(post2);
        
        // Post 3: Các bệnh lây truyền qua đường tình dục
        BlogPost post3 = new BlogPost();
        post3.setTitle("Các bệnh lây truyền qua đường tình dục: Phòng ngừa và điều trị");
        post3.setSlug("cac-benh-lay-truyen-qua-duong-tinh-duc-phong-ngua-va-dieu-tri");
        post3.setSummary("Thông tin về các bệnh lây truyền qua đường tình dục và cách phòng ngừa.");
        post3.setContent("<h2>STIs là gì?</h2><p>Các bệnh lây truyền qua đường tình dục (STIs) là những bệnh nhiễm trùng có thể lây truyền qua quan hệ tình dục. Bài viết này sẽ cung cấp thông tin về cách phòng ngừa và điều trị...</p><h3>1. Các bệnh STIs phổ biến</h3><p>Chlamydia, Gonorrhea, HPV, HIV là những bệnh STIs phổ biến nhất...</p><h3>2. Cách phòng ngừa</h3><p>Sử dụng bao cao su, khám định kỳ, tiêm vắc-xin là những cách phòng ngừa hiệu quả...</p>");
        post3.setTags("STIs, bệnh lây truyền, phòng ngừa");
        post3.setViews(1560);
        post3.setLikes(123);
        post3.setCommentsCount(15);
        post3.setAuthor(authors.get(0));
        post3.setCategories(new HashSet<>(Arrays.asList(categories.get(5)))); // Bệnh phụ khoa
        post3.setIsPublished(true);
        post3.setCreatedAt(LocalDateTime.now().minusDays(3));
        post3.setUpdatedAt(LocalDateTime.now().minusDays(3));
        post3.setIsDeleted(false);
        posts.add(post3);
        
        // Post 4: Dinh dưỡng cho phụ nữ mang thai
        BlogPost post4 = new BlogPost();
        post4.setTitle("Dinh dưỡng cho phụ nữ mang thai theo từng tháng");
        post4.setSlug("dinh-duong-cho-phu-nu-mang-thai-theo-tung-thang");
        post4.setSummary("Hướng dẫn dinh dưỡng chi tiết cho phụ nữ mang thai theo từng tháng.");
        post4.setContent("<h2>Tầm quan trọng của dinh dưỡng khi mang thai</h2><p>Dinh dưỡng đúng cách trong thai kỳ rất quan trọng cho sự phát triển của thai nhi và sức khỏe của mẹ bầu...</p><h3>1. Tam cá nguyệt đầu tiên</h3><p>Trong 3 tháng đầu, mẹ bầu cần bổ sung axit folic, sắt và canxi...</p><h3>2. Tam cá nguyệt thứ hai</h3><p>Giai đoạn này cần tăng cường protein và omega-3...</p>");
        post4.setTags("dinh dưỡng, mang thai, mẹ bầu");
        post4.setViews(980);
        post4.setLikes(76);
        post4.setCommentsCount(9);
        post4.setAuthor(authors.get(0));
        post4.setCategories(new HashSet<>(Arrays.asList(categories.get(2), categories.get(3)))); // Mang thai & Dinh dưỡng
        post4.setIsPublished(true);
        post4.setCreatedAt(LocalDateTime.now().minusDays(2));
        post4.setUpdatedAt(LocalDateTime.now().minusDays(2));
        post4.setIsDeleted(false);
        posts.add(post4);
        
        // Post 5: Dấu hiệu cảnh báo chu kỳ bất thường
        BlogPost post5 = new BlogPost();
        post5.setTitle("5 dấu hiệu cảnh báo chu kỳ kinh nguyệt bất thường");
        post5.setSlug("5-dau-hieu-canh-bao-chu-ky-kinh-nguyet-bat-thuong");
        post5.setSummary("Nhận biết các dấu hiệu bất thường của chu kỳ kinh nguyệt để kịp thời điều trị.");
        post5.setContent("<h2>Chu kỳ kinh nguyệt bình thường</h2><p>Chu kỳ kinh nguyệt là quá trình sinh lý tự nhiên của cơ thể phụ nữ. Tuy nhiên, có một số dấu hiệu bất thường cần lưu ý...</p><h3>1. Rong kinh kéo dài</h3><p>Khi kinh nguyệt kéo dài hơn 7 ngày, đây có thể là dấu hiệu của bệnh lý...</p><h3>2. Đau bụng dữ dội</h3><p>Đau bụng kinh quá mức có thể là dấu hiệu của lạc nội mạc tử cung...</p>");
        post5.setTags("chu kỳ bất thường, dấu hiệu, cảnh báo");
        post5.setViews(1340);
        post5.setLikes(98);
        post5.setCommentsCount(11);
        post5.setAuthor(authors.get(0));
        post5.setCategories(new HashSet<>(Arrays.asList(categories.get(1)))); // Chu kỳ kinh nguyệt
        post5.setIsPublished(true);
        post5.setCreatedAt(LocalDateTime.now().minusDays(1));
        post5.setUpdatedAt(LocalDateTime.now().minusDays(1));
        post5.setIsDeleted(false);
        posts.add(post5);
        
        // Post 6: Tư vấn tâm lý sau sinh
        BlogPost post6 = new BlogPost();
        post6.setTitle("Tư vấn tâm lý cho phụ nữ sau sinh");
        post6.setSlug("tu-van-tam-ly-cho-phu-nu-sau-sinh");
        post6.setSummary("Hỗ trợ tâm lý cho phụ nữ trong giai đoạn sau sinh.");
        post6.setContent("<h2>Trầm cảm sau sinh</h2><p>Sau khi sinh con, nhiều phụ nữ có thể gặp phải các vấn đề về tâm lý như trầm cảm sau sinh, lo lắng, stress...</p><h3>1. Dấu hiệu trầm cảm sau sinh</h3><p>Cảm thấy buồn bã kéo dài, mất hứng thú với mọi thứ, khó ngủ...</p><h3>2. Cách vượt qua</h3><p>Chia sẻ với người thân, tìm kiếm sự hỗ trợ từ chuyên gia tâm lý...</p>");
        post6.setTags("tâm lý, sau sinh, trầm cảm");
        post6.setViews(720);
        post6.setLikes(54);
        post6.setCommentsCount(6);
        post6.setAuthor(authors.get(0));
        post6.setCategories(new HashSet<>(Arrays.asList(categories.get(4)))); // Tâm lý & Tinh thần
        post6.setIsPublished(false); // Draft
        post6.setCreatedAt(LocalDateTime.now());
        post6.setUpdatedAt(LocalDateTime.now());
        post6.setIsDeleted(false);
        posts.add(post6);
        
        blogPostRepository.saveAll(posts);
        log.info("✅ Đã tạo {} blog posts", blogPostRepository.count());
    }
}

