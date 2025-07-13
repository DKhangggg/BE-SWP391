package com.example.gender_healthcare_service.service;

import com.example.gender_healthcare_service.dto.response.LocationResponseDTO;
import java.util.List;

public interface LocationService {
    List<LocationResponseDTO> getAllActiveLocations();
} 