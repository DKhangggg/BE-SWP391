package com.example.gender_healthcare_service.service;

import com.example.gender_healthcare_service.dto.response.BookingStatusUpdateDTO;
import com.example.gender_healthcare_service.entity.Booking;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Send notification to booking-specific topic when status changes
     */
    public void notifyBookingStatusChange(Booking booking, String newStatus, String previousStatus, String updatedBy) {
        log.info("🔔 Sending booking status notification - Booking ID: {}, Status: {} -> {}", 
                booking.getId(), previousStatus, newStatus);

        // Create status update DTO
        BookingStatusUpdateDTO statusUpdate = new BookingStatusUpdateDTO();
        statusUpdate.setBookingId(booking.getId());
        statusUpdate.setCustomerName(booking.getCustomerID().getFullName());
        statusUpdate.setServiceName(booking.getService().getServiceName());
        statusUpdate.setStatus(newStatus);
        statusUpdate.setPreviousStatus(previousStatus);
        statusUpdate.setTimestamp(LocalDateTime.now());
        statusUpdate.setUpdatedBy(updatedBy);
        
        // Set appropriate message based on status
        String message = getStatusMessage(newStatus);
        statusUpdate.setMessage(message);

        // Send to booking-specific topic
        String topic = "/topic/booking-updates/" + booking.getId();
        
        try {
            messagingTemplate.convertAndSend(topic, statusUpdate);
            log.info("✅ Successfully sent notification to topic: {}", topic);
        } catch (Exception e) {
            log.error("❌ Failed to send notification to topic {}: {}", topic, e.getMessage());
        }
    }

    /**
     * Get user-friendly message for status
     */
    private String getStatusMessage(String status) {
        return switch (status.toUpperCase()) {
            case "PENDING" -> "Booking đã được tạo, đang chờ xác nhận từ nhân viên.";
            case "CONFIRMED" -> "Booking đã được xác nhận. Vui lòng đến đúng giờ hẹn.";
            case "SAMPLE_COLLECTED" -> "Mẫu xét nghiệm đã được lấy. Đang chờ kết quả.";
            case "COMPLETED" -> "Kết quả xét nghiệm đã sẵn sàng. Vui lòng kiểm tra.";
            case "CANCELLED" -> "Booking đã bị hủy.";
            default -> "Trạng thái booking đã được cập nhật.";
        };
    }
}
