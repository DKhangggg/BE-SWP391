package com.example.gender_healthcare_service.service;

import com.example.gender_healthcare_service.dto.request.AnswerRequestDTO;
import com.example.gender_healthcare_service.dto.request.QuestionRequestDTO;
import com.example.gender_healthcare_service.dto.response.AnswerResponseDTO;
import com.example.gender_healthcare_service.dto.response.QuestionResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface QAService {
    QuestionResponseDTO submitQuestion(QuestionRequestDTO questionRequest);
    Page<QuestionResponseDTO> getUserQuestions(Integer UserId,String status, Pageable pageable);
    Page<QuestionResponseDTO> getConsultantQuestions(String category, Pageable pageable);
    QuestionResponseDTO getQuestionById(Integer questionId);
    AnswerResponseDTO answerQuestion(Integer questionId, AnswerRequestDTO answerRequest);
    AnswerResponseDTO updateAnswer(Integer answerId, AnswerRequestDTO answerRequest);
    List<QuestionResponseDTO> getFAQs(String category);
    QuestionResponseDTO markQuestionAsPublic(Integer questionId, boolean isPublic);
    void deleteQuestion(Integer questionId);
    List<Map<String, Object>> getPopularCategories(int limit);
    Page<QuestionResponseDTO> searchQuestions(String query, Pageable pageable);
}
