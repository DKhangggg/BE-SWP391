package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.dto.response.BookingResponseDTO;
import com.example.gender_healthcare_service.dto.response.PageResponse;
import com.example.gender_healthcare_service.dto.response.TestingServiceResponseDTO;
import com.example.gender_healthcare_service.entity.Booking;
import com.example.gender_healthcare_service.entity.TestingService;
import com.example.gender_healthcare_service.service.BookingService;
import com.example.gender_healthcare_service.service.TestingServiceService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    @Autowired
    private TestingServiceService testingService;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private BookingService bookingService;


    @GetMapping("/testing-services")
    public ResponseEntity<PageResponse<TestingServiceResponseDTO>> getService(
        @RequestParam(defaultValue = "1") int pageNumber,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<TestingService> services = testingService.getAllServices(pageable);
        PageResponse<TestingServiceResponseDTO> response = new PageResponse<>();
        response.setContent(services.getContent().stream().map(service -> mapper.map(service, TestingServiceResponseDTO.class)).collect(Collectors.toList()));
        response.setPageNumber(services.getNumber() + 1);
        response.setPageSize(services.getSize());
        response.setTotalElements(services.getTotalElements());
        response.setTotalPages(services.getTotalPages());
        response.setHasNext(services.hasNext());
        response.setHasPrevious(services.hasPrevious());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/testing-services/{serviceId}")
    public ResponseEntity<?> getTestingServiceDetails(@PathVariable Integer serviceId) {
        TestingService service = testingService.getServiceById(serviceId);
        if (service == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        TestingServiceResponseDTO dto = mapper.map(service, TestingServiceResponseDTO.class);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("/testing-services/bookings/{bookingId}/results")
    public ResponseEntity<?> getTestingServiceBookingResults(@PathVariable Integer bookingId, Principal principal) {
        BookingResponseDTO booking = bookingService.getBookingByIdAndUsername(bookingId, principal.getName());
        if (booking == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(booking, HttpStatus.OK);
    }

}
