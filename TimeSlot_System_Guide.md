# Hệ Thống TimeSlot và Quản Lý Lịch Consultant Mới

## 🎯 Tổng Quan

Hệ thống mới được thiết kế để:
- ✅ Quản lý TimeSlot dạng template có thể tái sử dụng
- ✅ Tự động tạo lịch hàng tuần cho consultant
- ✅ Hỗ trợ booking linh hoạt với multiple bookings per slot
- ✅ Quản lý availability theo ngày trong tuần
- ✅ Theo dõi utilization và analytics

## 🏗️ Kiến Trúc Hệ Thống

### 1. **TimeSlot** - Template khung giờ
```java
// Khung giờ cố định, tái sử dụng được
TimeSlot {
    startTime: LocalTime  // 09:00
    endTime: LocalTime    // 10:00
    duration: Integer     // 60 minutes
    isActive: Boolean     // có thể sử dụng không
}
```

### 2. **ConsultantAvailability** - Template lịch tuần
```java
// Lịch có sẵn theo ngày trong tuần
ConsultantAvailability {
    consultant: Consultant
    dayOfWeek: DayOfWeek      // MONDAY, TUESDAY...
    timeSlot: TimeSlot
    maxBookings: Integer      // số booking tối đa cho slot này
    isAvailable: Boolean
}
```

### 3. **ConsultantSchedule** - Lịch cụ thể từng ngày
```java
// Lịch thực tế cho ngày cụ thể
ConsultantSchedule {
    consultant: Consultant
    scheduleDate: LocalDate   // 2024-01-15
    timeSlot: TimeSlot
    consultantAvailability: ConsultantAvailability
    currentBookings: Integer  // số booking hiện tại
    maxBookings: Integer      // số booking tối đa
    status: String           // AVAILABLE, FULLY_BOOKED, CANCELLED
}
```

### 4. **Booking** - Đặt lịch cụ thể
```java
// Booking cho một consultant trong schedule cụ thể
Booking {
    consultantSchedule: ConsultantSchedule
    timeSlot: TimeSlot
    bookingDate: LocalDate
    bookingTime: LocalTime
    customer: User
    status: String  // PENDING, CONFIRMED, COMPLETED, CANCELLED
}
```

## 🔄 Workflow Hoạt Động

### 1. **Setup Ban Đầu**
```java
// 1. Tạo TimeSlots template
timeSlotService.createTimeSlot("09:00", "10:00", 60, "Morning Slot 1");

// 2. Tạo availability template cho consultant
availabilityService.createAvailability(consultant, MONDAY, timeSlot9AM, 2, null);
availabilityService.createAvailability(consultant, TUESDAY, timeSlot9AM, 1, null);

// 3. Tạo default availability cho consultant mới
availabilityService.createDefaultAvailabilityTemplate(newConsultant);
```

### 2. **Tạo Lịch Hàng Tuần Tự Động**
```java
// Tạo lịch cho tuần mới (chạy weekly job)
LocalDate startOfWeek = LocalDate.of(2024, 1, 15); // Monday
availabilityService.generateWeeklyScheduleForAllConsultants(startOfWeek);

// Tạo lịch cho consultant cụ thể
availabilityService.generateWeeklySchedule(consultant, startOfWeek);
```

### 3. **Booking Process**
```java
// 1. Tìm consultant available
List<ConsultantSchedule> availableSchedules = 
    scheduleRepository.findAvailableSchedules(LocalDate.of(2024, 1, 15));

// 2. Tạo booking
BookingService.createBooking(customer, consultantSchedule, service, notes);

// 3. Schedule tự động update currentBookings và status
```

## 📋 Các Chức Năng Chính

### 1. **Quản Lý TimeSlot**
```java
// Tạo time slots chuẩn
timeSlotService.createStandardTimeSlots();

// Tìm time slots available cho ngày cụ thể
List<TimeSlot> slots = timeSlotRepository.findAvailableTimeSlotsForDay(DayOfWeek.MONDAY);
```

### 2. **Quản Lý Availability**
```java
// Set lịch làm việc cho consultant
List<DayOfWeek> workingDays = Arrays.asList(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY);
List<TimeSlot> workingSlots = timeSlotService.getWorkingHourSlots();
availabilityService.setWeeklyAvailability(consultant, workingDays, workingSlots, 1);

// Update availability
availabilityService.updateAvailability(availabilityId, true, 2, "Updated max bookings");
```

### 3. **Tạo Lịch Hàng Tuần**
```java
// Tự động tạo lịch cho tuần mới
@Scheduled(cron = "0 0 0 * * SUN") // Chạy vào Chủ nhật hàng tuần
public void generateNextWeekSchedules() {
    LocalDate nextMonday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
    availabilityService.generateWeeklyScheduleForAllConsultants(nextMonday);
}
```

### 4. **Booking và Quản Lý**
```java
// Tìm slot available
List<ConsultantSchedule> available = scheduleRepository.findAvailableSchedulesByConsultant(
    consultant, LocalDate.now());

// Tạo booking
Booking booking = bookingService.createBooking(customer, schedule, service);

// Schedule tự động update:
schedule.incrementBookings(); // currentBookings++
if (schedule.getCurrentBookings() >= schedule.getMaxBookings()) {
    schedule.setStatus("FULLY_BOOKED");
}
```

## 🚀 Ví Dụ Sử Dụng Thực Tế

### Scenario: Setup consultant Dr. Nguyễn Văn A

```java
// 1. Tạo consultant
Consultant drNguyen = consultantService.createConsultant(...);

// 2. Tạo availability template (Thứ 2-6, 9AM-5PM)
List<DayOfWeek> workingDays = Arrays.asList(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY);
List<TimeSlot> timeSlots = Arrays.asList(slot9AM, slot10AM, slot2PM, slot3PM, slot4PM);
availabilityService.setWeeklyAvailability(drNguyen, workingDays, timeSlots, 1);

// 3. Tạo lịch cho tuần này
LocalDate thisMonday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
availabilityService.generateWeeklySchedule(drNguyen, thisMonday);

// 4. Customer book appointment
LocalDate appointmentDate = thisMonday.plusDays(1); // Tuesday
ConsultantSchedule schedule = scheduleRepository
    .findAvailableSchedulesByConsultant(drNguyen, appointmentDate)
    .get(0);
    
Booking booking = bookingService.createBooking(customer, schedule, service);
```

### Scenario: Consultant muốn tăng capacity cho slot 2PM thứ 3

```java
// Cập nhật availability template
ConsultantAvailability availability = availabilityRepository
    .findByConsultantAndDayOfWeekAndTimeSlotAndIsDeletedFalse(drNguyen, TUESDAY, slot2PM)
    .orElseThrow();
    
availabilityService.updateAvailability(availability.getId(), true, 3, "Tăng capacity");

// Update existing schedules cho tuần này
LocalDate nextTuesday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.TUESDAY));
ConsultantSchedule schedule = scheduleRepository
    .findByConsultantAndScheduleDateAndTimeSlotAndIsDeletedFalse(drNguyen, nextTuesday, slot2PM)
    .orElseThrow();
    
schedule.setMaxBookings(3);
schedule.setStatus("AVAILABLE"); // Reset nếu đang FULLY_BOOKED
scheduleRepository.save(schedule);
```

## 📊 Analytics và Báo Cáo

```java
// Utilization rate của consultant
Double utilization = availabilityService.getConsultantUtilizationRate(
    consultant, startDate, endDate);

// Tìm consultant bận nhất
List<Consultant> busyConsultants = availabilityService
    .findConsultantsWithMostAvailability(DayOfWeek.MONDAY);

// Tổng slots available trong ngày
Long totalSlots = scheduleRepository.getTotalAvailableSlots(LocalDate.now());
```

## 🛠️ Database Migration

Chạy script SQL để tạo dữ liệu mẫu:
```sql
-- Chạy file: create_timeslot_sample_data.sql
-- Tạo 17 time slots từ 8AM-8PM với duration 30-60 phút
```

## 📈 Lợi Ích

1. **Scalability**: Dễ dàng thêm time slots mới
2. **Flexibility**: Consultant có thể có multiple bookings per slot
3. **Automation**: Tự động tạo lịch hàng tuần
4. **Analytics**: Tracking utilization và performance
5. **Maintainability**: Code rõ ràng, dễ maintain

## 🔧 Next Steps

1. Implement frontend để quản lý availability
2. Thêm notification system khi tạo lịch mới
3. Implement export/import availability
4. Thêm recurring availability patterns
5. Integration với calendar systems (Google Calendar, Outlook) 