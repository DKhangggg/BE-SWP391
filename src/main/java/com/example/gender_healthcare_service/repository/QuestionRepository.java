package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.dto.response.QuestionResponseDTO;
import com.example.gender_healthcare_service.entity.Question;
import com.example.gender_healthcare_service.entity.User;
import com.example.gender_healthcare_service.entity.enumpackage.QuestionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;



@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {
    @Query("SELECT q FROM Question q WHERE q.isDeleted = false AND q.status = ?1")
    Page<Question> findAllByStatus(QuestionStatus status, Pageable pageable);

    @Query("SELECT q FROM Question q WHERE q.status = ?1 AND q.category = ?2 AND q.isDeleted = false")
    Page<Question> findAllByStatusAndCategory(QuestionStatus status, String category, Pageable pageable);
    
    @Query("SELECT q FROM Question q WHERE q.isDeleted = false AND q.status IN ?1")
    Page<Question> findAllByStatusIn(List<QuestionStatus> statuses, Pageable pageable);

    @Query("SELECT q FROM Question q WHERE q.status IN ?1 AND q.category = ?2 AND q.isDeleted = false")
    Page<Question> findAllByStatusInAndCategory(List<QuestionStatus> statuses, String category, Pageable pageable);
    
    @Query("SELECT q FROM Question q WHERE q.category = ?1 AND q.isDeleted = false")
    Page<Question> findAllByCategory(String category, Pageable pageable);
    
    @Query("SELECT q FROM Question q WHERE q.questionId = ?1 AND q.isDeleted = false ")
    Question findQuestionById(Integer questionId);
    @Query("SELECT q FROM Question q WHERE q.isPublic = true AND q.isDeleted = false AND (q.content LIKE %:query% OR q.category LIKE %:query%)")
    Page<Question> searchQuestions(@Param("query") String query, Pageable pageable);

    @Query("SELECT q FROM Question q WHERE q.user = ?1 AND q.isDeleted = false")
    Page<Question> findQuestionsByUser(User user, Pageable pageable);
    
    @Query("SELECT q FROM Question q WHERE q.user = ?1 AND q.status = ?2 AND q.isDeleted = false")
    Page<Question> findQuestionsByUserAndStatus(User user, QuestionStatus status, Pageable pageable);
    
    @Query("SELECT q FROM Question q WHERE q.isDeleted = false")
    Page<Question> findAllQuestion(Pageable pageable);
    
    // Dashboard methods
    @Query("SELECT COUNT(q) FROM Question q WHERE q.user.id = :userId AND q.status = :status")
    long countByUser_IdAndStatus(@Param("userId") Integer userId, @Param("status") QuestionStatus status);
}
