package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.dto.request.QuestionRequestDTO;
import com.example.gender_healthcare_service.dto.request.AnswerRequestDTO;
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
    public ResponseEntity<QuestionResponseDTO> submitQuestion(@RequestBody QuestionRequestDTO questionRequest) {

        QuestionResponseDTO newQuestion = qaService.submitQuestion(questionRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(newQuestion);
    }

    @GetMapping("/user/questions")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<PageResponse<QuestionResponseDTO>> getUserQuestions(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {
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
        return ResponseEntity.ok(response);
    }

    @GetMapping("/consultant/questions")
    @PreAuthorize("hasAuthority('ROLE_CONSULTANT')")
    public ResponseEntity<PageResponse<QuestionResponseDTO>> getUnansweredQuestions(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {
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
        return ResponseEntity.ok(response);
    }

    @GetMapping("/questions/{questionId}")
    public ResponseEntity<QuestionResponseDTO> getQuestionDetails(@PathVariable Integer questionId) {
        QuestionResponseDTO question = qaService.getQuestionById(questionId);
        return ResponseEntity.ok(question);
    }

    @PostMapping("/questions/{questionId}/answers")
    @PreAuthorize("hasAuthority('ROLE_CONSULTANT')")
    public ResponseEntity<AnswerResponseDTO> answerQuestion(
            @PathVariable Integer questionId,
             @RequestBody(required=false) AnswerRequestDTO answerRequest) {
        AnswerResponseDTO newAnswer = qaService.answerQuestion(questionId, answerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(newAnswer);
    }

    @PutMapping("/answers/{answerId}")
    @PreAuthorize("hasAuthority('ROLE_CONSULTANT')")
    public ResponseEntity<AnswerResponseDTO> updateAnswer(
            @PathVariable Integer answerId,
            @Valid @RequestBody AnswerRequestDTO answerRequest) {
        AnswerResponseDTO updatedAnswer = qaService.updateAnswer(answerId, answerRequest);
        return ResponseEntity.ok(updatedAnswer);
    }

    @GetMapping("/faq")
    public ResponseEntity<List<QuestionResponseDTO>> getFAQs(
            @RequestParam(required = false) String category) {
        List<QuestionResponseDTO> faqs = qaService.getFAQs(category);
        return ResponseEntity.ok(faqs);
    }

    @PutMapping("/questions/{questionId}/public")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<QuestionResponseDTO> markQuestionAsPublic(
            @PathVariable Integer questionId,
            @RequestParam boolean isPublic) {
        QuestionResponseDTO updatedQuestion = qaService.markQuestionAsPublic(questionId, isPublic);
        return ResponseEntity.ok(updatedQuestion);
    }

    @DeleteMapping("/questions/{questionId}")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN')")
    public ResponseEntity<?> deleteQuestion(@PathVariable Integer questionId) {
        qaService.deleteQuestion(questionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categories/popular")
    public ResponseEntity<List<?>> getPopularCategories(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(qaService.getPopularCategories(limit));
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<QuestionResponseDTO>> searchQuestions(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<QuestionResponseDTO> questions = qaService.searchQuestions(query, pageable);
        if(questions.isEmpty()){
            return ResponseEntity.noContent().build();
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
            return ResponseEntity.ok(response);
        }
    }
}
