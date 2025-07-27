package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.dto.response.ApiResponse;
import com.example.gender_healthcare_service.dto.response.LocationResponseDTO;
import com.example.gender_healthcare_service.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {
    private final LocationService locationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<LocationResponseDTO>>> getAllLocations() {
        try {
            List<LocationResponseDTO> locations = locationService.getAllActiveLocations();
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách địa điểm thành công", locations));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách địa điểm: " + e.getMessage()));
        }
    }
} 