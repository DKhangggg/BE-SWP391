package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.dto.response.ConsultantDTO;
import com.example.gender_healthcare_service.entity.Consultant;
import com.example.gender_healthcare_service.service.ConsultantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/consultant")
public class ConsultantController {
    @Autowired
    private ConsultantService consultantService;

    @GetMapping("/getConsultantInfo/{consultantId}")
    public ResponseEntity<?> getConsultantInfo(@PathVariable Integer consultantId) {
    ConsultantDTO consultantDTO = consultantService.getConsultantById(consultantId);
        if (consultantDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(consultantDTO);
    }
    @GetMapping("/list")
    public ResponseEntity<?> listAllConsultants() {
        // TODO: Implement list all available consultants logic
        return null;
    }

    @GetMapping("/{consultantId}/details") // Changed to avoid conflict if listConsultantInfo is kept as is
    public ResponseEntity<?> getSpecificConsultantDetails(@PathVariable Long consultantId) {
        // TODO: Implement get specific consultant details logic
        return null;
    }


    @GetMapping("/schedule/{consultantId}")
    public ResponseEntity<?> getConsultantSchedule(@PathVariable Long consultantId) {
        // TODO: Implement get consultant's schedule logic
        return null;
    }

    @PostMapping("/ask-question")
    public ResponseEntity<?> askQuestionToConsultant(/*@RequestBody AskQuestionDTO questionDTO*/) {
        // TODO: Implement allow users to ask questions to consultants logic
        return null;
    }
}
