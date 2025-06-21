package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findUserByEmail(String email);
    User findUserByUsername(String username);
    User findUserById(Integer id);

    @Query("SELECT u FROM User u WHERE u.roleName = ?1")
    List<User> findUserByRoleName(String roleName);
    }
