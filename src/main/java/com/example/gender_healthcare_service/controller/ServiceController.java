package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.dto.response.ApiResponse;
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
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<ApiResponse<PageResponse<TestingServiceResponseDTO>>> getService(
        @RequestParam(defaultValue = "1") int pageNumber,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        try {
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
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách dịch vụ xét nghiệm thành công", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách dịch vụ xét nghiệm: " + e.getMessage()));
        }
    }

    @GetMapping("/testing-services/{serviceId}")
    public ResponseEntity<ApiResponse<TestingServiceResponseDTO>> getTestingServiceDetails(@PathVariable Integer serviceId) {
        try {
            TestingService service = testingService.getServiceById(serviceId);
            if (service == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Không tìm thấy dịch vụ xét nghiệm với ID: " + serviceId));
            }
            TestingServiceResponseDTO dto = mapper.map(service, TestingServiceResponseDTO.class);
            return ResponseEntity.ok(ApiResponse.success("Lấy chi tiết dịch vụ xét nghiệm thành công", dto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy chi tiết dịch vụ xét nghiệm: " + e.getMessage()));
        }
    }

    @GetMapping("/testing-services/bookings/{bookingId}/results")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> getTestingServiceBookingResults(@PathVariable Integer bookingId, Principal principal) {
        try {
            BookingResponseDTO booking = bookingService.getBookingByIdAndUsername(bookingId, principal.getName());
            if (booking == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Không tìm thấy booking với ID: " + bookingId));
            }
            return ResponseEntity.ok(ApiResponse.success("Lấy kết quả booking thành công", booking));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy kết quả booking: " + e.getMessage()));
        }
    }

    @PostMapping("/testing-services/{serviceId}/image")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadServiceImage(
            @PathVariable Integer serviceId,
            @RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = testingService.uploadServiceImage(file, serviceId);
            return ResponseEntity.ok(ApiResponse.success("Upload ảnh dịch vụ thành công", Map.of("imageUrl", imageUrl)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Lỗi khi upload ảnh dịch vụ: " + e.getMessage()));
        }
    }

}
