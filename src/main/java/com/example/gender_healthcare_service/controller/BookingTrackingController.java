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

@Controller
@RequiredArgsConstructor
@Slf4j
public class BookingTrackingController {

    private final BookingTrackingService bookingTrackingService;

    @SubscribeMapping("/topic/booking-updates")
    public BookingStatusUpdateDTO onSubscribeToBookingUpdates() {
        log.info("Client subscribed to booking updates");
        return new BookingStatusUpdateDTO(null, "Connected", "Đã kết nối tới hệ thống tracking");
    }

    @SubscribeMapping("/topic/booking-updates/{bookingId}")
    public BookingStatusUpdateDTO onSubscribeToSpecificBooking(@DestinationVariable Integer bookingId) {
        log.info("Client subscribed to booking {} updates", bookingId);
        return new BookingStatusUpdateDTO(bookingId, "Connected", "Đã kết nối tới tracking cho booking #" + bookingId);
    }

    @SubscribeMapping("/topic/staff/booking-updates")
    public BookingStatusUpdateDTO onStaffSubscribeToUpdates() {
        log.info("Staff subscribed to booking updates");
        return new BookingStatusUpdateDTO(null, "Staff Connected", "Staff đã kết nối tới hệ thống tracking");
    }

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