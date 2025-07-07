package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.dto.request.ConsultantFeedbackDTO;
import com.example.gender_healthcare_service.dto.response.FeedbackResponseDTO;
import com.example.gender_healthcare_service.entity.Feedback;
import com.example.gender_healthcare_service.entity.User;
import com.example.gender_healthcare_service.repository.FeedbackRepository;
import com.example.gender_healthcare_service.repository.FeedbackRepository;
import com.example.gender_healthcare_service.repository.UserRepository;
import com.example.gender_healthcare_service.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackRepository feedBackRepo;

    @Autowired
    private UserRepository userRepository;


    @Override
    public List<Feedback> getAllFeedback() {
        return feedBackRepo.findAll();
    }

    @Override
    public Feedback createFeedback(Feedback feedback) {
        return feedBackRepo.save(feedback);
    }

    @Override
    public Feedback getFeedbackById(Integer id) {
        return feedBackRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found with id: " + id));
    }

    @Override
    public Feedback updateFeedback(Integer id, Feedback newFeedback) {
        Feedback existing = getFeedbackById(id);
        existing.setRating(newFeedback.getRating());
        existing.setComment(newFeedback.getComment());
        existing.setId(newFeedback.getId());
        return feedBackRepo.save(existing);
    }

    @Override
    public void deleteFeedback(Integer id) {
        Feedback feedback = getFeedbackById(id);
        feedback.setIsDeleted(true);
        feedBackRepo.save(feedback);
    }

    @Override
    public FeedbackResponseDTO submitConsultationFeedback(ConsultantFeedbackDTO feedbackDTO) {
        User customer = userRepository.findById(feedbackDTO.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        User consultant = userRepository.findById(feedbackDTO.getConsultantId())
                .orElseThrow(() -> new RuntimeException("Consultant not found"));

        Feedback feedback = new Feedback();
        feedback.setCustomer(customer);
        feedback.setConsultant(consultant);
        feedback.setComment(feedbackDTO.getComment());
        feedback.setRating(feedbackDTO.getRating());
        feedback.setIsDeleted(false);
        feedback.setCreatedAt(LocalDate.now());

        Feedback saved = feedBackRepo.save(feedback);

        FeedbackResponseDTO response = new FeedbackResponseDTO();
        response.setId(saved.getId());
        response.setUserId(saved.getCustomer().getId());
        response.setConsultantId(saved.getConsultant() != null ? saved.getConsultant().getId() : null);
        response.setComment(saved.getComment());
        response.setRating(saved.getRating());
        response.setCreatedAt(saved.getCreatedAt() != null ? saved.getCreatedAt().toString() : null);
        return response;
    }

    @Override
    public List<FeedbackResponseDTO> getConsultantFeedback(Long consultantId) {
        User consultant = userRepository.findById(consultantId.intValue())
                .orElseThrow(() -> new RuntimeException("Consultant not found"));
        List<Feedback> feedbackList = feedBackRepo.findByConsultant(consultant);
        return feedbackList.stream().map(fb -> {
            FeedbackResponseDTO dto = new FeedbackResponseDTO();
            dto.setId(fb.getId());
            dto.setUserId(fb.getCustomer() != null ? fb.getCustomer().getId() : null);
            dto.setConsultantId(fb.getConsultant() != null ? fb.getConsultant().getId() : null);
            dto.setComment(fb.getComment());
            dto.setRating(fb.getRating());
            dto.setCreatedAt(fb.getCreatedAt() != null ? fb.getCreatedAt().toString() : null);
            return dto;
        }).toList();
    }


}