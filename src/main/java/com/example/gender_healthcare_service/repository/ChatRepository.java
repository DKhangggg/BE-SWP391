package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.entity.Chat;
import com.example.gender_healthcare_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Integer> {
    List<Chat> findByCustomer(User customer);
    List<Chat> findByConsultant(User consultant);
}

