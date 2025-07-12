package com.example.gender_healthcare_service.service;

import com.example.gender_healthcare_service.dto.response.BookingStatusUpdateDTO;
import com.example.gender_healthcare_service.entity.Booking;

public interface BookingTrackingService {
    
    /**
     * Gửi update status cho specific booking
     */
    void sendBookingStatusUpdate(BookingStatusUpdateDTO statusUpdate);
    
    /**
     * Gửi update cho customer cụ thể
     */
    void sendBookingStatusUpdateToCustomer(Integer customerId, BookingStatusUpdateDTO statusUpdate);
    
    /**
     * Gửi update cho tất cả staff
     */
    void sendBookingStatusUpdateToStaff(BookingStatusUpdateDTO statusUpdate);
    
    /**
     * Tạo và gửi status update từ booking entity
     */
    void notifyBookingStatusChange(Booking booking, String newStatus, String previousStatus, String updatedBy);
    
    /**
     * Notify về booking mới
     */
    void notifyNewBooking(Booking booking);
    
    /**
     * Notify về sample collection
     */
    void notifySampleCollected(Integer bookingId, String collectedBy);
    
    /**
     * Notify về kết quả xét nghiệm
     */
    void notifyTestResultReady(Integer bookingId, String result);
    
    /**
     * Notify về completion
     */
    void notifyBookingCompleted(Integer bookingId);
} 