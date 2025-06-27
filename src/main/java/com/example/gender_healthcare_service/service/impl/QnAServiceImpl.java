package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.dto.request.AnswerRequestDTO;
import com.example.gender_healthcare_service.dto.request.QuestionRequestDTO;
import com.example.gender_healthcare_service.dto.response.AnswerResponseDTO;
import com.example.gender_healthcare_service.dto.response.QuestionResponseDTO;
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
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import java.util.List;
import java.util.Map;

@Service
public class QnAServiceImpl implements QAService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConsultantRepository consultantRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Override
    public QuestionResponseDTO submitQuestion(QuestionRequestDTO questionRequest) {
        if(questionRequest.getContent()==null||questionRequest.getUserId()==null){
            throw new IllegalArgumentException("Question content and user ID must not be null");
        }
        User user = userRepository.findById(questionRequest.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + questionRequest.getUserId()));
        Question question = new Question();
        question.setContent(questionRequest.getContent());
        question.setUser(user);
        question.setStatus(QuestionStatus.PENDING);
        question.setCreatedAt(LocalDate.now());
        question.setPublic(true);
        question.setUpdatedAt(LocalDate.now());
        question.setCategory(questionRequest.getCategory());
        questionRepository.save(question);
        return modelMapper.map(question, QuestionResponseDTO.class);
    }

    @Override
    public Page<QuestionResponseDTO> getUserQuestions(Integer userId, String status, Pageable pageable) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        if (status == null || status.isEmpty()) {
            return questionRepository.findQuestionsByUser(currentUser, pageable)
                    .map(question -> modelMapper.map(question, QuestionResponseDTO.class));
        }  else {
            QuestionStatus questionStatus = QuestionStatus.valueOf(status.toUpperCase());
            return questionRepository.findAllByStatus(questionStatus, pageable)
                    .map(question -> modelMapper.map(question, QuestionResponseDTO.class));
        }
    }

    @Override
    public Page<QuestionResponseDTO> getConsultantQuestions(String category, Pageable pageable) {
        if( category == null || category.isEmpty()) {
            return questionRepository.findAllByStatus(QuestionStatus.APPROVED, pageable)
                    .map(question -> modelMapper.map(question, QuestionResponseDTO.class));
        }else {
            return questionRepository.findAllByStatusAndCategory(QuestionStatus.APPROVED, category, pageable)
                    .map(question -> modelMapper.map(question, QuestionResponseDTO.class));
        }
    }

    @Override
    public QuestionResponseDTO getQuestionById(Integer questionId) {
        Question question = questionRepository.findQuestionById(questionId);
        if(question == null || question.isDeleted()) {
            throw new IllegalArgumentException("Question not found with ID: " + questionId);
        }
        return modelMapper.map(question, QuestionResponseDTO.class);
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
        answer.setCreatedAt(LocalDate.now());
        answer.setUpdatedAt(LocalDate.now());
        answerRepository.save(answer);

        return modelMapper.map(answer, AnswerResponseDTO.class);
    }

    @Override
    public AnswerResponseDTO updateAnswer(Integer answerId, AnswerRequestDTO answerRequest) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new IllegalArgumentException("Answer not found with ID: " + answerId));

        if (answerRequest.getContent() == null) {
            throw new IllegalArgumentException("Answer content must not be null");
        }

        answer.setContent(answerRequest.getContent());
        answer.setUpdatedAt(LocalDate.now());
        answerRepository.save(answer);

        return modelMapper.map(answer, AnswerResponseDTO.class);

    }

    @Override
    public List<QuestionResponseDTO> getFAQs(String category) {
        if (category == null || category.isEmpty()) {
            return questionRepository.findAllByStatus(QuestionStatus.APPROVED, Pageable.unpaged())
                    .map(question -> modelMapper.map(question, QuestionResponseDTO.class))
                    .getContent();
        } else {
            return questionRepository.findAllByStatusAndCategory(QuestionStatus.APPROVED, category, Pageable.unpaged())
                    .map(question -> modelMapper.map(question, QuestionResponseDTO.class))
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
        question.setUpdatedAt(LocalDate.now());
        questionRepository.save(question);
        return modelMapper.map(question, QuestionResponseDTO.class);
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
                    .map(question -> modelMapper.map(question, QuestionResponseDTO.class));
        } else {
            return questionRepository.searchQuestions(query, pageable)
                    .map(question -> modelMapper.map(question, QuestionResponseDTO.class));
        }
    }
}
