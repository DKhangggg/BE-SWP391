package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.dto.response.ConsultantDTO;
import com.example.gender_healthcare_service.service.ConsultantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/homepage")
public class HomepageController {

    @Autowired
    private ConsultantService consultantService;

    @GetMapping("/details")
    public ResponseEntity<?> getHomepageDetails() {
        return null;
    }

    @GetMapping("/blog/posts")
    public ResponseEntity<?> getBlogPosts() {
        return null;
    }

    @GetMapping("/blog/posts/{postId}")
    public ResponseEntity<?> getBlogPostById(@PathVariable String postId) {
        return null;
    }

    @GetMapping("/consultants")
    public ResponseEntity<List<ConsultantDTO>> getPublicConsultants() {
        try {
            List<ConsultantDTO> consultants = consultantService.getAllConsultants();
            if (consultants == null || consultants.isEmpty()) {
                return ResponseEntity.ok(List.of());
            }
            return ResponseEntity.ok(consultants);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}


