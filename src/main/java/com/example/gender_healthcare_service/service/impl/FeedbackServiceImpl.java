package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.dto.request.ConsultantFeedbackDTO;
import com.example.gender_healthcare_service.dto.response.FeedbackResponseDTO;
import com.example.gender_healthcare_service.entity.Feedback;
import com.example.gender_healthcare_service.entity.User;
import com.example.gender_healthcare_service.entity.Consultation;
import com.example.gender_healthcare_service.entity.Booking;
import com.example.gender_healthcare_service.repository.FeedbackRepository;
import com.example.gender_healthcare_service.repository.UserRepository;
import com.example.gender_healthcare_service.repository.ConsultationRepository;
import com.example.gender_healthcare_service.repository.BookingRepository;
import com.example.gender_healthcare_service.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackRepository feedBackRepo;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Override
    public List<Feedback> getAllFeedback() {
        return feedBackRepo.findAll();
    }

    @Override
    public Feedback createFeedback(Feedback feedback) {
        return feedBackRepo.save(feedback);
    }

    @Override
    public Feedback getFeedbackById(Integer id) {
        return feedBackRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found with id: " + id));
    }

    @Override
    public Feedback updateFeedback(Integer id, Feedback newFeedback) {
        Feedback existing = getFeedbackById(id);
        existing.setRating(newFeedback.getRating());
        existing.setComment(newFeedback.getComment());
        existing.setId(newFeedback.getId());
        return feedBackRepo.save(existing);
    }

    @Override
    public FeedbackResponseDTO updateFeedback(Integer id, ConsultantFeedbackDTO feedbackDTO) {
        // Method cũ - giữ lại để backward compatibility
        throw new RuntimeException("Method này đã deprecated. Vui lòng sử dụng method mới với user authentication.");
    }

    @Override
    public FeedbackResponseDTO updateFeedback(Integer id, ConsultantFeedbackDTO feedbackDTO, User currentUser) {
        Feedback existing = getFeedbackById(id);
        
        // Validate consultant
        User consultant = null;
        if (feedbackDTO.getConsultantId() != null) {
            consultant = userRepository.findById(feedbackDTO.getConsultantId())
                    .orElseThrow(() -> new RuntimeException("Consultant not found"));
        }

        // Kiểm tra xem feedback có thuộc về user hiện tại không
        if (!existing.getCustomer().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Bạn chỉ có thể cập nhật feedback của mình");
        }

        // Cập nhật thông tin feedback
        existing.setRating(feedbackDTO.getRating());
        existing.setComment(feedbackDTO.getComment());

        Feedback saved = feedBackRepo.save(existing);

        FeedbackResponseDTO response = new FeedbackResponseDTO();
        response.setId(saved.getId());
        response.setUserId(saved.getCustomer().getId());
        response.setConsultantId(saved.getConsultant() != null ? saved.getConsultant().getId() : null);
        response.setConsultationId(saved.getConsultation() != null ? saved.getConsultation().getId() : null);
        response.setBookingId(saved.getBooking() != null ? saved.getBooking().getId() : null);
        response.setComment(saved.getComment());
        response.setRating(saved.getRating());
        response.setCreatedAt(saved.getCreatedAt() != null ? saved.getCreatedAt().toString() : null);
        response.setHasFeedback(true);
        return response;
    }

    @Override
    public void deleteFeedback(Integer id) {
        // Method cũ - giữ lại để backward compatibility
        throw new RuntimeException("Method này đã deprecated. Vui lòng sử dụng method mới với user authentication.");
    }

    @Override
    public void deleteFeedback(Integer id, User currentUser) {
        Feedback feedback = getFeedbackById(id);
        
        // Kiểm tra xem feedback có thuộc về user hiện tại không
        if (!feedback.getCustomer().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Bạn chỉ có thể xóa feedback của mình");
        }
        
        feedback.setIsDeleted(true);
        feedBackRepo.save(feedback);
    }

    @Override
    public FeedbackResponseDTO submitConsultationFeedback(ConsultantFeedbackDTO feedbackDTO) {
        // Method cũ - giữ lại để backward compatibility
        throw new RuntimeException("Method này đã deprecated. Vui lòng sử dụng method mới với user authentication.");
    }

    @Override
    public FeedbackResponseDTO submitConsultationFeedback(ConsultantFeedbackDTO feedbackDTO, User currentUser) {
        // Validate required fields
        if (feedbackDTO.getRating() == null) {
            throw new RuntimeException("Rating không được để trống");
        }
        
        if (feedbackDTO.getComment() == null || feedbackDTO.getComment().trim().isEmpty()) {
            throw new RuntimeException("Comment không được để trống");
        }
        
        User consultant = null;
        if (feedbackDTO.getConsultantId() != null) {
            consultant = userRepository.findById(feedbackDTO.getConsultantId())
                    .orElseThrow(() -> new RuntimeException("Consultant not found"));
        }

        Consultation consultation = null;
        if (feedbackDTO.getConsultationId() != null) {
            consultation = consultationRepository.findById(feedbackDTO.getConsultationId())
                    .orElseThrow(() -> new RuntimeException("Consultation not found"));
            
            // Kiểm tra xem consultation đã hoàn thành chưa
            if (!consultation.isCompleted()) {
                throw new RuntimeException("Chỉ có thể đánh giá consultation đã hoàn thành");
            }
            
            // Kiểm tra xem customer có phải là customer của consultation này không
            if (!consultation.getCustomer().getId().equals(currentUser.getId())) {
                throw new RuntimeException("Bạn chỉ có thể đánh giá consultation của mình");
            }
        }

        // Validate booking if provided
        Booking booking = null;
        if (feedbackDTO.getBookingId() != null) {
            booking = bookingRepository.findById(feedbackDTO.getBookingId())
                    .orElseThrow(() -> new RuntimeException("Booking not found"));
            
            // Kiểm tra xem booking đã hoàn thành chưa
            if (!booking.isCompleted()) {
                throw new RuntimeException("Chỉ có thể đánh giá booking đã hoàn thành");
            }
            
            // Kiểm tra xem customer có phải là customer của booking này không
            if (!booking.getCustomerID().getId().equals(currentUser.getId())) {
                throw new RuntimeException("Bạn chỉ có thể đánh giá booking của mình");
            }
        }

        // Đảm bảo có ít nhất một trong hai: consultation hoặc booking
        if (consultation == null && booking == null) {
            throw new RuntimeException("Phải cung cấp Consultation ID hoặc Booking ID");
        }

        // Kiểm tra xem đã có feedback cho consultation/booking này chưa
        if (consultation != null) {
            boolean existingFeedback = feedBackRepo.existsByConsultationAndCustomer(consultation, currentUser);
            if (existingFeedback) {
                throw new RuntimeException("Bạn đã đánh giá consultation này rồi");
            }
        }
        
        if (booking != null) {
            boolean existingFeedback = feedBackRepo.existsByBookingAndCustomer(booking, currentUser);
            if (existingFeedback) {
                throw new RuntimeException("Bạn đã đánh giá booking này rồi");
            }
        }

        Feedback feedback = new Feedback();
        feedback.setCustomer(currentUser);
        feedback.setConsultant(consultant);
        feedback.setConsultation(consultation);
        feedback.setBooking(booking);
        feedback.setComment(feedbackDTO.getComment());
        feedback.setRating(feedbackDTO.getRating());
        feedback.setIsDeleted(false);
        feedback.setCreatedAt(LocalDateTime.now());

        Feedback saved = feedBackRepo.save(feedback);

        FeedbackResponseDTO response = new FeedbackResponseDTO();
        response.setId(saved.getId());
        response.setUserId(saved.getCustomer().getId());
        response.setConsultantId(saved.getConsultant() != null ? saved.getConsultant().getId() : null);
        response.setConsultationId(saved.getConsultation() != null ? saved.getConsultation().getId() : null);
        response.setBookingId(saved.getBooking() != null ? saved.getBooking().getId() : null);
        response.setComment(saved.getComment());
        response.setRating(saved.getRating());
        response.setCreatedAt(saved.getCreatedAt() != null ? saved.getCreatedAt().toString() : null);
        response.setHasFeedback(true);
        return response;
    }

    @Override
    public List<FeedbackResponseDTO> getConsultantFeedback(Long consultantId) {
        User consultant = userRepository.findById(consultantId.intValue())
                .orElseThrow(() -> new RuntimeException("Consultant not found"));
        List<Feedback> feedbackList = feedBackRepo.findByConsultant(consultant);
        return feedbackList.stream().map(fb -> {
            FeedbackResponseDTO dto = new FeedbackResponseDTO();
            dto.setId(fb.getId());
            dto.setUserId(fb.getCustomer() != null ? fb.getCustomer().getId() : null);
            dto.setConsultantId(fb.getConsultant() != null ? fb.getConsultant().getId() : null);
            dto.setConsultationId(fb.getConsultation() != null ? fb.getConsultation().getId() : null);
            dto.setBookingId(fb.getBooking() != null ? fb.getBooking().getId() : null);
            dto.setComment(fb.getComment());
            dto.setRating(fb.getRating());
            dto.setCreatedAt(fb.getCreatedAt() != null ? fb.getCreatedAt().toString() : null);
            dto.setHasFeedback(true); // Đánh dấu có feedback
            return dto;
        }).toList();
    }

    @Override
    public FeedbackResponseDTO getConsultationFeedback(Integer consultationId) {
        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new RuntimeException("Consultation not found"));
        
        List<Feedback> feedbacks = feedBackRepo.findByConsultation(consultation);
        if (feedbacks.isEmpty()) {
            return null; // Không có feedback cho consultation này
        }
        
        // Lấy feedback đầu tiên (mỗi consultation chỉ có 1 feedback)
        Feedback feedback = feedbacks.get(0);
        
        FeedbackResponseDTO dto = new FeedbackResponseDTO();
        dto.setId(feedback.getId());
        dto.setUserId(feedback.getCustomer() != null ? feedback.getCustomer().getId() : null);
        dto.setConsultantId(feedback.getConsultant() != null ? feedback.getConsultant().getId() : null);
        dto.setConsultationId(feedback.getConsultation() != null ? feedback.getConsultation().getId() : null);
        dto.setBookingId(feedback.getBooking() != null ? feedback.getBooking().getId() : null);
        dto.setComment(feedback.getComment());
        dto.setRating(feedback.getRating());
        dto.setCreatedAt(feedback.getCreatedAt() != null ? feedback.getCreatedAt().toString() : null);
        dto.setHasFeedback(true); // Đánh dấu có feedback
        return dto;
    }

    @Override
    public FeedbackResponseDTO getBookingFeedback(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        List<Feedback> feedbacks = feedBackRepo.findByBooking(booking);
        if (feedbacks.isEmpty()) {
            return null; // Không có feedback cho booking này
        }
        
        // Lấy feedback đầu tiên (mỗi booking chỉ có 1 feedback)
        Feedback feedback = feedbacks.get(0);
        
        FeedbackResponseDTO dto = new FeedbackResponseDTO();
        dto.setId(feedback.getId());
        dto.setUserId(feedback.getCustomer() != null ? feedback.getCustomer().getId() : null);
        dto.setConsultantId(feedback.getConsultant() != null ? feedback.getConsultant().getId() : null);
        dto.setConsultationId(feedback.getConsultation() != null ? feedback.getConsultation().getId() : null);
        dto.setBookingId(feedback.getBooking() != null ? feedback.getBooking().getId() : null);
        dto.setComment(feedback.getComment());
        dto.setRating(feedback.getRating());
        dto.setCreatedAt(feedback.getCreatedAt() != null ? feedback.getCreatedAt().toString() : null);
        dto.setHasFeedback(true); // Đánh dấu có feedback
        return dto;
    }
}