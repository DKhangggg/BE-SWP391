package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.dto.request.AnswerRequestDTO;
import com.example.gender_healthcare_service.dto.request.QuestionRequestDTO;
import com.example.gender_healthcare_service.dto.response.AnswerResponseDTO;
import com.example.gender_healthcare_service.dto.response.QuestionResponseDTO;
import com.example.gender_healthcare_service.dto.response.UserResponseDTO;
import com.example.gender_healthcare_service.dto.response.ConsultantDTO;
import com.example.gender_healthcare_service.entity.Answer;
import com.example.gender_healthcare_service.entity.Consultant;
import com.example.gender_healthcare_service.entity.Question;
import com.example.gender_healthcare_service.entity.User;
import com.example.gender_healthcare_service.entity.enumpackage.QuestionStatus;
import com.example.gender_healthcare_service.repository.AnswerRepository;
import com.example.gender_healthcare_service.repository.ConsultantRepository;
import com.example.gender_healthcare_service.repository.QuestionRepository;
import com.example.gender_healthcare_service.repository.UserRepository;
import com.example.gender_healthcare_service.service.QAService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QnAServiceImpl implements QAService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConsultantRepository consultantRepository;



    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Override
    public QuestionResponseDTO submitQuestion(QuestionRequestDTO questionRequest) {
        if (questionRequest.getContent() == null || questionRequest.getUserId() == null) {
            throw new IllegalArgumentException("Question content and user ID must not be null");
        }

        Question question = new Question();
        question.setContent(questionRequest.getContent());
        question.setCategory(questionRequest.getCategory() != null ? questionRequest.getCategory() : "general");
        question.setCreatedAt(LocalDateTime.now());
        question.setUpdatedAt(LocalDateTime.now());
        question.setStatus(QuestionStatus.PENDING);
        question.setPublic(false); // Default to private
        question.setUser(userRepository.findById(questionRequest.getUserId()).orElseThrow(() -> new RuntimeException("User not found")));

        Question savedQuestion = questionRepository.save(question);
        return mapQuestionToDTO(savedQuestion);
    }

    @Override
    public Page<QuestionResponseDTO> getUserQuestions(Integer userId, String status, Pageable pageable) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        if (status == null || status.isEmpty()) {
            // Lấy tất cả câu hỏi của user không cần filter status
            return questionRepository.findQuestionsByUser(currentUser, pageable)
                    .map(question -> mapQuestionToDTO(question));
        }  else {
            // Nếu có status thì filter theo status và user
            QuestionStatus questionStatus = QuestionStatus.valueOf(status.toUpperCase());
            return questionRepository.findQuestionsByUserAndStatus(currentUser, questionStatus, pageable)
                    .map(question -> mapQuestionToDTO(question));
        }
    }

    @Override
    public Page<QuestionResponseDTO> getConsultantQuestions(String category, Pageable pageable) {
        if( category == null || category.isEmpty()) {
            // Lấy tất cả câu hỏi không cần filter status
            return questionRepository.findAllQuestion(pageable)
                    .map(question -> mapQuestionToDTO(question));
        }else {
            // Lấy tất cả câu hỏi theo category không cần filter status
            return questionRepository.findAllByCategory(category, pageable)
                    .map(question -> mapQuestionToDTO(question));
        }
    }

    @Override
    public QuestionResponseDTO getQuestionById(Integer questionId) {
        Question question = questionRepository.findQuestionById(questionId);
        if(question == null || question.isDeleted()) {
            throw new IllegalArgumentException("Question not found with ID: " + questionId);
        }
        return mapQuestionToDTO(question);
    }

    @Override
    public AnswerResponseDTO answerQuestion(Integer questionId, AnswerRequestDTO answerRequest) {
        Question question = questionRepository.findQuestionById(questionId);
        if(question == null || question.isDeleted()) {
            throw new IllegalArgumentException("Question not found with ID: " + questionId);
        }
        if (answerRequest.getContent() == null ) {
            throw new IllegalArgumentException("Answer content must not be null");
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findUserByUsername(username);
        if(currentUser == null) {
            throw new IllegalArgumentException("User not found with username: " + username);
        }
        Consultant consultant = consultantRepository.findById(currentUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("Consultant not found with ID: " + currentUser.getId()));

        Answer answer = new Answer();
        answer.setContent(answerRequest.getContent());
        answer.setConsultant(consultant);
        answer.setQuestion(question);
        answer.setCreatedAt(LocalDateTime.now());
        answer.setUpdatedAt(LocalDateTime.now());
        answerRepository.save(answer);

        // Update question as answered
        question.setAnswered(true);
        question.setStatus(QuestionStatus.ANSWERED);
        question.setUpdatedAt(LocalDateTime.now());
        questionRepository.save(question);

        return mapAnswerToDTO(answer);
    }

    @Override
    public AnswerResponseDTO updateAnswer(Integer id, AnswerRequestDTO answerRequest) {
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Answer not found with ID: " + id));

        if (answerRequest.getContent() == null) {
            throw new IllegalArgumentException("Answer content must not be null");
        }

        answer.setContent(answerRequest.getContent());
        answer.setUpdatedAt(LocalDateTime.now());
        answerRepository.save(answer);

        return mapAnswerToDTO(answer);

    }

    @Override
    public List<QuestionResponseDTO> getFAQs(String category) {
        if (category == null || category.isEmpty()) {
            return questionRepository.findAllByStatus(QuestionStatus.APPROVED, Pageable.unpaged())
                    .map(question -> mapQuestionToDTO(question))
                    .getContent();
        } else {
            return questionRepository.findAllByStatusAndCategory(QuestionStatus.APPROVED, category, Pageable.unpaged())
                    .map(question -> mapQuestionToDTO(question))
                    .getContent();
        }
    }

    @Override
    public QuestionResponseDTO markQuestionAsPublic(Integer questionId, boolean isPublic) {
    Question question =questionRepository.findQuestionById(questionId);
        if(question == null || question.isDeleted()) {
            throw new IllegalArgumentException("Question not found with ID: " + questionId);
        }
        question.setPublic(isPublic);
        question.setUpdatedAt(LocalDateTime.now());
        questionRepository.save(question);
        return mapQuestionToDTO(question);
    }

    @Override
    public void deleteQuestion(Integer questionId) {
        Question question = questionRepository.findQuestionById(questionId);
        if(question == null || question.isDeleted()) {
            throw new IllegalArgumentException("Question not found with ID: " + questionId);
        }
        question.setDeleted(true);
        question.getAnswers().forEach(answer -> {
            answer.setDeleted(true);
            answerRepository.save(answer);
        });
        questionRepository.save(question);
    }

    @Override
    public List<Map<String, Object>> getPopularCategories(int limit) {
        return List.of();
    }

    @Override
    public Page<QuestionResponseDTO> searchQuestions(String query, Pageable pageable) {
        if (query == null || query.isEmpty()) {
            return questionRepository.findAllQuestion(pageable)
                    .map(question -> mapQuestionToDTO(question));
        } else {
            return questionRepository.searchQuestions(query, pageable)
                    .map(question -> mapQuestionToDTO(question));
        }
    }

    // Manual mapping methods
    private QuestionResponseDTO mapQuestionToDTO(Question question) {
        QuestionResponseDTO dto = new QuestionResponseDTO();
        dto.setId(question.getQuestionId());
        dto.setUserId(question.getUser().getId());
        dto.setCategory(question.getCategory());
        dto.setContent(question.getContent());
        dto.setStatus(question.getStatus().toString());
        dto.setPublic(question.isPublic());
        dto.setAnswered(question.isAnswered());
        dto.setCreatedAt(question.getCreatedAt());
        dto.setUpdatedAt(question.getUpdatedAt());
        
        // Map user
        UserResponseDTO userDTO = new UserResponseDTO();
        userDTO.setId(question.getUser().getId());
        userDTO.setUsername(question.getUser().getUsername());
        userDTO.setEmail(question.getUser().getEmail());
        userDTO.setFullName(question.getUser().getFullName());
        userDTO.setRoleName(question.getUser().getRoleName());
        userDTO.setPhoneNumber(question.getUser().getPhoneNumber());
        userDTO.setAddress(question.getUser().getAddress());
        userDTO.setDateOfBirth(question.getUser().getDateOfBirth());
        userDTO.setMedicalHistory(question.getUser().getMedicalHistory());
        userDTO.setGender(question.getUser().getGender());
        userDTO.setDescription(question.getUser().getDescription());
        userDTO.setAvatarUrl(question.getUser().getAvatarUrl());
        userDTO.setAvatarPublicId(question.getUser().getAvatarPublicId());
        dto.setUser(userDTO);
        
        // Map answers
        List<AnswerResponseDTO> answerDTOs = question.getAnswers().stream()
                .filter(answer -> !answer.isDeleted())
                .map(this::mapAnswerToDTO)
                .collect(Collectors.toList());
        dto.setAnswers(answerDTOs);
        
        return dto;
    }

    private AnswerResponseDTO mapAnswerToDTO(Answer answer) {
        AnswerResponseDTO dto = new AnswerResponseDTO();
        dto.setId(answer.getId());
        dto.setQuestionId(answer.getQuestion().getQuestionId());
        dto.setContent(answer.getContent());
        dto.setConsultantId(answer.getConsultant().getId());
        dto.setCreatedAt(answer.getCreatedAt());
        dto.setUpdatedAt(answer.getUpdatedAt());
        
        // Map consultant
        ConsultantDTO consultantDTO = new ConsultantDTO();
        consultantDTO.setId(answer.getConsultant().getId());
        consultantDTO.setUsername(answer.getConsultant().getUser().getUsername());
        consultantDTO.setEmail(answer.getConsultant().getUser().getEmail());
        consultantDTO.setFullName(answer.getConsultant().getUser().getFullName());
        consultantDTO.setPhoneNumber(answer.getConsultant().getUser().getPhoneNumber());
        consultantDTO.setGender(answer.getConsultant().getUser().getGender());
        consultantDTO.setAddress(answer.getConsultant().getUser().getAddress());
        consultantDTO.setBirthDate(java.sql.Date.valueOf(answer.getConsultant().getUser().getDateOfBirth()));
        consultantDTO.setBiography(answer.getConsultant().getBiography());
        consultantDTO.setQualifications(answer.getConsultant().getQualifications());
        consultantDTO.setExperienceYears(answer.getConsultant().getExperienceYears());
        consultantDTO.setSpecialization(answer.getConsultant().getSpecialization());
        consultantDTO.setProfileImageUrl(answer.getConsultant().getUser().getAvatarUrl());
        dto.setConsultant(consultantDTO);
        
        return dto;
    }
}
