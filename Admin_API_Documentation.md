# 📋 Admin API Documentation

## 🔐 Thông tin chung
- **Base URL:** `/api/admin`
- **Authorization:** Cần token JWT với role `ROLE_ADMIN`
- **Header:** `Authorization: Bearer <your-jwt-token>`

---

## 🧪 **1. QUẢN LÝ DỊCH VỤ XÉT NGHIỆM**

### Tạo dịch vụ mới
```http
POST /api/admin/testing-services
```
**Body:**
```json
{
  "serviceName": "Xét nghiệm máu",
  "description": "Xét nghiệm công thức máu toàn bộ",
  "price": 150000,
  "durationMinutes": 30
}
```

### Cập nhật dịch vụ
```http
PUT /api/admin/testing-services/{serviceId}
```
**Body:**
```json
{
  "serviceName": "Xét nghiệm máu cập nhật",
  "description": "Mô tả mới",
  "price": 200000,
  "duration": 45
}
```

### Xóa dịch vụ
```http
DELETE /api/admin/testing-services/{serviceId}
```

### Khôi phục dịch vụ đã xóa
```http
PUT /api/admin/testing-services/{serviceId}/restore
```

### Xem danh sách đặt lịch xét nghiệm
```http
GET /api/admin/testing-services/bookings?pageNumber=1&pageSize=10
```

### Cập nhật kết quả xét nghiệm
```http
PUT /api/admin/testing-services/bookings/{bookingId}/results
```
**Body:**
```json
{
  "status": "COMPLETED",
  "result": "Kết quả bình thường"
}
```

---

## 👥 **2. QUẢN LÝ TƯ VẤN VIÊN**

### Xem thông tin chi tiết tư vấn viên
```http
GET /api/admin/consultants/{consultantId}
```

### Xem lịch làm việc của tư vấn viên
```http
GET /api/admin/consultants/{consultantId}/schedule
```

### Thêm thời gian nghỉ của tư vấn viên
```http
POST /api/admin/consultants/{consultantId}/unavailability
```
**Body:**
```json
{
  "startDate": "2024-01-15",
  "endDate": "2024-01-16",
  "reason": "Nghỉ phép cá nhân"
}
```

### Xem danh sách tất cả tư vấn viên
```http
GET /api/admin/listConsultant
```

### Tạo tư vấn viên mới
```http
POST /api/admin/consultants
```
**Body:**
```json
{
  "username": "consultant1",
  "email": "consultant@example.com",
  "fullName": "Bác sĩ Nguyễn Văn A",
  "phoneNumber": "0123456789",
  "biography": "Bác sĩ có kinh nghiệm",
  "qualifications": "Bác sĩ chuyên khoa",
  "experienceYears": 10,
  "specialization": "Phụ khoa"
}
```

### Chuyển user thành tư vấn viên
```http
PUT /api/admin/setUserToConsultant/{userId}
```

### Cập nhật thông tin tư vấn viên
```http
PUT /api/admin/consultants/{consultantId}
```
**Body:**
```json
{
  "biography": "Tiểu sử cập nhật",
  "qualifications": "Bằng cấp cập nhật",
  "experienceYears": 15,
  "specialization": "Chuyên khoa cập nhật"
}
```

### Xóa tư vấn viên
```http
DELETE /api/admin/consultants/{consultantId}
```

---

## 👤 **3. QUẢN LÝ NGƯỜI DÙNG**

### Xem thông tin chi tiết user
```http
GET /api/admin/users/{userId}
```

### Xem danh sách tất cả users (có phân trang)
```http
GET /api/admin/users?pageNumber=1&pageSize=10
```

### Cập nhật thông tin user
```http
PUT /api/admin/users/{userId}
```
**Body:**
```json
{
  "fullName": "Tên cập nhật",
  "email": "email@example.com",
  "phoneNumber": "0987654321",
  "roleName": "ROLE_CUSTOMER"
}
```

### Xóa user
```http
DELETE /api/admin/users/{userId}
```

---

## 📅 **4. QUẢN LÝ ĐẶT LỊCH TƯ VẤN**

### Xem tất cả đặt lịch tư vấn
```http
GET /api/admin/consultation-bookings?date=2024-01-15&status=SCHEDULED&userId=1&consultantId=1&pageNumber=1&pageSize=10
```

### Xem chi tiết đặt lịch tư vấn
```http
GET /api/admin/consultation-bookings/{bookingId}
```

### Hủy đặt lịch tư vấn
```http
POST /api/admin/consultation-bookings/{bookingId}/cancel
```
**Body (tùy chọn):**
```json
"Ghi chú hủy lịch từ admin"
```

### Đổi lịch tư vấn
```http
POST /api/admin/consultation-bookings/{bookingId}/reschedule
```
**Body:**
```json
{
  "newTimeSlotId": 123,
  "reason": "Yêu cầu từ bệnh nhân"
}
```

---

## 🩸 **5. QUẢN LÝ CHU KỲ KINH NGUYỆT**

### Xem thông tin chu kỳ của user
```http
GET /api/admin/menstrual-cycles/{userId}
```

---

## ⏰ **6. QUẢN LÝ NHẮC NHỞ**

### Xem nhắc nhở của user
```http
GET /api/admin/patient/{userId}/reminders
```

### Tạo nhắc nhở mới
```http
POST /api/admin/patient/reminder
```
**Body:**
```json
{
  "userId": 1,
  "title": "Nhắc nhở lịch hẹn",
  "message": "Bạn có lịch hẹn vào ngày mai",
  "reminderDate": "2024-01-16T10:00:00",
  "type": "APPOINTMENT"
}
```

### Xem nhắc nhở theo ID
```http
GET /api/admin/patient/reminder/{id}
```

### Cập nhật nhắc nhở
```http
PUT /api/admin/patient/reminder/{id}
```
**Body:**
```json
{
  "title": "Nhắc nhở cập nhật",
  "message": "Nội dung cập nhật",
  "reminderDate": "2024-01-17T10:00:00"
}
```

### Xóa nhắc nhở
```http
DELETE /api/admin/patient/reminder/{id}
```

---

## 📊 **7. BÁO CÁO & THỐNG KÊ**

### Báo cáo tổng quan
```http
GET /api/admin/reports/dashboard?startDate=2024-01-01&endDate=2024-01-31
```

### Thống kê tổng quan
```http
GET /api/admin/reports/overview
```

### Báo cáo đặt lịch
```http
GET /api/admin/reports/bookings?startDate=2024-01-01&endDate=2024-01-31&period=daily
```

### Báo cáo tài chính
```http
GET /api/admin/reports/financials?startDate=2024-01-01&endDate=2024-01-31&period=monthly
```

### Báo cáo người dùng
```http
GET /api/admin/reports/users?startDate=2024-01-01&endDate=2024-01-31&period=weekly
```

### Báo cáo tư vấn viên
```http
GET /api/admin/reports/consultants
```

### Báo cáo dịch vụ
```http
GET /api/admin/reports/services
```

---

## 📋 **8. QUẢN LÝ ĐƠN HÀNG**

### Xem tất cả đơn hàng
```http
GET /api/admin/orders?pageNumber=1&pageSize=10
```

---

## 🔍 **9. QUẢN LÝ PHẢN HỒI**

### Xem tất cả phản hồi
```http
GET /api/admin/feedback
```

### Cập nhật trạng thái phản hồi
```http
PUT /api/admin/feedback/{feedbackId}/status
```

---

## 📈 **10. DASHBOARD**

### Xem thống kê dashboard
```http
GET /api/admin/dashboard/stats
```

---

## 📝 **Thông tin bổ sung**

### **Tham số chung:**
- `pageNumber` (mặc định: 1) - Số trang
- `pageSize` (mặc định: 10) - Số item trên mỗi trang
- `startDate` (tùy chọn) - Ngày bắt đầu (định dạng: YYYY-MM-DD)
- `endDate` (tùy chọn) - Ngày kết thúc (định dạng: YYYY-MM-DD)
- `period` (mặc định: "daily") - Chu kỳ báo cáo (daily/weekly/monthly)

### **Định dạng phản hồi:**
- **Thành công:** `ApiResponse<T>` với data và message
- **Lỗi:** `ApiResponse<String>` với thông báo lỗi
- **Có phân trang:** `PageResponse<T>` với thông tin phân trang

### **Mã trạng thái HTTP:**
- `200 OK` - Thành công
- `201 Created` - Tạo mới thành công
- `400 Bad Request` - Dữ liệu không hợp lệ
- `404 Not Found` - Không tìm thấy
- `500 Internal Server Error` - Lỗi server

### **Ví dụ sử dụng:**
```bash
# Lấy danh sách users
curl -X GET "http://localhost:8080/api/admin/users?pageNumber=1&pageSize=10" \
  -H "Authorization: Bearer your-jwt-token"

# Tạo dịch vụ mới
curl -X POST "http://localhost:8080/api/admin/testing-services" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your-jwt-token" \
  -d '{
    "serviceName": "Xét nghiệm máu",
    "description": "Xét nghiệm công thức máu",
    "price": 150000,
    "durationMinutes": 30
  }'
```

---

## 🚀 **Lưu ý quan trọng:**

1. **Tất cả API đều yêu cầu quyền ADMIN**
2. **Token JWT phải còn hiệu lực**
3. **Kiểm tra dữ liệu đầu vào trước khi gửi**
4. **Xử lý lỗi phù hợp ở phía client**
5. **Sử dụng phân trang cho danh sách lớn**

---

## 📞 **Hỗ trợ:**
Nếu có vấn đề, hãy kiểm tra:
- Token JWT có hợp lệ không
- Quyền truy cập có đúng không
- Định dạng dữ liệu có đúng không
- Log server để xem lỗi chi tiết 