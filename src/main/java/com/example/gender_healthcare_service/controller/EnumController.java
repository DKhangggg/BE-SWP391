package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.entity.enumpackage.MoodType;
import com.example.gender_healthcare_service.util.EnumUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/enums")
public class EnumController {

    @GetMapping("/mood-types")
    public ResponseEntity<Map<String, Object>> getMoodTypes() {
        Map<String, Object> response = new HashMap<>();
        
        // Lấy tất cả các giá trị enum hợp lệ
        List<String> validMoodTypes = EnumUtils.getAllValidMoodTypes();
        
        response.put("validMoodTypes", validMoodTypes);
        response.put("count", validMoodTypes.size());
        response.put("message", "Danh sách các giá trị MoodType hợp lệ");
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test-mood-type")
    public ResponseEntity<Map<String, Object>> testMoodType(String moodString) {
        Map<String, Object> response = new HashMap<>();
        
        MoodType moodType = EnumUtils.safeMoodTypeFromString(moodString);
        
        response.put("input", moodString);
        response.put("isValid", moodType != null);
        response.put("convertedValue", moodType != null ? moodType.name() : null);
        response.put("message", moodType != null ? "Giá trị hợp lệ" : "Giá trị không hợp lệ");
        
        return ResponseEntity.ok(response);
    }
} 