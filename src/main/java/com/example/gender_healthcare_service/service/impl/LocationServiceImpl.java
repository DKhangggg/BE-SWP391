package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.dto.response.LocationResponseDTO;
import com.example.gender_healthcare_service.entity.Location;
import com.example.gender_healthcare_service.repository.LocationRepository;
import com.example.gender_healthcare_service.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<LocationResponseDTO> getAllActiveLocations() {
        List<Location> locations = locationRepository.findByIsDeletedFalse();
        return locations.stream()
                .map(location -> modelMapper.map(location, LocationResponseDTO.class))
                .collect(Collectors.toList());
    }
} 