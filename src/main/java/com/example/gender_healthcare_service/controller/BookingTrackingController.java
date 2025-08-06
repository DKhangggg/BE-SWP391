package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.dto.response.BookingStatusUpdateDTO;
import com.example.gender_healthcare_service.service.BookingTrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * WEBSOCKET BOOKING TRACKING CONTROLLER
 * 
 * Controller này xử lý real-time tracking cho booking system thông qua WebSocket:
 * - Customer subscribe để nhận real-time updates về booking status
 * - Staff có thể trigger notifications khi cập nhật booking
 * - WebSocket messages được broadcast đến tất cả clients đang track booking
 * 
 * WebSocket Topics:
 * - /topic/booking-updates: Tất cả booking updates
 * - /topic/booking-updates/{bookingId}: Updates cho booking cụ thể
 * - /topic/staff/booking-updates: Staff notifications
 * 
 * Frontend Integration:
 * - SWP391_FE/src/context/WebSocketContext.jsx: WebSocket connection và subscription
 * - SWP391_FE/src/pages/User/STITesting/TrackingPage.jsx: Customer tracking page
 * - SWP391_FE/src/components/GlobalNotificationToast.jsx: Hiển thị notifications
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class BookingTrackingController {

    private final BookingTrackingService bookingTrackingService;

    /**
     * Customer subscribe để nhận tất cả booking updates
     * 
     * Frontend: SWP391_FE/src/context/WebSocketContext.jsx
     * - Khi customer connect WebSocket, tự động subscribe topic này
     * - Nhận được tất cả booking status updates
     * - Hiển thị notification toast khi có update
     * 
     * @return BookingStatusUpdateDTO với thông báo đã kết nối
     */
    @SubscribeMapping("/topic/booking-updates")
    public BookingStatusUpdateDTO onSubscribeToBookingUpdates() {
        log.info("Client subscribed to booking updates");
        return new BookingStatusUpdateDTO(null, "Connected", "Đã kết nối tới hệ thống tracking");
    }

    /**
     * Customer subscribe để nhận updates cho booking cụ thể
     * 
     * Frontend: SWP391_FE/src/pages/User/STITesting/TrackingPage.jsx
     * - Customer mở tracking page cho booking cụ thể
     * - Subscribe topic /topic/booking-updates/{bookingId}
     * - Nhận real-time updates khi staff cập nhật booking status
     * - UI tự động update và hiển thị notification
     * 
     * @param bookingId ID của booking cần track
     * @return BookingStatusUpdateDTO với thông báo đã kết nối cho booking cụ thể
     */
    @SubscribeMapping("/topic/booking-updates/{bookingId}")
    public BookingStatusUpdateDTO onSubscribeToSpecificBooking(@DestinationVariable Integer bookingId) {
        log.info("Client subscribed to booking {} updates", bookingId);
        return new BookingStatusUpdateDTO(bookingId, "Connected", "Đã kết nối tới tracking cho booking #" + bookingId);
    }

    /**
     * Staff subscribe để nhận booking updates
     * 
     * Frontend: SWP391_FE/src/pages/Staff/StaffAppointments.jsx
     * - Staff connect WebSocket để nhận notifications
     * - Có thể nhận được updates từ các staff khác
     * - Hiển thị real-time updates trong staff dashboard
     * 
     * @return BookingStatusUpdateDTO với thông báo staff đã kết nối
     */
    @SubscribeMapping("/topic/staff/booking-updates")
    public BookingStatusUpdateDTO onStaffSubscribeToUpdates() {
        log.info("Staff subscribed to booking updates");
        return new BookingStatusUpdateDTO(null, "Staff Connected", "Staff đã kết nối tới hệ thống tracking");
    }

    /**
     * Handle booking status update messages từ WebSocket
     * 
     * Frontend: SWP391_FE/src/context/WebSocketContext.jsx
     * - Client có thể gửi booking status update qua WebSocket
     * - Message được broadcast đến tất cả subscribers
     * - Trigger notification service để xử lý update
     * 
     * @param statusUpdate BookingStatusUpdateDTO chứa thông tin update
     * @return BookingStatusUpdateDTO đã được xử lý và broadcast
     */
    @MessageMapping("/booking-status-update")
    @SendTo("/topic/booking-updates")
    public BookingStatusUpdateDTO handleBookingStatusUpdate(@Payload BookingStatusUpdateDTO statusUpdate) {
        log.info("Received booking status update via WebSocket: {}", statusUpdate.getBookingId());
        
        // Set timestamp nếu chưa có
        if (statusUpdate.getTimestamp() == null) {
            statusUpdate.setTimestamp(LocalDateTime.now());
        }
        
        // Broadcast update
        bookingTrackingService.sendBookingStatusUpdate(statusUpdate);
        
        return statusUpdate;
    }

    /**
     * Staff mark sample collected và trigger WebSocket notification
     * 
     * Frontend: SWP391_FE/src/components/staff/SampleCollectionForm.jsx
     * - Staff submit form lấy mẫu
     * - API này được gọi để trigger WebSocket notification
     * - Customer nhận được real-time update: "Đã lấy mẫu"
     * - Status chuyển từ CONFIRMED → SAMPLE_COLLECTED
     * 
     * @param bookingId ID của booking đã lấy mẫu
     * @param collectedBy Tên staff đã lấy mẫu
     * @return Map với thông báo thành công
     */
    @PostMapping("/api/booking/{bookingId}/sample-collected")
    @ResponseBody
    public Map<String, String> markSampleCollected(@PathVariable Integer bookingId, 
                                                   @RequestParam String collectedBy) {
        log.info("Marking sample collected for booking {}", bookingId);
        
        bookingTrackingService.notifySampleCollected(bookingId, collectedBy);
        
        return Map.of(
            "status", "success",
            "message", "Sample collection notification sent",
            "bookingId", bookingId.toString()
        );
    }

    /**
     * Staff mark test results ready và trigger WebSocket notification
     * 
     * Frontend: SWP391_FE/src/components/staff/TestResultForm.jsx
     * - Staff upload test results
     * - API này được gọi để trigger WebSocket notification
     * - Customer nhận được real-time update: "Kết quả đã sẵn sàng"
     * - Status chuyển từ SAMPLE_COLLECTED → COMPLETED
     * 
     * @param bookingId ID của booking có kết quả
     * @param result Kết quả xét nghiệm
     * @return Map với thông báo thành công
     */
    @PostMapping("/api/booking/{bookingId}/results-ready")
    @ResponseBody
    public Map<String, String> markResultsReady(@PathVariable Integer bookingId, 
                                               @RequestParam String result) {
        log.info("Marking results ready for booking {}", bookingId);
        
        bookingTrackingService.notifyTestResultReady(bookingId, result);
        
        return Map.of(
            "status", "success",
            "message", "Test results notification sent",
            "bookingId", bookingId.toString()
        );
    }

    /**
     * Staff mark booking completed và trigger WebSocket notification
     * 
     * Frontend: SWP391_FE/src/components/staff/TestResultForm.jsx
     * - Staff hoàn thành tất cả quy trình cho booking
     * - API này được gọi để trigger WebSocket notification
     * - Customer nhận được real-time update: "Hoàn thành"
     * - Status chuyển thành COMPLETED
     * 
     * @param bookingId ID của booking đã hoàn thành
     * @return Map với thông báo thành công
     */
    @PostMapping("/api/booking/{bookingId}/completed")
    @ResponseBody
    public Map<String, String> markBookingCompleted(@PathVariable Integer bookingId) {
        log.info("Marking booking completed: {}", bookingId);
        
        bookingTrackingService.notifyBookingCompleted(bookingId);
        
        return Map.of(
            "status", "success",
            "message", "Booking completion notification sent",
            "bookingId", bookingId.toString()
        );
    }

    /**
     * Get WebSocket tracking system status
     * 
     * Frontend: SWP391_FE/src/context/WebSocketContext.jsx
     * - Client có thể check status của WebSocket system
     * - Hiển thị thông tin về endpoints và topics available
     * - Dùng để debug WebSocket connection issues
     * 
     * @return Map với thông tin status và endpoints
     */
    @GetMapping("/api/booking-tracking/status")
    @ResponseBody
    public Map<String, Object> getTrackingStatus() {
        return Map.of(
            "status", "active",
            "timestamp", LocalDateTime.now(),
            "endpoints", Map.of(
                "websocket", "/ws",
                "bookingTracking", "/booking-tracking",
                "topics", Map.of(
                    "allUpdates", "/topic/booking-updates",
                    "staffUpdates", "/topic/staff/booking-updates",
                    "userQueue", "/user/queue/booking-updates"
                )
            )
        );
    }
} 