package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT u FROM User u WHERE u.email = ?1")
    User findUserByEmail(String email);

    User findUserByUsername(String username);

    User findUserById(Integer id);

    @Query("SELECT u FROM User u WHERE u.roleName = ?1")
    List<User> findUserByRoleName(String roleName);

    @Query("SELECT count(u) FROM User u WHERE CAST(u.createdAt AS date) >= :startDate AND CAST(u.createdAt AS date) < :endDate")
    long countByRegistrationDate(LocalDate startDate, LocalDate endDate);

    @Query("SELECT COUNT(DISTINCT b.customerID) FROM Booking b WHERE b.timeSlot.slotDate = :date AND b.isDeleted = false")
    long countActiveUsersByDate(LocalDate date);

    @Query("SELECT count(u) FROM User u WHERE CAST(u.createdAt AS date) < :endDate")
    long countByRegistrationDateBefore(LocalDate endDate);
    List<User> findAllByRoleNameAndIsDeletedFalse(String roleName);
    Optional<User> findByIdAndRoleName(Integer id, String roleName);
}