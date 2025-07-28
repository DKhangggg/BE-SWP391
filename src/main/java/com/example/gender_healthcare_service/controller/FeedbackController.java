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
        } catch (RuntimeException e) {
            // Kiểm tra nếu là lỗi đã đánh giá rồi thì trả về 400
            if (e.getMessage().contains("đã đánh giá")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Lỗi khi gửi phản hồi tư vấn: " + e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi gửi phản hồi tư vấn: " + e.getMessage()));
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

    @GetMapping("/consultation/{consultationId}")
    public ResponseEntity<ApiResponse<FeedbackResponseDTO>> getConsultationFeedback(@PathVariable Integer consultationId) {
        try {
            FeedbackResponseDTO feedback = feedbackService.getConsultationFeedback(consultationId);
            return ResponseEntity.ok(ApiResponse.success("Lấy phản hồi của consultation thành công", feedback));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy phản hồi consultation: " + e.getMessage()));
        }
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ApiResponse<FeedbackResponseDTO>> getBookingFeedback(@PathVariable Integer bookingId) {
        try {
            FeedbackResponseDTO feedback = feedbackService.getBookingFeedback(bookingId);
            return ResponseEntity.ok(ApiResponse.success("Lấy phản hồi của booking thành công", feedback));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy phản hồi booking: " + e.getMessage()));
        }
    }

    @PutMapping("/{feedbackId}")
    public ResponseEntity<ApiResponse<FeedbackResponseDTO>> updateFeedback(
            @PathVariable Integer feedbackId,
            @RequestBody ConsultantFeedbackDTO feedbackDTO) {
        try {
            FeedbackResponseDTO updatedFeedback = feedbackService.updateFeedback(feedbackId, feedbackDTO);
            return ResponseEntity.ok(ApiResponse.success("Cập nhật phản hồi thành công", updatedFeedback));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi cập nhật phản hồi: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{feedbackId}")
    public ResponseEntity<ApiResponse<String>> deleteFeedback(@PathVariable Integer feedbackId) {
        try {
            feedbackService.deleteFeedback(feedbackId);
            return ResponseEntity.ok(ApiResponse.success("Xóa phản hồi thành công", "Phản hồi đã được xóa"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi xóa phản hồi: " + e.getMessage()));
        }
    }
}