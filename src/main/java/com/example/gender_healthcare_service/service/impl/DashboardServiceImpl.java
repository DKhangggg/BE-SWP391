package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.dto.response.DashboardStatsResponseDTO;
import com.example.gender_healthcare_service.dto.response.UpcomingAppointmentResponseDTO;
import com.example.gender_healthcare_service.entity.*;
import com.example.gender_healthcare_service.entity.enumpackage.ConsultationStatus;
import com.example.gender_healthcare_service.entity.enumpackage.QuestionStatus;
import com.example.gender_healthcare_service.repository.*;
import com.example.gender_healthcare_service.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ConsultationRepository consultationRepository;
    private final BookingRepository bookingRepository;
    private final QuestionRepository questionRepository;
    private final NotificationRepository notificationRepository;
    private final MenstrualCycleRepository menstrualCycleRepository;

    @Override
    public DashboardStatsResponseDTO getDashboardStats(Integer userId) {
        long totalConsultations = consultationRepository.countByCustomer_IdAndStatus(userId, ConsultationStatus.COMPLETED);
        
        long totalSTITests = bookingRepository.countByCustomerID_IdAndStatus(userId, "COMPLETED");
        
        long totalQuestions = questionRepository.countByUser_IdAndStatus(userId, QuestionStatus.ANSWERED);
        
        long newNotifications = notificationRepository.countByUser_IdAndIsReadFalse(userId);
        
        // Đếm upcoming appointments
        long upcomingAppointments = getUpcomingAppointmentsCount(userId);
        
        // Lấy thông tin chu kỳ
        String cycleStatus = getCycleStatus(userId);
        String ovulationStatus = getOvulationStatus(userId);
        
        return new DashboardStatsResponseDTO(
                (int) totalConsultations,
                (int) totalSTITests,
                (int) totalQuestions,
                (int) newNotifications,
                (int) upcomingAppointments,
                cycleStatus,
                ovulationStatus
        );
    }

    @Override
    public List<UpcomingAppointmentResponseDTO> getUpcomingAppointments(Integer userId) {
        LocalDateTime now = LocalDateTime.now();
        
        // Lấy consultations sắp tới
        List<Consultation> consultations = consultationRepository.findByCustomer_IdAndTimeSlot_SlotDateAfterOrderByTimeSlot_SlotDateAsc(
                userId, now.toLocalDate());
        
        // Lấy bookings sắp tới
        List<Booking> bookings = bookingRepository.findByCustomerID_IdAndTimeSlot_SlotDateAfterOrderByTimeSlot_SlotDateAsc(
                userId, now.toLocalDate());
        
        List<UpcomingAppointmentResponseDTO> result = consultations.stream()
                .map(this::convertConsultationToDTO)
                .collect(Collectors.toList());
        
        result.addAll(bookings.stream()
                .map(this::convertBookingToDTO)
                .collect(Collectors.toList()));
        
        // Sắp xếp theo ngày
        return result.stream()
                .sorted((a, b) -> a.getAppointmentDate().compareTo(b.getAppointmentDate()))
                .limit(5) // Chỉ lấy 5 lịch hẹn gần nhất
                .collect(Collectors.toList());
    }

    private long getUpcomingAppointmentsCount(Integer userId) {
        LocalDateTime now = LocalDateTime.now();
        long consultationCount = consultationRepository.countByCustomer_IdAndTimeSlot_SlotDateAfter(
                userId, now.toLocalDate());
        long bookingCount = bookingRepository.countByCustomerID_IdAndTimeSlot_SlotDateAfter(
                userId, now.toLocalDate());
        return consultationCount + bookingCount;
    }

    private String getCycleStatus(Integer userId) {
        // Logic để lấy trạng thái chu kỳ
        // Có thể implement sau khi có menstrual cycle tracking
        return "Chưa thiết lập";
    }

    private String getOvulationStatus(Integer userId) {
        // Logic để lấy trạng thái rụng trứng
        // Có thể implement sau khi có menstrual cycle tracking
        return "Chưa thiết lập";
    }

    private UpcomingAppointmentResponseDTO convertConsultationToDTO(Consultation consultation) {
        return new UpcomingAppointmentResponseDTO(
                consultation.getId(),
                "CONSULTATION",
                "Lịch tư vấn với " + consultation.getConsultant().getFullName(),
                "Tư vấn trực tuyến",
                consultation.getTimeSlot().getSlotDate().atTime(consultation.getTimeSlot().getStartTime()),
                consultation.getStatus().toString(),
                consultation.getConsultant().getFullName(),
                "Tư vấn trực tuyến",
                consultation.getMeetingLink(),
                null
        );
    }

    private UpcomingAppointmentResponseDTO convertBookingToDTO(Booking booking) {
        return new UpcomingAppointmentResponseDTO(
                booking.getId(),
                "BOOKING",
                "Lịch xét nghiệm " + booking.getService().getServiceName(),
                "Xét nghiệm STI",
                booking.getTimeSlot().getSlotDate().atTime(booking.getTimeSlot().getStartTime()),
                booking.getStatus(),
                null,
                booking.getService().getServiceName(),
                null,
                null
        );
    }
} 