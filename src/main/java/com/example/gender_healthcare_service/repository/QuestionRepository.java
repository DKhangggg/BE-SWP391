package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.dto.response.QuestionResponseDTO;
import com.example.gender_healthcare_service.entity.Question;
import com.example.gender_healthcare_service.entity.User;
import com.example.gender_healthcare_service.entity.enumpackage.QuestionStatus;
import io.micrometer.observation.ObservationFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;



@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {
    @Query("SELECT q FROM Question q WHERE q.isDeleted =false  AND q.status = ?1")
    Page<Question> findAllByStatus(QuestionStatus status, Pageable pageable);

    @Query("SELECT q FROM Question q WHERE q.status = ?1 AND q.category = ?2 AND q.isDeleted = false")
    Page<Question> findAllByStatusAndCategory(QuestionStatus status, String category, Pageable pageable);
    @Query("SELECT q FROM Question q WHERE q.questionId = ?1 AND q.isDeleted = false ")
    Question findQuestionById(Integer questionId);
    @Query("SELECT q FROM Question q WHERE q.isPublic = true AND q.isDeleted = false AND q.category = ?1")
    Page<QuestionResponseDTO> searchQuestions(String query, Pageable pageable);

    @Query("SELECT q FROM Question q WHERE q.user = ?1 AND q.isDeleted = false")
    Page<Question> findQuestionsByUser(User user, Pageable pageable);
    @Query("SELECT q FROM Question q WHERE q.isDeleted = false")
    Page<Question> findAllQuestion(Pageable pageable);
}
