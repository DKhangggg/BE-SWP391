package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.dto.request.ConsultantFeedbackDTO;
import com.example.gender_healthcare_service.dto.response.FeedbackResponseDTO;
import com.example.gender_healthcare_service.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping("/consultation")
    public ResponseEntity<FeedbackResponseDTO> submitConsultationFeedback(@RequestBody ConsultantFeedbackDTO feedbackDTO) {
        FeedbackResponseDTO response = feedbackService.submitConsultationFeedback(feedbackDTO);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/consultant/{consultantId}")
    public ResponseEntity<List<FeedbackResponseDTO>> getConsultantFeedback(@PathVariable Long consultantId) {
        List<FeedbackResponseDTO> feedbackList = feedbackService.getConsultantFeedback(consultantId);
        return ResponseEntity.ok(feedbackList);
    }
}