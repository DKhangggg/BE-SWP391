package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.dto.request.ConsultantFeedbackDTO;
import com.example.gender_healthcare_service.dto.response.ApiResponse;
import com.example.gender_healthcare_service.dto.response.FeedbackResponseDTO;
import com.example.gender_healthcare_service.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping("/consultation")
    public ResponseEntity<ApiResponse<FeedbackResponseDTO>> submitConsultationFeedback(@RequestBody ConsultantFeedbackDTO feedbackDTO) {
        try {
            FeedbackResponseDTO response = feedbackService.submitConsultationFeedback(feedbackDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Gửi phản hồi tư vấn thành công", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi gửi phản hồi tư vấn: " + e.getMessage()));
        }
    }

    @GetMapping("/consultant/{consultantId}")
    public ResponseEntity<ApiResponse<List<FeedbackResponseDTO>>> getConsultantFeedback(@PathVariable Long consultantId) {
        try {
            List<FeedbackResponseDTO> feedbackList = feedbackService.getConsultantFeedback(consultantId);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách phản hồi của tư vấn viên thành công", feedbackList));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách phản hồi: " + e.getMessage()));
        }
    }
}