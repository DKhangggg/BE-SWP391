package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.dto.response.BookingStatusUpdateDTO;
import com.example.gender_healthcare_service.entity.Booking;
import com.example.gender_healthcare_service.repository.BookingRepository;
import com.example.gender_healthcare_service.service.BookingTrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingTrackingServiceImpl implements BookingTrackingService {

    private final SimpMessagingTemplate messagingTemplate;
    private final BookingRepository bookingRepository;

    @Override
    public void sendBookingStatusUpdate(BookingStatusUpdateDTO statusUpdate) {
        log.info("Sending booking status update for booking ID: {}, status: {}", 
                statusUpdate.getBookingId(), statusUpdate.getStatus());
        
        // Gửi tới topic chung cho tất cả
        messagingTemplate.convertAndSend("/topic/booking-updates", statusUpdate);
        
        // Gửi tới topic specific cho booking này
        messagingTemplate.convertAndSend("/topic/booking-updates/" + statusUpdate.getBookingId(), statusUpdate);
    }

    @Override
    public void sendBookingStatusUpdateToCustomer(Integer customerId, BookingStatusUpdateDTO statusUpdate) {
        log.info("Sending booking status update to customer ID: {}, booking ID: {}", 
                customerId, statusUpdate.getBookingId());
        
        // Gửi private message tới customer cụ thể
        messagingTemplate.convertAndSendToUser(
                customerId.toString(), 
                "/queue/booking-updates", 
                statusUpdate
        );
    }

    @Override
    public void sendBookingStatusUpdateToStaff(BookingStatusUpdateDTO statusUpdate) {
        log.info("Sending booking status update to staff for booking ID: {}", statusUpdate.getBookingId());
        
        // Gửi tới topic dành cho staff
        messagingTemplate.convertAndSend("/topic/staff/booking-updates", statusUpdate);
    }

    @Override
    public void notifyBookingStatusChange(Booking booking, String newStatus, String previousStatus, String updatedBy) {
        BookingStatusUpdateDTO statusUpdate = new BookingStatusUpdateDTO(
                booking.getId(),
                booking.getCustomerID().getFullName(),
                booking.getService().getServiceName(),
                newStatus,
                getStatusMessage(newStatus),
                updatedBy
        );
        statusUpdate.setPreviousStatus(previousStatus);
        
        // Gửi update tới tất cả channels
        sendBookingStatusUpdate(statusUpdate);
        sendBookingStatusUpdateToCustomer(booking.getCustomerID().getId(), statusUpdate);
        sendBookingStatusUpdateToStaff(statusUpdate);
    }

    @Override
    public void notifyNewBooking(Booking booking) {
        BookingStatusUpdateDTO statusUpdate = new BookingStatusUpdateDTO(
                booking.getId(),
                booking.getCustomerID().getFullName(),
                booking.getService().getServiceName(),
                booking.getStatus(),
                "Đặt lịch mới thành công. Vui lòng chờ xác nhận.",
                "System"
        );
        
        sendBookingStatusUpdate(statusUpdate);
        sendBookingStatusUpdateToCustomer(booking.getCustomerID().getId(), statusUpdate);
        sendBookingStatusUpdateToStaff(statusUpdate);
    }

    @Override
    public void notifySampleCollected(Integer bookingId, String collectedBy) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking != null) {
            BookingStatusUpdateDTO statusUpdate = new BookingStatusUpdateDTO(
                    bookingId,
                    booking.getCustomerID().getFullName(),
                    booking.getService().getServiceName(),
                    "Sample Collected",
                    "Đã lấy mẫu xét nghiệm. Đang chờ xử lý kết quả.",
                    collectedBy
            );
            
            sendBookingStatusUpdate(statusUpdate);
            sendBookingStatusUpdateToCustomer(booking.getCustomerID().getId(), statusUpdate);
            sendBookingStatusUpdateToStaff(statusUpdate);
        }
    }

    @Override
    public void notifyTestResultReady(Integer bookingId, String result) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking != null) {
            BookingStatusUpdateDTO statusUpdate = new BookingStatusUpdateDTO(
                    bookingId,
                    booking.getCustomerID().getFullName(),
                    booking.getService().getServiceName(),
                    "Results Ready",
                    "Kết quả xét nghiệm đã sẵn sàng. Vui lòng kiểm tra.",
                    "Lab System"
            );
            statusUpdate.setNotes(result);
            
            sendBookingStatusUpdate(statusUpdate);
            sendBookingStatusUpdateToCustomer(booking.getCustomerID().getId(), statusUpdate);
            sendBookingStatusUpdateToStaff(statusUpdate);
        }
    }

    @Override
    public void notifyBookingCompleted(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking != null) {
            BookingStatusUpdateDTO statusUpdate = new BookingStatusUpdateDTO(
                    bookingId,
                    booking.getCustomerID().getFullName(),
                    booking.getService().getServiceName(),
                    "Completed",
                    "Dịch vụ đã hoàn thành. Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi.",
                    "System"
            );
            
            sendBookingStatusUpdate(statusUpdate);
            sendBookingStatusUpdateToCustomer(booking.getCustomerID().getId(), statusUpdate);
            sendBookingStatusUpdateToStaff(statusUpdate);
        }
    }

    private String getStatusMessage(String status) {
        return switch (status.toLowerCase()) {
            case "scheduled", "pending" -> "Đã đặt lịch thành công. Vui lòng chờ xác nhận.";
            case "confirmed" -> "Lịch đã được xác nhận. Vui lòng đến đúng giờ.";
            case "in progress", "in_progress" -> "Đang thực hiện dịch vụ.";
            case "sample collected", "sample_collected" -> "Đã lấy mẫu. Đang xử lý kết quả.";
            case "processing" -> "Đang xử lý mẫu xét nghiệm.";
            case "results ready", "results_ready" -> "Kết quả đã sẵn sàng.";
            case "completed" -> "Dịch vụ đã hoàn thành.";
            case "cancelled" -> "Lịch đã bị hủy.";
            case "rescheduled" -> "Lịch đã được dời.";
            default -> "Cập nhật trạng thái: " + status;
        };
    }
} 