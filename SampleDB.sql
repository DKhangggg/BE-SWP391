-- =====================================================
-- SAMPLE DATA FOR HS_New DATABASE
-- Based on NewstDB.sql schema
-- =====================================================

USE [HS_New]
GO

-- =====================================================
-- 1. USERS DATA
-- =====================================================

INSERT INTO [Users] (Username, PasswordHash, Email, FullName, PhoneNumber, RoleName, Description, DateOfBirth, Address, Gender, MedicalHistory, CreatedAt, UpdatedAt, IsDeleted) VALUES
-- Admin Users
('admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'admin@gynexa.com', N'Quản trị viên hệ thống', '0901234567', 'ROLE_ADMIN', N'System administrator', '1985-01-15', N'Quận 1, TP.HCM', N'Male', NULL, GETDATE(), GETDATE(), 0),
('manager', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'manager@gynexa.com', N'Nguyễn Văn Manager', '0901234568', 'ROLE_MANAGER', N'Healthcare manager', '1982-03-20', N'Quận 3, TP.HCM', N'Male', NULL, GETDATE(), GETDATE(), 0),

-- Customer Users
('user001', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'nguyen.lan@email.com', N'Nguyễn Thị Lan', '0987654321', 'ROLE_CUSTOMER', N'Healthcare customer', '1995-06-10', N'Quận 10, TP.HCM', N'Female', N'Không có tiền sử bệnh tật đặc biệt', GETDATE(), GETDATE(), 0),
('user002', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'tran.mai@email.com', N'Trần Thị Mai', '0123456789', 'ROLE_CUSTOMER', N'Healthcare customer', '1993-04-22', N'Quận 7, TP.HCM', N'Female', N'Có tiền sử viêm phụ khoa', GETDATE(), GETDATE(), 0),
('user003', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'le.hoa@email.com', N'Lê Thị Hoa', '0909123456', 'ROLE_CUSTOMER', N'Healthcare customer', '1998-12-05', N'Quận Bình Thạnh, TP.HCM', N'Female', N'Chu kỳ kinh nguyệt không đều', GETDATE(), GETDATE(), 0),
('user004', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'pham.yen@email.com', N'Phạm Thị Yến', '0912345678', 'ROLE_CUSTOMER', N'Healthcare customer', '1996-08-14', N'Quận Gò Vấp, TP.HCM', N'Female', N'Đau bụng kinh mỗi tháng', GETDATE(), GETDATE(), 0),
('user005', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'vo.linh@email.com', N'Võ Thị Linh', '0934567890', 'ROLE_CUSTOMER', N'Healthcare customer', '1992-11-30', N'Quận Tân Bình, TP.HCM', N'Female', N'Stress và lo âu', GETDATE(), GETDATE(), 0),

-- Consultant Users  
('consultant001', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'dr.huong@gynexa.com', N'BS. Nguyễn Thị Hương', '0901111111', 'ROLE_CONSULTANT', N'Obstetrics and Gynecology specialist', '1980-02-15', N'Quận 1, TP.HCM', N'Female', NULL, GETDATE(), GETDATE(), 0),
('consultant002', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'dr.linh@gynexa.com', N'BS. Trần Văn Linh', '0902222222', 'ROLE_CONSULTANT', N'Reproductive Endocrinology specialist', '1978-07-20', N'Quận 3, TP.HCM', N'Male', NULL, GETDATE(), GETDATE(), 0),
('consultant003', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'dr.minh@gynexa.com', N'BS. Lê Thị Minh', '0903333333', 'ROLE_CONSULTANT', N'Women mental health specialist', '1983-09-12', N'Quận 5, TP.HCM', N'Female', NULL, GETDATE(), GETDATE(), 0),

-- Staff Users
('staff001', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'staff1@gynexa.com', N'Nguyễn Văn Staff', '0904444444', 'ROLE_STAFF', N'Healthcare staff', '1990-05-25', N'Quận 2, TP.HCM', N'Male', NULL, GETDATE(), GETDATE(), 0);

-- =====================================================
-- 2. CONSULTANTS DATA
-- =====================================================

INSERT INTO [Consultants] (ConsultantID, Biography, Qualifications, ExperienceYears, Specialization, IsDeleted) VALUES
((SELECT UserID FROM Users WHERE Username = 'consultant001'), 
 N'Bác sĩ chuyên khoa Sản Phụ khoa với hơn 10 năm kinh nghiệm. Chuyên về tư vấn sức khỏe sinh sản, theo dõi thai kỳ và điều trị các bệnh phụ khoa.',
 N'Đại học Y khoa TP.HCM, Chuyên khoa cấp I Sản Phụ khoa, Chứng chỉ hành nghề y khoa',
 10, N'Sản Phụ khoa', 0),

((SELECT UserID FROM Users WHERE Username = 'consultant002'), 
 N'Bác sĩ chuyên khoa Nội tiết - Sinh sản với 8 năm kinh nghiệm. Chuyên điều trị rối loạn nội tiết tố, vô sinh hiếm muộn.',
 N'Đại học Y Dược TP.HCM, Thạc sĩ Y học, Chứng chỉ chuyên khoa Nội tiết',
 8, N'Nội tiết - Sinh sản', 0),

((SELECT UserID FROM Users WHERE Username = 'consultant003'), 
 N'Bác sĩ chuyên về tâm lý và sức khỏe tinh thần phụ nữ. Có kinh nghiệm tư vấn về các vấn đề tâm lý trong thời kỳ mang thai.',
 N'Đại học Y khoa Hà Nội, Chứng chỉ tâm lý lâm sàng, Chứng chỉ hành nghề y khoa',
 6, N'Tâm lý sức khỏe phụ nữ', 0);

-- =====================================================
-- 3. TESTING SERVICES DATA
-- =====================================================

INSERT INTO [TestingServices] (ServiceName, Description, Price, Status, DurationMinutes, CreatedAt, UpdatedAt, IsDeleted) VALUES
(N'Xét nghiệm hormone sinh sản', N'Xét nghiệm đầy đủ các hormone sinh sản bao gồm FSH, LH, Estradiol, Progesterone, Testosterone, AMH để đánh giá chức năng buồng trứng và khả năng sinh sản.', 850000, 'ACTIVE', 120, GETDATE(), GETDATE(), 0),
(N'Tầm soát ung thư cổ tử cung (Pap test)', N'Xét nghiệm tế bào học cổ tử cung để phát hiện sớm các thay đổi tế bào có thể dẫn đến ung thư cổ tử cung. Bao gồm cả xét nghiệm HPV.', 450000, 'ACTIVE', 30, GETDATE(), GETDATE(), 0),
(N'Siêu âm tử cung buồng trứng', N'Siêu âm đánh giá cấu trúc và chức năng của tử cung, buồng trứng, phát hiện các khối u, nang, polyp và các bất thường khác.', 320000, 'ACTIVE', 45, GETDATE(), GETDATE(), 0),
(N'Xét nghiệm nhiễm khuẩn sinh dục', N'Xét nghiệm toàn diện các tác nhân gây nhiễm khuẩn đường sinh dục bao gồm Chlamydia, Gonorrhea, Trichomonas, Candida và các vi khuẩn khác.', 680000, 'ACTIVE', 90, GETDATE(), GETDATE(), 0),
(N'Xét nghiệm vitamin và khoáng chất', N'Đánh giá mức độ vitamin D, B12, Folate, sắt, canxi và các vi chất dinh dưỡng quan trọng cho sức khỏe phụ nữ.', 520000, 'ACTIVE', 60, GETDATE(), GETDATE(), 0),
(N'Xét nghiệm tiền mãn kinh', N'Bộ xét nghiệm chuyên biệt đánh giá tình trạng tiền mãn kinh và mãn kinh bao gồm FSH, Estradiol, và các chỉ số liên quan.', 420000, 'ACTIVE', 75, GETDATE(), GETDATE(), 0);

-- =====================================================
-- 4. TIME SLOTS DATA
-- =====================================================

INSERT INTO [TimeSlots] (SlotNumber, StartTime, EndTime, Duration, Description, IsActive, CreatedAt, IsDeleted) VALUES
(1, '08:00:00', '08:30:00', 30, N'Slot sáng 1', 1, GETDATE(), 0),
(2, '08:30:00', '09:00:00', 30, N'Slot sáng 2', 1, GETDATE(), 0),
(3, '09:00:00', '09:30:00', 30, N'Slot sáng 3', 1, GETDATE(), 0),
(4, '09:30:00', '10:00:00', 30, N'Slot sáng 4', 1, GETDATE(), 0),
(5, '10:00:00', '10:30:00', 30, N'Slot sáng 5', 1, GETDATE(), 0),
(6, '10:30:00', '11:00:00', 30, N'Slot sáng 6', 1, GETDATE(), 0),
(7, '14:00:00', '14:30:00', 30, N'Slot chiều 1', 1, GETDATE(), 0),
(8, '14:30:00', '15:00:00', 30, N'Slot chiều 2', 1, GETDATE(), 0),
(9, '15:00:00', '15:30:00', 30, N'Slot chiều 3', 1, GETDATE(), 0),
(10, '15:30:00', '16:00:00', 30, N'Slot chiều 4', 1, GETDATE(), 0),
(11, '16:00:00', '16:30:00', 30, N'Slot chiều 5', 1, GETDATE(), 0),
(12, '16:30:00', '17:00:00', 30, N'Slot chiều 6', 1, GETDATE(), 0);

-- =====================================================
-- 5. CONSULTANT AVAILABILITIES DATA
-- =====================================================

INSERT INTO [ConsultantAvailabilities] (ConsultantID, TimeSlotID, DayOfWeek, IsAvailable, MaxBookings, Notes, CreatedAt, UpdatedAt, IsDeleted) VALUES
-- Consultant 1 availability
((SELECT ConsultantID FROM Consultants WHERE ConsultantID = (SELECT UserID FROM Users WHERE Username = 'consultant001')), 1, 'MONDAY', 1, 2, N'Khám sáng thứ 2', GETDATE(), GETDATE(), 0),
((SELECT ConsultantID FROM Consultants WHERE ConsultantID = (SELECT UserID FROM Users WHERE Username = 'consultant001')), 2, 'MONDAY', 1, 2, N'Khám sáng thứ 2', GETDATE(), GETDATE(), 0),
((SELECT ConsultantID FROM Consultants WHERE ConsultantID = (SELECT UserID FROM Users WHERE Username = 'consultant001')), 7, 'MONDAY', 1, 2, N'Khám chiều thứ 2', GETDATE(), GETDATE(), 0),
((SELECT ConsultantID FROM Consultants WHERE ConsultantID = (SELECT UserID FROM Users WHERE Username = 'consultant001')), 8, 'MONDAY', 1, 2, N'Khám chiều thứ 2', GETDATE(), GETDATE(), 0),
((SELECT ConsultantID FROM Consultants WHERE ConsultantID = (SELECT UserID FROM Users WHERE Username = 'consultant001')), 1, 'TUESDAY', 1, 2, N'Khám sáng thứ 3', GETDATE(), GETDATE(), 0),
((SELECT ConsultantID FROM Consultants WHERE ConsultantID = (SELECT UserID FROM Users WHERE Username = 'consultant001')), 2, 'TUESDAY', 1, 2, N'Khám sáng thứ 3', GETDATE(), GETDATE(), 0),

-- Consultant 2 availability 
((SELECT ConsultantID FROM Consultants WHERE ConsultantID = (SELECT UserID FROM Users WHERE Username = 'consultant002')), 3, 'MONDAY', 1, 1, N'Khám sáng thứ 2', GETDATE(), GETDATE(), 0),
((SELECT ConsultantID FROM Consultants WHERE ConsultantID = (SELECT UserID FROM Users WHERE Username = 'consultant002')), 4, 'MONDAY', 1, 1, N'Khám sáng thứ 2', GETDATE(), GETDATE(), 0),
((SELECT ConsultantID FROM Consultants WHERE ConsultantID = (SELECT UserID FROM Users WHERE Username = 'consultant002')), 9, 'MONDAY', 1, 1, N'Khám chiều thứ 2', GETDATE(), GETDATE(), 0),
((SELECT ConsultantID FROM Consultants WHERE ConsultantID = (SELECT UserID FROM Users WHERE Username = 'consultant002')), 10, 'MONDAY', 1, 1, N'Khám chiều thứ 2', GETDATE(), GETDATE(), 0),

-- Consultant 3 availability 
((SELECT ConsultantID FROM Consultants WHERE ConsultantID = (SELECT UserID FROM Users WHERE Username = 'consultant003')), 7, 'WEDNESDAY', 1, 1, N'Tư vấn tâm lý thứ 4', GETDATE(), GETDATE(), 0),
((SELECT ConsultantID FROM Consultants WHERE ConsultantID = (SELECT UserID FROM Users WHERE Username = 'consultant003')), 8, 'WEDNESDAY', 1, 1, N'Tư vấn tâm lý thứ 4', GETDATE(), GETDATE(), 0),
((SELECT ConsultantID FROM Consultants WHERE ConsultantID = (SELECT UserID FROM Users WHERE Username = 'consultant003')), 9, 'WEDNESDAY', 1, 1, N'Tư vấn tâm lý thứ 4', GETDATE(), GETDATE(), 0);

-- =====================================================
-- 6. CONSULTANT SCHEDULES DATA
-- =====================================================

INSERT INTO [ConsultantSchedules] (ConsultantID, ScheduleDate, TimeSlotID, Status, Notes, CreatedAt, IsDeleted) VALUES
-- Consultant 1 schedules
((SELECT ConsultantID FROM Consultants WHERE ConsultantID = (SELECT UserID FROM Users WHERE Username = 'consultant001')), '2024-12-16', 1, 'AVAILABLE', N'Sẵn sàng khám', GETDATE(), 0),
((SELECT ConsultantID FROM Consultants WHERE ConsultantID = (SELECT UserID FROM Users WHERE Username = 'consultant001')), '2024-12-16', 2, 'AVAILABLE', N'Sẵn sàng khám', GETDATE(), 0),
((SELECT ConsultantID FROM Consultants WHERE ConsultantID = (SELECT UserID FROM Users WHERE Username = 'consultant001')), '2024-12-17', 7, 'AVAILABLE', N'Sẵn sàng khám', GETDATE(), 0),
((SELECT ConsultantID FROM Consultants WHERE ConsultantID = (SELECT UserID FROM Users WHERE Username = 'consultant001')), '2024-12-17', 8, 'AVAILABLE', N'Sẵn sàng khám', GETDATE(), 0),

-- Consultant 2 schedules
((SELECT ConsultantID FROM Consultants WHERE ConsultantID = (SELECT UserID FROM Users WHERE Username = 'consultant002')), '2024-12-16', 3, 'AVAILABLE', N'Sẵn sàng khám', GETDATE(), 0),
((SELECT ConsultantID FROM Consultants WHERE ConsultantID = (SELECT UserID FROM Users WHERE Username = 'consultant002')), '2024-12-16', 4, 'AVAILABLE', N'Sẵn sàng khám', GETDATE(), 0),

-- Consultant 3 schedules  
((SELECT ConsultantID FROM Consultants WHERE ConsultantID = (SELECT UserID FROM Users WHERE Username = 'consultant003')), '2024-12-18', 7, 'AVAILABLE', N'Tư vấn tâm lý', GETDATE(), 0),
((SELECT ConsultantID FROM Consultants WHERE ConsultantID = (SELECT UserID FROM Users WHERE Username = 'consultant003')), '2024-12-18', 8, 'AVAILABLE', N'Tư vấn tâm lý', GETDATE(), 0);

-- =====================================================
-- 7. BLOG CATEGORIES DATA
-- =====================================================

INSERT INTO [BlogCategories] (CategoryName, Description, CreatedAt, UpdatedAt, IsDeleted) VALUES
(N'Sức khỏe sinh sản', N'Thông tin và lời khuyên về sức khỏe sinh sản phụ nữ', GETDATE(), GETDATE(), 0),
(N'Chu kỳ kinh nguyệt', N'Kiến thức về chu kỳ kinh nguyệt và các vấn đề liên quan', GETDATE(), GETDATE(), 0),
(N'Mang thai & Sinh nở', N'Hướng dẫn và chăm sóc trong thời kỳ mang thai', GETDATE(), GETDATE(), 0),
(N'Dinh dưỡng & Lối sống', N'Dinh dưỡng và lối sống healthy cho phụ nữ', GETDATE(), GETDATE(), 0),
(N'Tâm lý & Tinh thần', N'Chăm sóc sức khỏe tinh thần và tâm lý phụ nữ', GETDATE(), GETDATE(), 0),
(N'Bệnh phụ khoa', N'Thông tin về các bệnh phụ khoa thường gặp', GETDATE(), GETDATE(), 0);

-- =====================================================
-- 8. BLOG POSTS DATA
-- =====================================================

INSERT INTO [BlogPosts] (Title, Content, AuthorID, CategoryID, PublishedAt, IsPublished, IsDeleted, UpdatedAt, CreatedAt) VALUES
(N'5 dấu hiệu cảnh báo chu kỳ kinh nguyệt bất thường', 
 N'<h2>Chu kỳ kinh nguyệt là gì?</h2><p>Chu kỳ kinh nguyệt là quá trình sinh lý tự nhiên xảy ra hàng tháng ở phụ nữ trong độ tuổi sinh sản...</p><h2>5 dấu hiệu cần chú ý</h2><p><strong>1. Chu kỳ không đều:</strong> Khoảng cách giữa các lần kinh nguyệt thay đổi liên tục...</p>', 
 (SELECT UserID FROM Users WHERE Username = 'consultant001'),
 (SELECT CategoryID FROM BlogCategories WHERE CategoryName = N'Chu kỳ kinh nguyệt'),
 GETDATE(), 1, 0, GETDATE(), GETDATE()),

(N'Dinh dưỡng cho phụ nữ mang thai theo từng tháng', 
 N'<h2>Tầm quan trọng của dinh dưỡng khi mang thai</h2><p>Dinh dưỡng đúng cách trong thời kỳ mang thai không chỉ giúp mẹ khỏe mạnh mà còn đảm bảo sự phát triển toàn diện của thai nhi...</p>', 
 (SELECT UserID FROM Users WHERE Username = 'consultant002'),
 (SELECT CategoryID FROM BlogCategories WHERE CategoryName = N'Mang thai & Sinh nở'),
 GETDATE(), 1, 0, GETDATE(), GETDATE()),

(N'Cách quản lý stress và lo âu ở phụ nữ', 
 N'<h2>Stress ảnh hưởng đến sức khỏe phụ nữ như thế nào?</h2><p>Stress kéo dài có thể gây ra nhiều vấn đề sức khỏe nghiêm trọng ở phụ nữ...</p>', 
 (SELECT UserID FROM Users WHERE Username = 'consultant003'),
 (SELECT CategoryID FROM BlogCategories WHERE CategoryName = N'Tâm lý & Tinh thần'),
 GETDATE(), 1, 0, GETDATE(), GETDATE());

-- =====================================================
-- 9. BLOG POST CATEGORIES (many-to-many table)
-- =====================================================

INSERT INTO [BlogPost_Categories] (post_id, category_id) VALUES
((SELECT TOP 1 PostID FROM BlogPosts WHERE Title LIKE N'5 dấu hiệu%'), (SELECT CategoryID FROM BlogCategories WHERE CategoryName = N'Chu kỳ kinh nguyệt')),
((SELECT TOP 1 PostID FROM BlogPosts WHERE Title LIKE N'Dinh dưỡng%'), (SELECT CategoryID FROM BlogCategories WHERE CategoryName = N'Mang thai & Sinh nở')),
((SELECT TOP 1 PostID FROM BlogPosts WHERE Title LIKE N'Cách quản lý stress%'), (SELECT CategoryID FROM BlogCategories WHERE CategoryName = N'Tâm lý & Tinh thần'));

-- =====================================================
-- 10. QUESTIONS DATA
-- =====================================================

INSERT INTO [Questions] (user_id, category, content, status, is_public, created_at, updated_at, isDeleted) VALUES
((SELECT UserID FROM Users WHERE Username = 'user001'), N'Chu kỳ kinh nguyệt', 
 N'Em 23 tuổi, chu kỳ kinh nguyệt của em thường từ 28-35 ngày, có khi 40 ngày mới có kinh. Em có cần lo lắng không ạ?', 
 'ANSWERED', 1, GETDATE(), GETDATE(), 0),

((SELECT UserID FROM Users WHERE Username = 'user002'), N'Chu kỳ kinh nguyệt', 
 N'Mỗi lần đến kỳ kinh, em đều bị đau bụng rất nhiều. Em muốn hỏi có cách nào giảm đau tự nhiên không ạ?', 
 'ANSWERED', 1, GETDATE(), GETDATE(), 0),

((SELECT UserID FROM Users WHERE Username = 'user003'), N'Sức khỏe sinh sản', 
 N'Chồng em muốn có con nên em muốn hỏi nên ăn gì để tăng cường khả năng sinh sản?', 
 'ANSWERED', 1, GETDATE(), GETDATE(), 0),

((SELECT UserID FROM Users WHERE Username = 'user004'), N'Bệnh phụ khoa', 
 N'Em bị ngứa và tiết dịch màu trắng đặc. Em nghĩ có thể bị nhiễm nấm âm đạo. Cho em hỏi cách điều trị?', 
 'ANSWERED', 1, GETDATE(), GETDATE(), 0),

((SELECT UserID FROM Users WHERE Username = 'user005'), N'Tâm lý & Tinh thần', 
 N'Dạo này em stress công việc nhiều và thấy kinh nguyệt đến muộn. Stress có thể làm rối loạn kinh nguyệt không ạ?', 
 'PENDING', 0, GETDATE(), GETDATE(), 0);

-- =====================================================
-- 11. ANSWERS DATA
-- =====================================================

INSERT INTO [Answers] (question_id, consultant_id, content, created_at, updated_at, isDeleted) VALUES
((SELECT TOP 1 id FROM Questions WHERE content LIKE N'%chu kỳ kinh nguyệt%28-35 ngày%'),
 (SELECT ConsultantID FROM Consultants WHERE ConsultantID = (SELECT UserID FROM Users WHERE Username = 'consultant001')),
 N'Chu kỳ kinh nguyệt từ 21-35 ngày đều được coi là bình thường. Tuy nhiên, nếu chu kỳ của bạn thay đổi đột ngột hoặc có các triệu chứng bất thường khác, bạn nên đi khám để được tư vấn cụ thể.',
 GETDATE(), GETDATE(), 0),

((SELECT TOP 1 id FROM Questions WHERE content LIKE N'%đau bụng rất nhiều%'),
 (SELECT ConsultantID FROM Consultants WHERE ConsultantID = (SELECT UserID FROM Users WHERE Username = 'consultant001')),
 N'Để giảm đau bụng kinh tự nhiên: 1) Chườm ấm vùng bụng, 2) Tập yoga nhẹ, 3) Massage nhẹ, 4) Uống trà gừng, 5) Bổ sung magnesium và omega-3. Nếu đau quá nhiều, hãy đi khám để loại trừ các bệnh lý.',
 GETDATE(), GETDATE(), 0),

((SELECT TOP 1 id FROM Questions WHERE content LIKE N'%tăng cường khả năng sinh sản%'),
 (SELECT ConsultantID FROM Consultants WHERE ConsultantID = (SELECT UserID FROM Users WHERE Username = 'consultant002')),
 N'Để tăng cường khả năng sinh sản: 1) Bổ sung axit folic, 2) Ăn nhiều rau xanh và protein, 3) Hạn chế caffeine, 4) Duy trì cân nặng khỏe mạnh, 5) Tập thể dục đều đặn, 6) Quản lý stress tốt.',
 GETDATE(), GETDATE(), 0),

((SELECT TOP 1 id FROM Questions WHERE content LIKE N'%nhiễm nấm âm đạo%'),
 (SELECT ConsultantID FROM Consultants WHERE ConsultantID = (SELECT UserID FROM Users WHERE Username = 'consultant001')),
 N'Triệu chứng bạn mô tả có thể là nhiễm nấm âm đạo. Để điều trị: sử dụng thuốc chống nấm theo đơn bác sĩ, giữ vùng kín sạch khô, mặc đồ lót cotton. Bạn nên đi khám để được chẩn đoán chính xác.',
 GETDATE(), GETDATE(), 0);

-- =====================================================
-- 12. SYMPTOMS DATA
-- =====================================================

INSERT INTO [Symptoms] (SymptomName, Category, Description, IsActive, CreatedAt) VALUES
(N'Đau bụng kinh', N'Kinh nguyệt', N'Cơn đau xuất hiện trước và trong kỳ kinh nguyệt', 1, GETDATE()),
(N'Đau đầu', N'Thần kinh', N'Cơn đau đầu có thể liên quan đến chu kỳ hormone', 1, GETDATE()),
(N'Buồn nôn', N'Tiêu hóa', N'Cảm giác buồn nôn, có thể do thay đổi hormone', 1, GETDATE()),
(N'Mệt mỏi', N'Toàn thân', N'Cảm giác mệt mỏi, thiếu năng lượng', 1, GETDATE()),
(N'Căng tức ngực', N'Ngực', N'Cảm giác căng tức, đau ngực trước kỳ kinh', 1, GETDATE()),
(N'Thay đổi tâm trạng', N'Tâm lý', N'Cáu gắt, buồn chán, lo lắng', 1, GETDATE()),
(N'Tăng cân', N'Toàn thân', N'Tăng cân tạm thời do tích nước', 1, GETDATE()),
(N'Mụn trứng cá', N'Da', N'Mụn xuất hiện nhiều hơn trước kỳ kinh', 1, GETDATE());

-- =====================================================
-- 13. MENSTRUAL CYCLES DATA
-- =====================================================

INSERT INTO [MenstrualCycles] (UserID, StartDate, CycleLength, CreatedAt, IsDeleted, UpdatedAt, periodDay, AverageCycleLength, PeriodDuration, AveragePeriodDuration, IsRegular, NextPredictedPeriod, FertilityWindowStart, FertilityWindowEnd, OvulationDate) VALUES
((SELECT UserID FROM Users WHERE Username = 'user001'), '2024-11-10', 30, GETDATE(), 0, GETDATE(), '2024-11-10', 30.0, 5, 5.0, 1, '2024-12-10', '2024-11-22 00:00:00', '2024-11-27 00:00:00', '2024-11-24'),
((SELECT UserID FROM Users WHERE Username = 'user002'), '2024-11-15', 28, GETDATE(), 0, GETDATE(), '2024-11-15', 28.0, 4, 4.0, 1, '2024-12-13', '2024-11-27 00:00:00', '2024-12-01 00:00:00', '2024-11-29'),
((SELECT UserID FROM Users WHERE Username = 'user003'), '2024-11-08', 32, GETDATE(), 0, GETDATE(), '2024-11-08', 32.0, 6, 6.0, 0, '2024-12-10', '2024-11-24 00:00:00', '2024-11-29 00:00:00', '2024-11-26'),
((SELECT UserID FROM Users WHERE Username = 'user004'), '2024-11-12', 29, GETDATE(), 0, GETDATE(), '2024-11-12', 29.0, 5, 5.0, 1, '2024-12-11', '2024-11-26 00:00:00', '2024-12-01 00:00:00', '2024-11-28'),
((SELECT UserID FROM Users WHERE Username = 'user005'), '2024-11-14', 27, GETDATE(), 0, GETDATE(), '2024-11-14', 27.0, 4, 4.0, 1, '2024-12-11', '2024-11-24 00:00:00', '2024-11-29 00:00:00', '2024-11-27');

-- =====================================================
-- 14. MENSTRUAL LOGS DATA
-- =====================================================

INSERT INTO [MenstrualLogs] (CycleID, LogDate, Notes, CreatedAt, UpdatedAt, IsActualPeriod, FlowIntensity, Mood, Temperature, Symptoms) VALUES
((SELECT TOP 1 CycleID FROM MenstrualCycles WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'user001')), '2024-11-10 08:00:00', N'Ngày đầu kỳ kinh', GETDATE(), GETDATE(), 1, 'MEDIUM', 'NORMAL', 36.5, N'Đau bụng nhẹ'),
((SELECT TOP 1 CycleID FROM MenstrualCycles WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'user001')), '2024-11-11 08:00:00', N'Ngày thứ 2', GETDATE(), GETDATE(), 1, 'HEAVY', 'IRRITATED', 36.7, N'Đau bụng nhiều, mệt mỏi'),
((SELECT TOP 1 CycleID FROM MenstrualCycles WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'user002')), '2024-11-15 08:00:00', N'Bắt đầu kỳ kinh', GETDATE(), GETDATE(), 1, 'LIGHT', 'NORMAL', 36.4, N'Không có triệu chứng đặc biệt'),
((SELECT TOP 1 CycleID FROM MenstrualCycles WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'user003')), '2024-11-08 08:00:00', N'Chu kỳ không đều', GETDATE(), GETDATE(), 1, 'MEDIUM', 'ANXIOUS', 36.8, N'Lo lắng về chu kỳ'),
((SELECT TOP 1 CycleID FROM MenstrualCycles WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'user004')), '2024-11-12 08:00:00', N'Đau bụng nhiều', GETDATE(), GETDATE(), 1, 'HEAVY', 'PAINFUL', 37.0, N'Đau bụng kinh rất nhiều');

-- =====================================================
-- 15. BOOKINGS DATA
-- =====================================================

INSERT INTO [Bookings] (CustomerID, ServiceID, BookingDate, TimeSlotID, Status, Result, ResultDate, CreatedAt, IsDeleted, ScheduleID) VALUES
((SELECT UserID FROM Users WHERE Username = 'user001'), 
 (SELECT ServiceID FROM TestingServices WHERE ServiceName = N'Xét nghiệm hormone sinh sản'), 
 '2024-12-16 08:00:00', 1, 'CONFIRMED', NULL, NULL, GETDATE(), 0, 
 (SELECT TOP 1 ScheduleID FROM ConsultantSchedules WHERE TimeSlotID = 1)),

((SELECT UserID FROM Users WHERE Username = 'user002'), 
 (SELECT ServiceID FROM TestingServices WHERE ServiceName = N'Tầm soát ung thư cổ tử cung (Pap test)'), 
 '2024-12-16 08:30:00', 2, 'CONFIRMED', NULL, NULL, GETDATE(), 0, 
 (SELECT TOP 1 ScheduleID FROM ConsultantSchedules WHERE TimeSlotID = 2)),

((SELECT UserID FROM Users WHERE Username = 'user003'), 
 (SELECT ServiceID FROM TestingServices WHERE ServiceName = N'Siêu âm tử cung buồng trứng'), 
 '2024-12-16 09:00:00', 3, 'PENDING', NULL, NULL, GETDATE(), 0, 
 (SELECT TOP 1 ScheduleID FROM ConsultantSchedules WHERE TimeSlotID = 3));

-- =====================================================
-- 16. CONSULTATIONS DATA
-- =====================================================

INSERT INTO [Consultations] (CustomerID, ConsultantID, ConsultationDate, Status, MeetingLink, Notes, CreatedAt, IsDeleted) VALUES
((SELECT UserID FROM Users WHERE Username = 'user001'), 
 (SELECT UserID FROM Users WHERE Username = 'consultant001'), 
 '2024-12-17 15:00:00', 'COMPLETED', 
 'meet.gynexa.com/room/001', N'Tư vấn về chu kỳ kinh nguyệt', GETDATE(), 0),

((SELECT UserID FROM Users WHERE Username = 'user002'), 
 (SELECT UserID FROM Users WHERE Username = 'consultant003'), 
 '2024-12-18 16:30:00', 'SCHEDULED', 
 'meet.gynexa.com/room/002', N'Tư vấn về stress và sức khỏe tinh thần', GETDATE(), 0),

((SELECT UserID FROM Users WHERE Username = 'user003'), 
 (SELECT UserID FROM Users WHERE Username = 'consultant002'), 
 '2024-12-20 10:00:00', 'SCHEDULED', 
 'meet.gynexa.com/room/003', N'Tư vấn về chuẩn bị mang thai', GETDATE(), 0);

-- =====================================================
-- 17. PAYMENTS DATA
-- =====================================================

INSERT INTO [Payments] (CustomerID, BookingID, ConsultationID, Amount, PaymentMethod, PaymentStatus, TransactionID, PaymentDate, CreatedAt, IsDeleted) VALUES
((SELECT UserID FROM Users WHERE Username = 'user001'), 
 (SELECT TOP 1 BookingID FROM Bookings WHERE CustomerID = (SELECT UserID FROM Users WHERE Username = 'user001')), 
 NULL, 850000, 'VNPAY', 'COMPLETED', 'TXN_20241212_001', GETDATE(), GETDATE(), 0),

((SELECT UserID FROM Users WHERE Username = 'user002'), 
 (SELECT TOP 1 BookingID FROM Bookings WHERE CustomerID = (SELECT UserID FROM Users WHERE Username = 'user002')), 
 NULL, 450000, 'MOMO', 'COMPLETED', 'TXN_20241212_002', GETDATE(), GETDATE(), 0),

((SELECT UserID FROM Users WHERE Username = 'user003'), 
 (SELECT TOP 1 BookingID FROM Bookings WHERE CustomerID = (SELECT UserID FROM Users WHERE Username = 'user003')), 
 NULL, 320000, 'BANK_TRANSFER', 'PENDING', 'TXN_20241212_003', GETDATE(), GETDATE(), 0);

-- =====================================================
-- 18. FEEDBACK DATA
-- =====================================================

INSERT INTO [Feedback] (CustomerID, ConsultantID, ServiceID, Rating, Comment, CreatedAt, IsDeleted) VALUES
((SELECT UserID FROM Users WHERE Username = 'user001'), 
 (SELECT UserID FROM Users WHERE Username = 'consultant001'), 
 (SELECT ServiceID FROM TestingServices WHERE ServiceName = N'Xét nghiệm hormone sinh sản'), 
 5, N'Bác sĩ tư vấn rất tận tâm và chuyên nghiệp. Em cảm thấy rất an tâm sau buổi tư vấn.', GETDATE(), 0),

((SELECT UserID FROM Users WHERE Username = 'user002'), 
 (SELECT UserID FROM Users WHERE Username = 'consultant001'), 
 (SELECT ServiceID FROM TestingServices WHERE ServiceName = N'Tầm soát ung thư cổ tử cung (Pap test)'), 
 4, N'Buổi tư vấn rất hữu ích, bác sĩ giải thích dễ hiểu. Chỉ tiếc là thời gian hơi ngắn.', GETDATE(), 0);

-- =====================================================
-- 19. NOTIFICATIONS DATA
-- =====================================================

INSERT INTO [Notifications] (UserID, message, isRead, link, createdAt) VALUES
((SELECT UserID FROM Users WHERE Username = 'user001'), N'Bạn có lịch khám vào ngày 16/12/2024 lúc 8:00 AM', 0, '/bookings/1', GETDATE()),
((SELECT UserID FROM Users WHERE Username = 'user002'), N'Kết quả xét nghiệm của bạn đã có, vui lòng kiểm tra', 1, '/results/1', GETDATE()),
((SELECT UserID FROM Users WHERE Username = 'user003'), N'Bạn có cuộc tư vấn vào ngày 20/12/2024 lúc 10:00 AM', 0, '/consultations/3', GETDATE()),
((SELECT UserID FROM Users WHERE Username = 'user004'), N'Nhắc nhở: Đã đến thời gian ghi nhật ký kinh nguyệt', 0, '/menstrual-tracking', GETDATE()),
((SELECT UserID FROM Users WHERE Username = 'user005'), N'Câu hỏi của bạn đã được trả lời bởi bác sĩ', 1, '/qa/answers', GETDATE());

-- =====================================================
-- 20. REMINDERS DATA
-- =====================================================

INSERT INTO [Reminders] (UserID, ReminderType, ReminderDate, Message, IsSent, CreatedAt, IsDeleted) VALUES
((SELECT UserID FROM Users WHERE Username = 'user001'), 'APPOINTMENT', '2024-12-16 07:30:00', N'Nhắc nhở: Bạn có lịch khám lúc 8:00 AM hôm nay', 0, GETDATE(), 0),
((SELECT UserID FROM Users WHERE Username = 'user002'), 'MEDICATION', '2024-12-13 08:00:00', N'Đã đến giờ uống thuốc theo đơn bác sĩ', 1, GETDATE(), 0),
((SELECT UserID FROM Users WHERE Username = 'user003'), 'MENSTRUAL_TRACKING', '2024-12-10 20:00:00', N'Nhắc nhở ghi nhật ký chu kỳ kinh nguyệt hôm nay', 1, GETDATE(), 0),
((SELECT UserID FROM Users WHERE Username = 'user004'), 'CONSULTATION', '2024-12-20 09:30:00', N'Nhắc nhở: Bạn có buổi tư vấn lúc 10:00 AM', 0, GETDATE(), 0),
((SELECT UserID FROM Users WHERE Username = 'user005'), 'HEALTH_CHECKUP', '2025-01-15 09:00:00', N'Đã đến thời gian khám sức khỏe định kỳ', 0, GETDATE(), 0);

-- =====================================================
-- 21. TRANSACTION HISTORY DATA
-- =====================================================

INSERT INTO [TransactionHistory] (UserID, ServiceID, BookingID, TransactionDate, Status, Notes, CreatedAt, IsDeleted) VALUES
((SELECT UserID FROM Users WHERE Username = 'user001'), 
 (SELECT ServiceID FROM TestingServices WHERE ServiceName = N'Xét nghiệm hormone sinh sản'),
 (SELECT TOP 1 BookingID FROM Bookings WHERE CustomerID = (SELECT UserID FROM Users WHERE Username = 'user001')),
 GETDATE(), 'COMPLETED', N'Thanh toán thành công qua VNPAY', GETDATE(), 0),

((SELECT UserID FROM Users WHERE Username = 'user002'), 
 (SELECT ServiceID FROM TestingServices WHERE ServiceName = N'Tầm soát ung thư cổ tử cung (Pap test)'),
 (SELECT TOP 1 BookingID FROM Bookings WHERE CustomerID = (SELECT UserID FROM Users WHERE Username = 'user002')),
 GETDATE(), 'COMPLETED', N'Thanh toán thành công qua MOMO', GETDATE(), 0),

((SELECT UserID FROM Users WHERE Username = 'user003'), 
 (SELECT ServiceID FROM TestingServices WHERE ServiceName = N'Siêu âm tử cung buồng trứng'),
 (SELECT TOP 1 BookingID FROM Bookings WHERE CustomerID = (SELECT UserID FROM Users WHERE Username = 'user003')),
 GETDATE(), 'PENDING', N'Đang chờ xác nhận chuyển khoản', GETDATE(), 0);

 -- =====================================================
-- SAMPLE DATA FOR CHAT & CONSULTANT UNAVAILABILITY
-- =====================================================

USE [HS_New]
GO

-- =====================================================
-- CHAT DATA - HỆ THỐNG CHAT HỎI ĐÁP
-- =====================================================

INSERT INTO [Chat] (CustomerID, ConsultantID, QuestionText, AnswerText, Status, CreatedAt, AnsweredAt, IsDeleted) VALUES

-- Cuộc trò chuyện 1: User001 với Consultant001 - Đau bụng kinh
((SELECT UserID FROM Users WHERE Username = 'user001'), 
 (SELECT UserID FROM Users WHERE Username = 'consultant001'), 
 N'Chào bác sĩ ạ! Em 23 tuổi, mỗi tháng khi đến kỳ kinh em đều bị đau bụng rất nhiều. Đau từ 1-2 ngày trước khi có kinh và kéo dài 2-3 ngày đầu. Em phải uống thuốc giảm đau mới chịu được. Bác sĩ cho em hỏi tình trạng này có bình thường không ạ?',
 N'Chào em! Cảm ơn em đã tin tưởng chia sẻ. Tình trạng đau bụng kinh (dysmenorrhea) mà em mô tả là khá phổ biến ở phụ nữ trẻ. Tuy nhiên, nếu đau quá nhiều và ảnh hưởng đến sinh hoạt hàng ngày thì cần được quan tâm. Em có thể thử một số cách giảm đau tự nhiên như: chườm ấm vùng bụng dưới, tập yoga nhẹ, massage bụng, uống trà gừng. Nếu đau không cải thiện, em nên đi khám để loại trừ các bệnh lý như lạc nội mạc tử cung.',
 'ANSWERED', '2024-12-10 14:30:00', '2024-12-10 15:15:00', 0),

-- Cuộc trò chuyện 2: User002 với Consultant003 - Stress và kinh nguyệt
((SELECT UserID FROM Users WHERE Username = 'user002'), 
 (SELECT UserID FROM Users WHERE Username = 'consultant003'), 
 N'Bác sĩ ơi, dạo này em stress công việc rất nhiều. Em làm việc overtime liên tục, ngủ ít, ăn uống không đều. Và em thấy chu kỳ kinh nguyệt của em bị rối loạn. Tháng trước em trễ kinh 10 ngày, tháng này lại đến sớm 5 ngày. Em lo lắng lắm, không biết stress có ảnh hưởng đến kinh nguyệt không ạ?',
 N'Em ơi, stress thực sự có thể ảnh hưởng rất lớn đến chu kỳ kinh nguyệt. Khi stress, cơ thể sản xuất hormone cortisol tăng cao, điều này có thể làm rối loạn trục hạ đồi-tuyến yên-buồng trứng, dẫn đến chu kỳ không đều. Em cần ưu tiên giảm stress: 1) Cố gắng ngủ đủ 7-8 tiếng/ngày, 2) Ăn uống đều đặn và đủ chất, 3) Tập thiền hoặc yoga, 4) Tìm cách cân bằng công việc-cuộc sống. Nếu tình trạng kéo dài, em nên đi khám để được tư vấn cụ thể.',
 'ANSWERED', '2024-12-11 09:20:00', '2024-12-11 10:45:00', 0),

-- Cuộc trò chuyện 3: User003 với Consultant002 - Chuẩn bị mang thai
((SELECT UserID FROM Users WHERE Username = 'user003'), 
 (SELECT UserID FROM Users WHERE Username = 'consultant002'), 
 N'Bác sĩ ơi, em và chồng đang có kế hoạch có con trong năm tới. Em muốn hỏi nên chuẩn bị những gì trước khi mang thai? Em nghe nói phải uống axit folic, vậy uống từ khi nào và liều lượng như thế nào ạ? Còn có gì khác em cần lưu ý không?',
 N'Chúc mừng em và chồng! Việc chuẩn bị trước khi mang thai rất quan trọng. Về axit folic: em nên uống 400-800mcg/ngày, bắt đầu từ ít nhất 1 tháng trước khi có thai và tiếp tục trong 3 tháng đầu thai kỳ. Ngoài ra: 1) Khám sức khỏe tổng quát, 2) Tiêm vaccine cần thiết (rubella, hepatitis B), 3) Kiểm tra nhóm máu và thalassemia, 4) Duy trì cân nặng khỏe mạnh (BMI 18.5-24.9), 5) Tránh rượu bia, thuốc lá, 6) Ăn uống cân bằng, tập thể dục nhẹ. Em có thể đặt lịch khám tiền mang thai để được tư vấn chi tiết hơn.',
 'ANSWERED', '2024-12-12 16:00:00', '2024-12-12 16:30:00', 0),

-- Cuộc trò chuyện 4: User004 - Chưa được trả lời
((SELECT UserID FROM Users WHERE Username = 'user004'), 
 NULL, 
 N'Em muốn hỏi về tình trạng tiết dịch âm đạo bất thường. Gần đây em thấy có tiết dịch màu trắng đặc, có mùi hơi khó chịu và vùng kín bị ngứa. Em đã thử rửa bằng nước muối loãng nhưng không thấy khỏi. Bác sĩ có thể tư vấn giúp em được không ạ?',
 NULL,
 'PENDING', '2024-12-13 11:15:00', NULL, 0),

-- Cuộc trò chuyện 5: User005 với Consultant003 - PMS
((SELECT UserID FROM Users WHERE Username = 'user005'), 
 (SELECT UserID FROM Users WHERE Username = 'consultant003'), 
 N'Bác sĩ ơi, em thấy mỗi tháng trước khi có kinh khoảng 1 tuần, tâm trạng của em thay đổi rất nhiều. Em trở nên cáu gắt, dễ khóc, buồn chán và không muốn làm gì. Ngực em cũng căng tức, đau. Sau khi có kinh thì mọi thứ lại bình thường. Em có bị bệnh gì không ạ?',
 N'Em đang gặp phải hội chứng tiền kinh nguyệt (PMS - Premenstrual Syndrome), đây là tình trạng rất phổ biến ở phụ nữ. Khoảng 75% phụ nữ trải qua PMS với mức độ khác nhau. Để cải thiện: 1) Tập thể dục đều đặn (giúp giải phóng endorphin), 2) Ăn nhiều rau xanh, hạn chế đường và caffeine, 3) Ngủ đủ giấc, 4) Quản lý stress tốt, 5) Có thể bổ sung vitamin B6, magnesium. Nếu triệu chứng quá nặng và ảnh hưởng đến cuộc sống, em nên đi khám để được tư vấn điều trị cụ thể.',
 'ANSWERED', '2024-12-13 20:30:00', '2024-12-13 21:00:00', 0),

-- Cuộc trò chuyện 6: User001 hỏi thêm về xét nghiệm
((SELECT UserID FROM Users WHERE Username = 'user001'), 
 (SELECT UserID FROM Users WHERE Username = 'consultant001'), 
 N'Bác sĩ ơi, em cảm ơn lời tư vấn trước đó. Em muốn hỏi thêm, với tình trạng đau bụng kinh như em, có nên làm xét nghiệm gì để kiểm tra không ạ? Em nghe nói có thể do lạc nội mạc tử cung?',
 N'Em rất khôn ngoan khi quan tâm đến sức khỏe của mình. Với triệu chứng đau bụng kinh nhiều, em có thể cần: 1) Siêu âm tử cung buồng trứng để xem cấu trúc, 2) Xét nghiệm máu CA125 (có thể tăng trong lạc nội mạc tử cung), 3) Khám phụ khoa để đánh giá tổng quát. Nếu cần thiết, bác sĩ có thể chỉ định MRI hoặc nội soi ổ bụng. Em nên đặt lịch khám trực tiếp để được thăm khám và tư vấn cụ thể nhé.',
 'ANSWERED', '2024-12-14 10:00:00', '2024-12-14 10:20:00', 0),

-- Cuộc trò chuyện 7: User002 hỏi về thuốc tránh thai
((SELECT UserID FROM Users WHERE Username = 'user002'), 
 (SELECT UserID FROM Users WHERE Username = 'consultant002'), 
 N'Bác sĩ ơi, em đang dùng thuốc tránh thai hàng ngày để tránh thai. Nhưng em nghe nói thuốc tránh thai có thể gây tác dụng phụ. Em muốn hỏi có nên tiếp tục dùng không? Và có phương pháp tránh thai nào khác an toàn hơn không ạ?',
 N'Thuốc tránh thai kết hợp có hiệu quả tránh thai rất cao (>99%) khi dùng đúng cách. Tác dụng phụ có thể gặp: buồn nôn, căng ngực, thay đổi tâm trạng, tăng cân nhẹ. Tuy nhiên, thuốc cũng có lợi ích: giảm đau bụng kinh, điều hòa chu kỳ, giảm nguy cơ ung thư buồng trứng. Các phương pháp khác: que tránh thai, vòng tránh thai, bao cao su. Mỗi phương pháp có ưu nhược điểm riêng. Em nên đặt lịch tư vấn trực tiếp để được tư vấn phù hợp với tình trạng sức khỏe cụ thể.',
 'ANSWERED', '2024-12-14 14:30:00', '2024-12-14 15:00:00', 0),

-- Cuộc trò chuyện 8: User003 hỏi về vitamin
((SELECT UserID FROM Users WHERE Username = 'user003'), 
 (SELECT UserID FROM Users WHERE Username = 'consultant002'), 
 N'Bác sĩ ơi, ngoài axit folic, em có cần bổ sung vitamin gì khác khi chuẩn bị mang thai không? Em thấy trên mạng có nhiều loại vitamin cho bà bầu, nhưng em chưa có thai thì có nên uống không ạ?',
 N'Khi chuẩn bị mang thai, ngoài axit folic, em có thể cần: 1) Vitamin D (2000-4000 IU/ngày), 2) Sắt (nếu thiếu máu), 3) Omega-3 DHA (200-300mg/ngày), 4) Iodine (150mcg/ngày), 5) Vitamin B12 (đặc biệt quan trọng nếu em ăn chay). Tuy nhiên, em nên xét nghiệm máu trước để biết cơ thể thiếu gì rồi bổ sung có mục tiêu. Vitamin tổng hợp cho phụ nữ chuẩn bị mang thai cũng là lựa chọn tốt. Quan trọng là chọn sản phẩm uy tín và tham khảo ý kiến bác sĩ.',
 'ANSWERED', '2024-12-15 09:45:00', '2024-12-15 10:15:00', 0),

-- Cuộc trò chuyện 9: User005 hỏi về tập thể dục
((SELECT UserID FROM Users WHERE Username = 'user005'), 
 (SELECT UserID FROM Users WHERE Username = 'consultant003'), 
 N'Bác sĩ ơi, em muốn hỏi về việc tập thể dục để cải thiện PMS. Em nên tập loại hình nào và tần suất như thế nào? Em chưa có kinh nghiệm tập thể dục nhiều nên không biết bắt đầu từ đâu.',
 N'Tập thể dục thực sự rất hiệu quả trong việc giảm triệu chứng PMS. Em có thể bắt đầu với: 1) Đi bộ nhanh 30 phút/ngày, 2) Yoga (đặc biệt tốt cho PMS), 3) Bơi lội, 4) Tập aerobic nhẹ. Tần suất: 3-4 lần/tuần, mỗi lần 30-45 phút. Trong thời kỳ PMS, em có thể tập nhẹ hơn như yoga, đi bộ. Quan trọng là tập đều đặn và lâu dài. Em nên bắt đầu từ từ, tăng cường độ dần để cơ thể thích nghi. Nếu có điều kiện, em có thể tham gia lớp yoga hoặc gym có huấn luyện viên.',
 'ANSWERED', '2024-12-15 18:00:00', '2024-12-15 18:30:00', 0),

-- Cuộc trò chuyện 10: User004 hỏi về nhiễm trùng (chưa trả lời)
((SELECT UserID FROM Users WHERE Username = 'user004'), 
 NULL, 
 N'Em muốn hỏi thêm về việc phòng ngừa nhiễm trùng phụ khoa. Em nên làm gì để tránh bị nhiễm trùng? Và có nên rửa âm đạo bằng dung dịch vệ sinh phụ nữ hàng ngày không ạ?',
 NULL,
 'PENDING', '2024-12-16 08:30:00', NULL, 0);

-- =====================================================
-- CONSULTANT UNAVAILABILITY DATA - LỊCH NGHỈ CỦA BÁC SĨ
-- =====================================================

INSERT INTO [consultant_unavailability] (consultant_id, reason, createDate, status, start_time, end_time) VALUES

-- Consultant001 - BS. Nguyễn Thị Hương
((SELECT UserID FROM Users WHERE Username = 'consultant001'), 
 N'Tham gia Hội thảo Quốc tế về Sản Phụ khoa 2024 tại TP.HCM', 
 '2024-12-01 10:00:00', 'APPROVED', 
 '2024-12-19 07:00:00', '2024-12-19 18:00:00'),

((SELECT UserID FROM Users WHERE Username = 'consultant001'), 
 N'Nghỉ lễ Giáng sinh cùng gia đình', 
 '2024-12-10 09:00:00', 'APPROVED', 
 '2024-12-24 00:00:00', '2024-12-25 23:59:59'),

((SELECT UserID FROM Users WHERE Username = 'consultant001'), 
 N'Khám bệnh tại Bệnh viện Từ Dũ (ca phẫu thuật)', 
 '2024-12-12 14:00:00', 'APPROVED', 
 '2024-12-22 13:00:00', '2024-12-22 17:00:00'),

((SELECT UserID FROM Users WHERE Username = 'consultant001'), 
 N'Tham gia khóa đào tạo "Chăm sóc sức khỏe sinh sản hiện đại"', 
 '2024-12-15 11:00:00', 'PENDING', 
 '2024-12-28 08:00:00', '2024-12-28 17:00:00'),

-- Consultant002 - BS. Trần Văn Linh  
((SELECT UserID FROM Users WHERE Username = 'consultant002'), 
 N'Nghỉ phép thường niên - du lịch cùng gia đình', 
 '2024-12-05 16:00:00', 'APPROVED', 
 '2024-12-25 00:00:00', '2024-12-27 23:59:59'),

((SELECT UserID FROM Users WHERE Username = 'consultant002'), 
 N'Tham gia Hội nghị Nội tiết Sinh sản Việt Nam', 
 '2024-12-08 09:30:00', 'APPROVED', 
 '2024-12-21 08:00:00', '2024-12-21 17:30:00'),

((SELECT UserID FROM Users WHERE Username = 'consultant002'), 
 N'Nghỉ ốm - cảm cúm', 
 '2024-12-14 07:00:00', 'APPROVED', 
 '2024-12-15 00:00:00', '2024-12-15 23:59:59'),

((SELECT UserID FROM Users WHERE Username = 'consultant002'), 
 N'Tham gia nghiên cứu khoa học tại Đại học Y Dược TP.HCM', 
 '2024-12-16 10:00:00', 'PENDING', 
 '2024-12-30 09:00:00', '2024-12-30 16:00:00'),

-- Consultant003 - BS. Lê Thị Minh
((SELECT UserID FROM Users WHERE Username = 'consultant003'), 
 N'Tham gia Khóa đào tạo Tâm lý lâm sàng nâng cao', 
 '2024-12-03 08:00:00', 'APPROVED', 
 '2024-12-20 08:00:00', '2024-12-20 17:00:00'),

((SELECT UserID FROM Users WHERE Username = 'consultant003'), 
 N'Nghỉ phép để chăm sóc con nhỏ bị ốm', 
 '2024-12-11 07:30:00', 'APPROVED', 
 '2024-12-17 00:00:00', '2024-12-17 23:59:59'),

((SELECT UserID FROM Users WHERE Username = 'consultant003'), 
 N'Tham gia Hội thảo "Sức khỏe tâm thần phụ nữ sau sinh"', 
 '2024-12-13 15:00:00', 'APPROVED', 
 '2024-12-23 08:30:00', '2024-12-23 16:30:00'),

((SELECT UserID FROM Users WHERE Username = 'consultant003'), 
 N'Nghỉ lễ Tết Dương lịch', 
 '2024-12-20 12:00:00', 'PENDING', 
 '2024-12-31 00:00:00', '2025-01-01 23:59:59'),

((SELECT UserID FROM Users WHERE Username = 'consultant003'), 
 N'Tham gia tư vấn tâm lý cho nhân viên công ty ABC', 
 '2024-12-18 09:00:00', 'APPROVED', 
 '2024-12-26 14:00:00', '2024-12-26 18:00:00'),

-- Thêm một số lịch nghỉ trong tương lai
((SELECT UserID FROM Users WHERE Username = 'consultant001'), 
 N'Nghỉ phép Tết Nguyên đán 2025', 
 '2024-12-20 10:00:00', 'PENDING', 
 '2025-01-28 00:00:00', '2025-02-03 23:59:59'),

((SELECT UserID FROM Users WHERE Username = 'consultant002'), 
 N'Tham gia Hội nghị Y khoa Quốc tế tại Singapore', 
 '2024-12-22 11:00:00', 'PENDING', 
 '2025-01-15 06:00:00', '2025-01-18 22:00:00'),

((SELECT UserID FROM Users WHERE Username = 'consultant003'), 
 N'Khóa đào tạo "Therapy tâm lý nhận thức hành vi"', 
 '2024-12-23 14:00:00', 'PENDING', 
 '2025-01-10 08:00:00', '2025-01-12 17:00:00');

 -- =====================================================
-- SAMPLE DATA FOR REPORT LOGS & SYMPTOM LOGS
-- =====================================================

USE [HS_New]
GO

-- =====================================================
-- SYMPTOM LOGS DATA - NHẬT KÝ TRIỆU CHỨNG CHI TIẾT
-- =====================================================

INSERT INTO [SymptomLogs] (LogID, SymptomID, Severity, Notes, CreatedAt) VALUES

-- User001 - Chu kỳ kinh nguyệt 30 ngày - Ngày đầu kỳ kinh (2024-11-10)
((SELECT TOP 1 LogID FROM MenstrualLogs WHERE CycleID = (SELECT TOP 1 CycleID FROM MenstrualCycles WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'user001')) AND LogDate = '2024-11-10 08:00:00'),
 (SELECT SymptomID FROM Symptoms WHERE SymptomName = N'Đau bụng kinh'), 'MILD', N'Đau bụng nhẹ từ sáng, có thể chịu được', GETDATE()),

((SELECT TOP 1 LogID FROM MenstrualLogs WHERE CycleID = (SELECT TOP 1 CycleID FROM MenstrualCycles WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'user001')) AND LogDate = '2024-11-10 08:00:00'),
 (SELECT SymptomID FROM Symptoms WHERE SymptomName = N'Căng tức ngực'), 'MODERATE', N'Ngực căng tức từ 2 ngày trước khi có kinh', GETDATE()),

-- User001 - Ngày thứ 2 của kỳ kinh (2024-11-11) - Triệu chứng nặng nhất
((SELECT TOP 1 LogID FROM MenstrualLogs WHERE CycleID = (SELECT TOP 1 CycleID FROM MenstrualCycles WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'user001')) AND LogDate = '2024-11-11 08:00:00'),
 (SELECT SymptomID FROM Symptoms WHERE SymptomName = N'Đau bụng kinh'), 'SEVERE', N'Đau bụng rất dữ dội, phải uống thuốc Ponstan 500mg', GETDATE()),

((SELECT TOP 1 LogID FROM MenstrualLogs WHERE CycleID = (SELECT TOP 1 CycleID FROM MenstrualCycles WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'user001')) AND LogDate = '2024-11-11 08:00:00'),
 (SELECT SymptomID FROM Symptoms WHERE SymptomName = N'Mệt mỏi'), 'SEVERE', N'Mệt mỏi toàn thân, không thể tập trung làm việc', GETDATE()),

((SELECT TOP 1 LogID FROM MenstrualLogs WHERE CycleID = (SELECT TOP 1 CycleID FROM MenstrualCycles WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'user001')) AND LogDate = '2024-11-11 08:00:00'),
 (SELECT SymptomID FROM Symptoms WHERE SymptomName = N'Thay đổi tâm trạng'), 'MODERATE', N'Cáu gắt, dễ bị kích động với đồng nghiệp', GETDATE()),

((SELECT TOP 1 LogID FROM MenstrualLogs WHERE CycleID = (SELECT TOP 1 CycleID FROM MenstrualCycles WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'user001')) AND LogDate = '2024-11-11 08:00:00'),
 (SELECT SymptomID FROM Symptoms WHERE SymptomName = N'Đau đầu'), 'MODERATE', N'Đau đầu từ chiều, có thể do căng thẳng', GETDATE()),

-- User002 - Chu kỳ 28 ngày - Ngày đầu kỳ kinh (2024-11-15)
((SELECT TOP 1 LogID FROM MenstrualLogs WHERE CycleID = (SELECT TOP 1 CycleID FROM MenstrualCycles WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'user002')) AND LogDate = '2024-11-15 08:00:00'),
 (SELECT SymptomID FROM Symptoms WHERE SymptomName = N'Mệt mỏi'), 'MILD', N'Hơi mệt nhưng vẫn làm việc bình thường', GETDATE()),

((SELECT TOP 1 LogID FROM MenstrualLogs WHERE CycleID = (SELECT TOP 1 CycleID FROM MenstrualCycles WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'user002')) AND LogDate = '2024-11-15 08:00:00'),
 (SELECT SymptomID FROM Symptoms WHERE SymptomName = N'Thay đổi tâm trạng'), 'MILD', N'Tâm trạng ổn định, không có thay đổi đáng kể', GETDATE()),

-- User003 - Chu kỳ không đều 32 ngày - Lo lắng về tình trạng (2024-11-08)
((SELECT TOP 1 LogID FROM MenstrualLogs WHERE CycleID = (SELECT TOP 1 CycleID FROM MenstrualCycles WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'user003')) AND LogDate = '2024-11-08 08:00:00'),
 (SELECT SymptomID FROM Symptoms WHERE SymptomName = N'Thay đổi tâm trạng'), 'SEVERE', N'Lo lắng và căng thẳng vì chu kỳ không đều, mất ngủ', GETDATE()),

((SELECT TOP 1 LogID FROM MenstrualLogs WHERE CycleID = (SELECT TOP 1 CycleID FROM MenstrualCycles WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'user003')) AND LogDate = '2024-11-08 08:00:00'),
 (SELECT SymptomID FROM Symptoms WHERE SymptomName = N'Đau đầu'), 'MODERATE', N'Đau đầu do stress và lo lắng kéo dài', GETDATE()),

((SELECT TOP 1 LogID FROM MenstrualLogs WHERE CycleID = (SELECT TOP 1 CycleID FROM MenstrualCycles WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'user003')) AND LogDate = '2024-11-08 08:00:00'),
 (SELECT SymptomID FROM Symptoms WHERE SymptomName = N'Buồn nôn'), 'MILD', N'Buồn nôn nhẹ, có thể do lo lắng', GETDATE()),

-- User004 - Đau bụng kinh rất nhiều (2024-11-12)
((SELECT TOP 1 LogID FROM MenstrualLogs WHERE CycleID = (SELECT TOP 1 CycleID FROM MenstrualCycles WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'user004')) AND LogDate = '2024-11-12 08:00:00'),
 (SELECT SymptomID FROM Symptoms WHERE SymptomName = N'Đau bụng kinh'), 'SEVERE', N'Đau bụng kinh cực kỳ dữ dội, phải nghỉ làm', GETDATE()),

((SELECT TOP 1 LogID FROM MenstrualLogs WHERE CycleID = (SELECT TOP 1 CycleID FROM MenstrualCycles WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'user004')) AND LogDate = '2024-11-12 08:00:00'),
 (SELECT SymptomID FROM Symptoms WHERE SymptomName = N'Buồn nôn'), 'SEVERE', N'Buồn nôn và nôn 2 lần do đau bụng quá nhiều', GETDATE()),

((SELECT TOP 1 LogID FROM MenstrualLogs WHERE CycleID = (SELECT TOP 1 CycleID FROM MenstrualCycles WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'user004')) AND LogDate = '2024-11-12 08:00:00'),
 (SELECT SymptomID FROM Symptoms WHERE SymptomName = N'Căng tức ngực'), 'MODERATE', N'Ngực căng tức từ 3 ngày trước khi có kinh', GETDATE()),

((SELECT TOP 1 LogID FROM MenstrualLogs WHERE CycleID = (SELECT TOP 1 CycleID FROM MenstrualCycles WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'user004')) AND LogDate = '2024-11-12 08:00:00'),
 (SELECT SymptomID FROM Symptoms WHERE SymptomName = N'Mệt mỏi'), 'SEVERE', N'Mệt mỏi cực độ, chỉ muốn nằm nghỉ', GETDATE()),

((SELECT TOP 1 LogID FROM MenstrualLogs WHERE CycleID = (SELECT TOP 1 CycleID FROM MenstrualCycles WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'user004')) AND LogDate = '2024-11-12 08:00:00'),
 (SELECT SymptomID FROM Symptoms WHERE SymptomName = N'Thay đổi tâm trạng'), 'MODERATE', N'Khó chịu và cáu gắt vì đau đớn', GETDATE()),
((SELECT TOP 1 LogID FROM MenstrualLogs WHERE CycleID = (SELECT TOP 1 CycleID FROM MenstrualCycles WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'user002')) AND LogDate = '2024-11-15 08:00:00'),
 (SELECT SymptomID FROM Symptoms WHERE SymptomName = N'Mụn trứng cá'), 'MILD', N'Mụn trứng cá nhẹ quanh cằm và trán', GETDATE()),

((SELECT TOP 1 LogID FROM MenstrualLogs WHERE CycleID = (SELECT TOP 1 CycleID FROM MenstrualCycles WHERE UserID = (SELECT UserID FROM Users WHERE Username = 'user003')) AND LogDate = '2024-11-08 08:00:00'),
 (SELECT SymptomID FROM Symptoms WHERE SymptomName = N'Tăng cân'), 'MODERATE', N'Tăng 1.5kg do tích nước, quần áo chật', GETDATE());

-- =====================================================
-- REPORT LOGS DATA - BÁO CÁO HỆ THỐNG
-- =====================================================

INSERT INTO [ReportLogs] (ReportType, GeneratedBy, GeneratedAt, ReportData, IsDeleted) VALUES

-- 1. BÁO CÁO BOOKING HÀNG THÁNG
('MONTHLY_BOOKING_REPORT', 
 (SELECT UserID FROM Users WHERE Username = 'admin'), 
 '2024-12-01 09:00:00', 
 N'{"report_period": "2024-11", "total_bookings": 18, "confirmed_bookings": 15, "pending_bookings": 2, "cancelled_bookings": 1, "total_revenue": 12750000, "services_breakdown": {"hormone_test": 6, "pap_test": 4, "ultrasound": 3, "infection_test": 2, "vitamin_test": 2, "menopause_test": 1}, "consultant_breakdown": {"consultant001": 8, "consultant002": 5, "consultant003": 5}, "payment_methods": {"vnpay": 8500000, "momo": 2750000, "bank_transfer": 1500000}, "avg_booking_value": 708333}',
 0),

-- 2. BÁO CÁO HIỆU SUẤT CONSULTANT
('CONSULTANT_PERFORMANCE_REPORT', 
 (SELECT UserID FROM Users WHERE Username = 'manager'), 
 '2024-12-01 14:30:00', 
 N'{"report_period": "2024-11", "consultant_performance": [{"consultant_id": ' + CAST((SELECT UserID FROM Users WHERE Username = 'consultant001') AS NVARCHAR) + ', "name": "BS. Nguyễn Thị Hương", "total_consultations": 12, "total_bookings": 8, "avg_rating": 4.8, "total_revenue": 6800000, "specialization": "Sản Phụ khoa", "response_time_hours": 2.5, "patient_satisfaction": 96}, {"consultant_id": ' + CAST((SELECT UserID FROM Users WHERE Username = 'consultant002') AS NVARCHAR) + ', "name": "BS. Trần Văn Linh", "total_consultations": 8, "total_bookings": 5, "avg_rating": 4.7, "total_revenue": 3200000, "specialization": "Nội tiết Sinh sản", "response_time_hours": 3.2, "patient_satisfaction": 94}, {"consultant_id": ' + CAST((SELECT UserID FROM Users WHERE Username = 'consultant003') AS NVARCHAR) + ', "name": "BS. Lê Thị Minh", "total_consultations": 10, "total_bookings": 5, "avg_rating": 4.9, "total_revenue": 2750000, "specialization": "Tâm lý Sức khỏe", "response_time_hours": 1.8, "patient_satisfaction": 98}]}',
 0),

-- 3. BÁO CÁO PHÂN TÍCH CHU KỲ KINH NGUYỆT
('MENSTRUAL_CYCLE_ANALYTICS', 
 (SELECT UserID FROM Users WHERE Username = 'admin'), 
 '2024-12-02 10:15:00', 
 N'{"report_period": "2024-11", "total_active_users": 5, "total_cycles_tracked": 5, "cycle_statistics": {"avg_cycle_length": 29.2, "min_cycle_length": 27, "max_cycle_length": 32, "regular_cycles": 4, "irregular_cycles": 1}, "period_statistics": {"avg_period_duration": 4.8, "min_period_duration": 4, "max_period_duration": 6}, "symptom_analysis": {"most_common_symptoms": ["Đau bụng kinh", "Mệt mỏi", "Thay đổi tâm trạng", "Căng tức ngực"], "severity_distribution": {"mild": 35, "moderate": 45, "severe": 20}, "symptom_frequency": {"pain": 80, "mood_changes": 60, "fatigue": 70, "breast_tenderness": 50, "headache": 40, "nausea": 30, "acne": 20, "weight_gain": 15}}, "user_engagement": {"daily_logs": 15, "symptom_logs": 18, "avg_logs_per_user": 3.6}, "health_insights": {"users_with_severe_symptoms": 2, "users_needing_consultation": 3, "improvement_after_consultation": 75}}',
 0),

-- 4. BÁO CÁO TÀI CHÍNH CHI TIẾT
('FINANCIAL_REPORT', 
 (SELECT UserID FROM Users WHERE Username = 'manager'), 
 '2024-12-02 16:45:00', 
 N'{"report_period": "2024-11", "revenue_summary": {"total_revenue": 12750000, "completed_payments": 10250000, "pending_payments": 2500000, "refunded_payments": 0}, "payment_method_breakdown": {"vnpay": {"amount": 8500000, "percentage": 66.7, "transactions": 8, "avg_transaction": 1062500}, "momo": {"amount": 2750000, "percentage": 21.6, "transactions": 4, "avg_transaction": 687500}, "bank_transfer": {"amount": 1500000, "percentage": 11.7, "transactions": 3, "avg_transaction": 500000}}, "service_revenue": {"hormone_test": 5100000, "pap_test": 1800000, "ultrasound": 960000, "infection_test": 1360000, "vitamin_test": 1040000, "menopause_test": 420000}, "consultant_revenue": {"consultant001": 6800000, "consultant002": 3200000, "consultant003": 2750000}, "growth_metrics": {"revenue_growth": 15.5, "transaction_growth": 12.3, "avg_transaction_value": 708333}, "financial_health": {"collection_rate": 80.4, "pending_rate": 19.6, "refund_rate": 0}}',
 0),

-- 5. BÁO CÁO HOẠT ĐỘNG NGƯỜI DÙNG
('USER_ACTIVITY_REPORT', 
 (SELECT UserID FROM Users WHERE Username = 'admin'), 
 '2024-12-03 08:30:00', 
 N'{"report_period": "2024-11", "user_statistics": {"total_users": 11, "active_users": 8, "new_registrations": 3, "user_retention": 72.7}, "user_engagement": {"total_logins": 156, "avg_session_duration": 25.5, "total_page_views": 1247, "bounce_rate": 12.5}, "content_engagement": {"blog_posts_viewed": 89, "most_viewed_category": "Chu kỳ kinh nguyệt", "blog_engagement_rate": 68.5, "avg_time_on_blog": 8.5}, "qa_system": {"total_questions": 5, "answered_questions": 4, "pending_questions": 1, "avg_response_time": 4.2, "question_categories": {"Chu kỳ kinh nguyệt": 2, "Sức khỏe sinh sản": 1, "Bệnh phụ khoa": 1, "Tâm lý & Tinh thần": 1}}, "chat_system": {"total_messages": 20, "active_conversations": 10, "avg_response_time": 2.8, "satisfaction_rate": 92.5}, "feature_usage": {"menstrual_tracking": 85, "appointment_booking": 65, "consultation_requests": 45, "symptom_logging": 75, "blog_reading": 55}, "user_satisfaction": {"overall_rating": 4.7, "feature_ratings": {"menstrual_tracking": 4.8, "consultations": 4.9, "booking_system": 4.6, "blog_content": 4.5, "chat_support": 4.8}}}',
 0),

-- 6. BÁO CÁO TRIỆU CHỨNG CHI TIẾT
('SYMPTOM_ANALYSIS_REPORT', 
 (SELECT UserID FROM Users WHERE Username = 'admin'), 
 '2024-12-03 11:00:00', 
 N'{"report_period": "2024-11", "symptom_overview": {"total_symptom_logs": 18, "unique_symptoms": 8, "users_logging_symptoms": 5}, "symptom_frequency": {"Đau bụng kinh": {"count": 5, "percentage": 27.8, "severity_breakdown": {"mild": 1, "moderate": 1, "severe": 3}}, "Mệt mỏi": {"count": 4, "percentage": 22.2, "severity_breakdown": {"mild": 1, "moderate": 1, "severe": 2}}, "Thay đổi tâm trạng": {"count": 4, "percentage": 22.2, "severity_breakdown": {"mild": 1, "moderate": 2, "severe": 1}}, "Căng tức ngực": {"count": 2, "percentage": 11.1, "severity_breakdown": {"mild": 0, "moderate": 2, "severe": 0}}, "Đau đầu": {"count": 2, "percentage": 11.1, "severity_breakdown": {"mild": 0, "moderate": 2, "severe": 0}}, "Buồn nôn": {"count": 3, "percentage": 16.7, "severity_breakdown": {"mild": 1, "moderate": 0, "severe": 2}}, "Mụn trứng cá": {"count": 1, "percentage": 5.6, "severity_breakdown": {"mild": 1, "moderate": 0, "severe": 0}}, "Tăng cân": {"count": 1, "percentage": 5.6, "severity_breakdown": {"mild": 0, "moderate": 1, "severe": 0}}}, "severity_analysis": {"mild_symptoms": 5, "moderate_symptoms": 8, "severe_symptoms": 8, "users_with_severe_symptoms": 3}, "correlation_analysis": {"pain_fatigue_correlation": 0.85, "mood_pain_correlation": 0.72, "hormonal_symptoms_correlation": 0.68}, "treatment_effectiveness": {"users_seeking_help": 4, "improvement_reported": 3, "consultation_requests": 2}, "recommendations": {"high_priority_users": ["user001", "user004"], "suggested_interventions": ["pain_management", "stress_reduction", "lifestyle_counseling"]}}',
 0),

-- 7. BÁO CÁO DỊCH VỤ XÉT NGHIỆM
('TESTING_SERVICES_REPORT', 
 (SELECT UserID FROM Users WHERE Username = 'manager'), 
 '2024-12-04 09:30:00', 
 N'{"report_period": "2024-11", "service_performance": {"total_services": 6, "total_bookings": 18, "completion_rate": 83.3}, "service_popularity": {"hormone_test": {"bookings": 6, "revenue": 5100000, "satisfaction": 4.8}, "pap_test": {"bookings": 4, "revenue": 1800000, "satisfaction": 4.7}, "ultrasound": {"bookings": 3, "revenue": 960000, "satisfaction": 4.6}, "infection_test": {"bookings": 2, "revenue": 1360000, "satisfaction": 4.5}, "vitamin_test": {"bookings": 2, "revenue": 1040000, "satisfaction": 4.4}, "menopause_test": {"bookings": 1, "revenue": 420000, "satisfaction": 4.3}}, "age_demographics": {"20-25": 40, "26-30": 35, "31-35": 20, "36-40": 5}, "booking_patterns": {"peak_hours": ["09:00-10:00", "14:00-15:00"], "peak_days": ["Monday", "Tuesday"], "seasonal_trends": "Increased bookings in winter months"}, "service_optimization": {"most_requested": "Hormone testing", "highest_satisfaction": "Hormone testing", "revenue_per_service": 708333, "avg_service_duration": 72}, "quality_metrics": {"on_time_service": 95, "result_delivery_time": 3.2, "follow_up_rate": 78, "repeat_customer_rate": 25}}',
 0),

-- 8. BÁO CÁO CHAT VÀ TƯ VẤN
('CHAT_CONSULTATION_REPORT', 
 (SELECT UserID FROM Users WHERE Username = 'admin'), 
 '2024-12-04 15:20:00', 
 N'{"report_period": "2024-11", "chat_statistics": {"total_conversations": 10, "answered_conversations": 7, "pending_conversations": 3, "avg_response_time": 2.8, "satisfaction_rate": 92.5}, "consultation_topics": {"menstrual_pain": 3, "stress_menstruation": 2, "pregnancy_preparation": 2, "pms_symptoms": 1, "contraception": 1, "infection_prevention": 1}, "consultant_workload": {"consultant001": {"chats": 3, "avg_response_time": 2.5, "satisfaction": 4.8}, "consultant002": {"chats": 3, "avg_response_time": 3.2, "satisfaction": 4.7}, "consultant003": {"chats": 4, "avg_response_time": 2.1, "satisfaction": 4.9}}, "peak_activity": {"busiest_hours": ["14:00-16:00", "20:00-22:00"], "busiest_days": ["Monday", "Wednesday", "Friday"]}, "user_satisfaction": {"very_satisfied": 70, "satisfied": 22.5, "neutral": 7.5, "dissatisfied": 0}, "follow_up_actions": {"appointment_bookings": 5, "service_recommendations": 8, "lifestyle_advice": 12, "referrals": 2}, "improvement_areas": {"reduce_response_time": "Target < 2 hours", "increase_availability": "Weekend coverage", "expand_topics": "Nutrition counseling"}}',
 0),

-- 9. BÁO CÁO TỔNG QUAN HỆ THỐNG
('SYSTEM_OVERVIEW_REPORT', 
 (SELECT UserID FROM Users WHERE Username = 'admin'), 
 '2024-12-05 10:00:00', 
 N'{"report_period": "2024-11", "system_health": {"uptime": 99.8, "avg_response_time": 1.2, "error_rate": 0.05, "database_size": "2.5GB"}, "user_metrics": {"total_users": 11, "active_users": 8, "growth_rate": 15.5, "churn_rate": 2.1}, "business_metrics": {"total_revenue": 12750000, "bookings": 18, "consultations": 15, "satisfaction_score": 4.7}, "feature_adoption": {"menstrual_tracking": 85, "online_booking": 75, "chat_consultation": 65, "blog_reading": 55, "symptom_logging": 80}, "technical_performance": {"page_load_time": 2.3, "mobile_usage": 68, "browser_compatibility": 98, "api_response_time": 0.8}, "security_metrics": {"login_attempts": 245, "failed_logins": 12, "security_incidents": 0, "data_backups": 30}, "operational_efficiency": {"automation_rate": 75, "manual_interventions": 8, "staff_productivity": 92, "cost_per_user": 125000}, "future_projections": {"expected_growth": 25, "capacity_planning": "Scale for 50 users", "feature_roadmap": ["AI symptom analysis", "Telemedicine integration", "Wearable device sync"]}}',
 0),

-- 10. BÁO CÁO FEEDBACK VÀ ĐÁNH GIÁ
('FEEDBACK_ANALYSIS_REPORT', 
 (SELECT UserID FROM Users WHERE Username = 'manager'), 
 '2024-12-05 14:45:00', 
 N'{"report_period": "2024-11", "feedback_summary": {"total_feedback": 15, "avg_rating": 4.7, "response_rate": 65}, "rating_distribution": {"5_stars": 60, "4_stars": 30, "3_stars": 10, "2_stars": 0, "1_star": 0}, "feedback_categories": {"service_quality": {"avg_rating": 4.8, "comments": 12}, "consultant_expertise": {"avg_rating": 4.9, "comments": 10}, "booking_process": {"avg_rating": 4.6, "comments": 8}, "communication": {"avg_rating": 4.7, "comments": 9}, "overall_experience": {"avg_rating": 4.7, "comments": 15}}, "positive_feedback": {"most_praised": ["Professional consultants", "Easy booking", "Helpful advice", "Quick response"], "satisfaction_drivers": ["Expertise", "Convenience", "Trust", "Results"]}, "improvement_areas": {"minor_issues": ["Booking time slots", "Mobile app features"], "suggestions": ["Weekend availability", "More payment options", "Reminder system"]}, "consultant_feedback": {"consultant001": {"avg_rating": 4.8, "strengths": ["Professional", "Knowledgeable"], "areas_for_improvement": ["Response time"]}, "consultant002": {"avg_rating": 4.7, "strengths": ["Thorough", "Patient"], "areas_for_improvement": ["Availability"]}, "consultant003": {"avg_rating": 4.9, "strengths": ["Empathetic", "Quick response"], "areas_for_improvement": ["None identified"]}}, "action_items": {"priority_1": ["Expand weekend hours"], "priority_2": ["Add mobile app features"], "priority_3": ["Implement reminder system"]}}',
 0);
