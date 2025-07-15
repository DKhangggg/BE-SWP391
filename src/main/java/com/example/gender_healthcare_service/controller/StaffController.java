package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.dto.request.StaffRequestDTO;
import com.example.gender_healthcare_service.dto.request.StaffUpdateUserRequestDTO;
import com.example.gender_healthcare_service.entity.User;
import com.example.gender_healthcare_service.service.StaffService;
import com.example.gender_healthcare_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @GetMapping
    public ResponseEntity<?> getAllStaff() {
        return ResponseEntity.ok(staffService.getAllStaff());
    }

    @GetMapping("/{staffId}")
    public ResponseEntity<?> getStaffById(@PathVariable Integer id) {
        User user = staffService.getStaffById(id);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> createStaff(@RequestBody StaffRequestDTO dto) {
        return ResponseEntity.ok(staffService.createStaff(dto));
    }

    @PutMapping("/{staffId}")
    public ResponseEntity<?> updateStaff(@PathVariable Integer id, @RequestBody StaffRequestDTO dto) {
        User updated = staffService.updateStaff(id, dto);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{staffId}")
    public ResponseEntity<?> deleteStaff(@PathVariable Integer id) {
        boolean deleted = staffService.deleteStaff(id);
        return deleted ? ResponseEntity.ok("Staff deleted") : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{staffId}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Integer id, @RequestBody StaffUpdateUserRequestDTO dto) {
        boolean updated = staffService.updateStatus(id, dto.getStatus());
        return updated ? ResponseEntity.ok("Status updated") : ResponseEntity.notFound().build();
    }

}
