package com.example.gender_healthcare_service.service;
import com.example.gender_healthcare_service.dto.request.ConsultantFeedbackDTO;

import com.example.gender_healthcare_service.dto.response.FeedbackResponseDTO;
import com.example.gender_healthcare_service.entity.Feedback;
import com.example.gender_healthcare_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackService  {
    List<Feedback> getAllFeedback();
    Feedback createFeedback(Feedback feedback);
    Feedback getFeedbackById(Integer id);
    Feedback updateFeedback(Integer id, Feedback feedback);
    void deleteFeedback(Integer id);

    // Method cũ với customerId (để backward compatibility)
    FeedbackResponseDTO submitConsultationFeedback(ConsultantFeedbackDTO feedbackDTO);
    FeedbackResponseDTO updateFeedback(Integer id, ConsultantFeedbackDTO feedbackDTO);
    
    // Method mới tự động lấy user hiện tại
    FeedbackResponseDTO submitConsultationFeedback(ConsultantFeedbackDTO feedbackDTO, User currentUser);
    FeedbackResponseDTO updateFeedback(Integer id, ConsultantFeedbackDTO feedbackDTO, User currentUser);
    void deleteFeedback(Integer id, User currentUser);
    
    List<FeedbackResponseDTO> getConsultantFeedback(Long consultantId);
    FeedbackResponseDTO getConsultationFeedback(Integer consultationId);
    FeedbackResponseDTO getBookingFeedback(Integer bookingId);
}
