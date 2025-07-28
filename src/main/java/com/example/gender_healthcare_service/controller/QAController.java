package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.dto.request.QuestionRequestDTO;
import com.example.gender_healthcare_service.dto.request.AnswerRequestDTO;
import com.example.gender_healthcare_service.dto.response.ApiResponse;
import com.example.gender_healthcare_service.dto.response.QuestionResponseDTO;
import com.example.gender_healthcare_service.dto.response.AnswerResponseDTO;
import com.example.gender_healthcare_service.dto.response.PageResponse;
import com.example.gender_healthcare_service.service.QAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/qa")
public class QAController {

    @Autowired
    private QAService qaService;


    @PostMapping("/questions")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<ApiResponse<QuestionResponseDTO>> submitQuestion(@RequestBody QuestionRequestDTO questionRequest) {
        try {
            QuestionResponseDTO newQuestion = qaService.submitQuestion(questionRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Gửi câu hỏi thành công", newQuestion));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi gửi câu hỏi: " + e.getMessage()));
        }
    }

    @GetMapping("/user/questions")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<ApiResponse<PageResponse<QuestionResponseDTO>>> getUserQuestions(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
            Page<QuestionResponseDTO> questions = qaService.getUserQuestions(userId, status, pageable);
            PageResponse<QuestionResponseDTO> response = new PageResponse<>();
            response.setContent(questions.getContent());
            response.setPageNumber(questions.getNumber() + 1);
            response.setPageSize(questions.getSize());
            response.setTotalElements(questions.getTotalElements());
            response.setTotalPages(questions.getTotalPages());
            response.setHasNext(questions.hasNext());
            response.setHasPrevious(questions.hasPrevious());
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách câu hỏi thành công", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách câu hỏi: " + e.getMessage()));
        }
    }

    @GetMapping("/consultant/questions")
    @PreAuthorize("hasAuthority('ROLE_CONSULTANT')")
    public ResponseEntity<ApiResponse<PageResponse<QuestionResponseDTO>>> getUnansweredQuestions(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
            Page<QuestionResponseDTO> questions = qaService.getConsultantQuestions(category, pageable);
            PageResponse<QuestionResponseDTO> response = new PageResponse<>();
            response.setContent(questions.getContent());
            response.setPageNumber(questions.getNumber() + 1);
            response.setPageSize(questions.getSize());
            response.setTotalElements(questions.getTotalElements());
            response.setTotalPages(questions.getTotalPages());
            response.setHasNext(questions.hasNext());
            response.setHasPrevious(questions.hasPrevious());
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách câu hỏi cho tư vấn viên thành công", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách câu hỏi: " + e.getMessage()));
        }
    }

    @GetMapping("/questions/{questionId}")
    public ResponseEntity<ApiResponse<QuestionResponseDTO>> getQuestionDetails(@PathVariable Integer questionId) {
        try {
            QuestionResponseDTO question = qaService.getQuestionById(questionId);
            return ResponseEntity.ok(ApiResponse.success("Lấy chi tiết câu hỏi thành công", question));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy chi tiết câu hỏi: " + e.getMessage()));
        }
    }

    @PostMapping("/questions/{questionId}/answers")
    @PreAuthorize("hasAuthority('ROLE_CONSULTANT')")
    public ResponseEntity<ApiResponse<AnswerResponseDTO>> answerQuestion(
            @PathVariable Integer questionId,
             @RequestBody(required=false) AnswerRequestDTO answerRequest) {
        try {
            AnswerResponseDTO newAnswer = qaService.answerQuestion(questionId, answerRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Trả lời câu hỏi thành công", newAnswer));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi trả lời câu hỏi: " + e.getMessage()));
        }
    }

    @PutMapping("/answers/{id}")
    @PreAuthorize("hasAuthority('ROLE_CONSULTANT')")
    public ResponseEntity<ApiResponse<AnswerResponseDTO>> updateAnswer(
            @PathVariable Integer id,
            @Valid @RequestBody AnswerRequestDTO answerRequest) {
        try {
            AnswerResponseDTO updatedAnswer = qaService.updateAnswer(id, answerRequest);
            return ResponseEntity.ok(ApiResponse.success("Cập nhật câu trả lời thành công", updatedAnswer));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi cập nhật câu trả lời: " + e.getMessage()));
        }
    }

    @GetMapping("/faq")
    public ResponseEntity<ApiResponse<List<QuestionResponseDTO>>> getFAQs(
            @RequestParam(required = false) String category) {
        try {
            List<QuestionResponseDTO> faqs = qaService.getFAQs(category);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách FAQ thành công", faqs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách FAQ: " + e.getMessage()));
        }
    }

    @PutMapping("/questions/{questionId}/public")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<QuestionResponseDTO>> markQuestionAsPublic(
            @PathVariable Integer questionId,
            @RequestParam boolean isPublic) {
        try {
            QuestionResponseDTO updatedQuestion = qaService.markQuestionAsPublic(questionId, isPublic);
            return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái công khai thành công", updatedQuestion));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi cập nhật trạng thái công khai: " + e.getMessage()));
        }
    }

    @DeleteMapping("/questions/{questionId}")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteQuestion(@PathVariable Integer questionId) {
        try {
            qaService.deleteQuestion(questionId);
            return ResponseEntity.ok(ApiResponse.success("Xóa câu hỏi thành công", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi xóa câu hỏi: " + e.getMessage()));
        }
    }

    @GetMapping("/categories/popular")
    public ResponseEntity<ApiResponse<List<?>>> getPopularCategories(
            @RequestParam(defaultValue = "5") int limit) {
        try {
            List<?> categories = qaService.getPopularCategories(limit);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách danh mục phổ biến thành công", categories));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách danh mục phổ biến: " + e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<QuestionResponseDTO>>> searchQuestions(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
            Page<QuestionResponseDTO> questions = qaService.searchQuestions(query, pageable);
            if(questions.isEmpty()){
                return ResponseEntity.ok(ApiResponse.success("Không tìm thấy câu hỏi nào", new PageResponse<>()));
            }
            else {
                PageResponse<QuestionResponseDTO> response = new PageResponse<>();
                response.setContent(questions.getContent());
                response.setPageNumber(questions.getNumber() + 1);
                response.setPageSize(questions.getSize());
                response.setTotalElements(questions.getTotalElements());
                response.setTotalPages(questions.getTotalPages());
                response.setHasNext(questions.hasNext());
                response.setHasPrevious(questions.hasPrevious());
                return ResponseEntity.ok(ApiResponse.success("Tìm kiếm câu hỏi thành công", response));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi tìm kiếm câu hỏi: " + e.getMessage()));
        }
    }

    @GetMapping("/public/questions")
    public ResponseEntity<ApiResponse<PageResponse<QuestionResponseDTO>>> getPublicQuestions(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
            Page<QuestionResponseDTO> questions = qaService.getConsultantQuestions(category, pageable);
            PageResponse<QuestionResponseDTO> response = new PageResponse<>();
            response.setContent(questions.getContent());
            response.setPageNumber(questions.getNumber() + 1);
            response.setPageSize(questions.getSize());
            response.setTotalElements(questions.getTotalElements());
            response.setTotalPages(questions.getTotalPages());
            response.setHasNext(questions.hasNext());
            response.setHasPrevious(questions.hasPrevious());
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách câu hỏi công khai thành công", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách câu hỏi: " + e.getMessage()));
        }
    }
}
