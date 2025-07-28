package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.entity.Feedback;
import com.example.gender_healthcare_service.entity.TestingService;
import com.example.gender_healthcare_service.entity.User;
import com.example.gender_healthcare_service.entity.Consultation;
import com.example.gender_healthcare_service.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    List<Feedback> findByCustomer(User customer);
    List<Feedback> findByConsultant(User consultant);
    List<Feedback> findByService(TestingService service);
    List<Feedback> findByConsultation(Consultation consultation);
    List<Feedback> findByBooking(Booking booking);
    
    // Kiểm tra feedback tồn tại
    boolean existsByConsultationAndCustomer(Consultation consultation, User customer);
    boolean existsByBookingAndCustomer(Booking booking, User customer);
}

