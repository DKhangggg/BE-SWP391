package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.dto.request.BookingRequestDTO;
import com.example.gender_healthcare_service.dto.request.BookingFilterRequestDTO;
import com.example.gender_healthcare_service.dto.request.ConsultantCreateBookingRequestDTO;
import com.example.gender_healthcare_service.dto.request.UpdateBookingStatusRequestDTO;
import com.example.gender_healthcare_service.dto.request.UpdateTestResultRequestDTO;
import com.example.gender_healthcare_service.dto.request.SampleCollectionRequestDTO;
import com.example.gender_healthcare_service.dto.response.BookingResponseDTO;
import com.example.gender_healthcare_service.dto.response.BookingPageResponseDTO;
import com.example.gender_healthcare_service.dto.response.SampleCollectionResponseDTO;
import com.example.gender_healthcare_service.dto.response.PageResponse;
import com.example.gender_healthcare_service.dto.response.ApiResponse;
import com.example.gender_healthcare_service.service.BookingService;
import com.example.gender_healthcare_service.exception.ServiceNotFoundException;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;

/**
 * WORKFLOW BOOKING SYSTEM CONTROLLER
 * 
 * This controller handles the complete booking workflow for testing services:
 * 1. Customer creates booking (PENDING)
 * 2. Customer confirms booking (CONFIRMED) 
 * 3. Staff collects sample (SAMPLE_COLLECTED)
 * 4. Staff uploads test results (COMPLETED)
 * 5. Customer views results
 * 
 * Real-time updates are sent via WebSocket to keep customers informed
 * of their booking status changes.
 */
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    @Autowired
    private BookingService bookingService;

    /**
     * WORKFLOW STEP 1: Customer đặt lịch xét nghiệm
     * 
     * Frontend: SWP391_FE/src/pages/User/STITesting/index.jsx
     * - Customer chọn service và timeslot
     * - Gọi API này để tạo booking với status PENDING
     * - Sau khi tạo thành công, customer được redirect đến BookingConfirmation
     * 
     * WebSocket: Tự động trigger notification PENDING → customer tracking page
     * 
     * @param bookingRequestDTO Chứa serviceId, timeSlotId, customerNotes
     * @return BookingResponseDTO với bookingId và status PENDING
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> createBooking(@RequestBody BookingRequestDTO bookingRequestDTO) {
        try {
            BookingResponseDTO createdBooking = bookingService.createBooking(bookingRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Đặt lịch thành công", createdBooking));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi đặt lịch: " + e.getMessage()));
        }
    }

    /**
     * Consultant tạo booking cho customer
     * 
     * Frontend: SWP391_FE/src/pages/Consultant/CreateAppointment.jsx
     * - Consultant có thể tạo booking thay cho customer
     * - Tương tự createBooking nhưng với quyền consultant
     */
    @PostMapping("/consultant-create")
    @PreAuthorize("hasAuthority('ROLE_CONSULTANT')")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> createBookingForUser(@RequestBody ConsultantCreateBookingRequestDTO bookingRequestDTO) {
        try {
            BookingResponseDTO createdBooking = bookingService.createBookingForUser(bookingRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Tạo lịch hẹn cho người dùng thành công", createdBooking));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi tạo lịch hẹn: " + e.getMessage()));
        }
    }

    /**
     * Lấy danh sách booking của customer hiện tại
     * 
     * Frontend: SWP391_FE/src/pages/User/Dashboard/index.jsx
     * - Hiển thị tất cả booking của customer (PENDING, CONFIRMED, SAMPLE_COLLECTED, COMPLETED)
     * - Customer có thể click vào từng booking để xem chi tiết hoặc tracking
     * 
     * @return List<BookingResponseDTO> với đầy đủ thông tin booking
     */
    @GetMapping("/my-bookings")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<BookingResponseDTO>>> getCurrentUserBookings() {
        try {
            List<BookingResponseDTO> bookings = bookingService.getUserBookings();
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách lịch hẹn thành công", bookings));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách lịch hẹn: " + e.getMessage()));
        }
    }

    /**
     * WORKFLOW STEP 5: Customer xem chi tiết booking và kết quả xét nghiệm
     * 
     * Frontend: SWP391_FE/src/components/TestResultModal.jsx
     * - Customer click "Xem kết quả" từ notification hoặc booking list
     * - Hiển thị modal với đầy đủ thông tin: kết quả, tên bác sĩ, ngày lấy mẫu
     * - Priority hiển thị doctorName: result.doctorName > sampleCollectionProfile.doctorName > fallback
     * 
     * @param bookingId ID của booking cần xem
     * @return BookingResponseDTO với đầy đủ thông tin kết quả và doctorName
     */
    @GetMapping("/{bookingId}/my-booking")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> getBookingByIdForCurrentUser(@PathVariable Integer bookingId) {
        try {
            BookingResponseDTO booking = bookingService.getBookingByIdForUser(bookingId);
            return ResponseEntity.ok(ApiResponse.success("Lấy thông tin lịch hẹn thành công", booking));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy thông tin lịch hẹn: " + e.getMessage()));
        }
    }

    /**
     * Admin xem chi tiết booking (có quyền xem tất cả booking)
     * 
     * Frontend: SWP391_FE/src/pages/admin/AdminOrderDetails.jsx
     * - Admin có thể xem chi tiết bất kỳ booking nào
     * - Hiển thị thông tin đầy đủ cho admin quản lý
     */
    @GetMapping("/{bookingId}/admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> getBookingByIdForAdmin(@PathVariable Integer bookingId) {
        try {
            BookingResponseDTO booking = bookingService.getBookingByIdForAdmin(bookingId);
            return ResponseEntity.ok(ApiResponse.success("Lấy thông tin lịch hẹn thành công", booking));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy thông tin lịch hẹn: " + e.getMessage()));
        }
    }

    /**
     * Staff/Admin cập nhật trạng thái booking
     * 
     * Frontend: SWP391_FE/src/pages/Staff/StaffAppointments.jsx
     * - Staff có thể cập nhật trạng thái booking (CONFIRMED, SAMPLE_COLLECTED, COMPLETED)
     * - Trigger WebSocket notification đến customer tracking page
     * 
     * @param bookingId ID của booking cần cập nhật
     * @param statusRequestDTO Chứa status mới và notes
     * @return BookingResponseDTO với status đã cập nhật
     */
    @PatchMapping("/{bookingId}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> updateBookingStatus(@PathVariable Integer bookingId, @RequestBody UpdateBookingStatusRequestDTO statusRequestDTO) {
        try {
            BookingResponseDTO updatedBooking = bookingService.updateBookingStatus(bookingId, statusRequestDTO);
            return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái lịch hẹn thành công", updatedBooking));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi cập nhật trạng thái lịch hẹn: " + e.getMessage()));
        }
    }

    /**
     * Customer/Admin hủy booking
     * 
     * Frontend: SWP391_FE/src/pages/User/Dashboard/index.jsx
     * - Customer có thể hủy booking nếu chưa được xác nhận
     * - Trigger WebSocket notification về việc hủy booking
     * 
     * @param bookingId ID của booking cần hủy
     * @return BookingResponseDTO với status CANCELLED
     */
    @PatchMapping("/{bookingId}/cancel")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> cancelBooking(@PathVariable Integer bookingId) {
        try {
            BookingResponseDTO cancelledBooking = bookingService.cancelBooking(bookingId);
            return ResponseEntity.ok(ApiResponse.success("Hủy lịch hẹn thành công", cancelledBooking));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi hủy lịch hẹn: " + e.getMessage()));
        }
    }

    /**
     * WORKFLOW STEP 2: Customer xác nhận booking
     * 
     * Frontend: SWP391_FE/src/pages/User/BookingConfirmation.jsx
     * - Customer xác nhận booking sau khi đặt lịch
     * - Chuyển status từ PENDING → CONFIRMED
     * - Trigger WebSocket notification
     * 
     * @param bookingId ID của booking cần xác nhận
     * @return BookingResponseDTO với status CONFIRMED
     */
    @PatchMapping("/{bookingId}/confirm")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> confirmBooking(@PathVariable Integer bookingId) {
        try {
            BookingResponseDTO confirmedBooking = bookingService.confirmBooking(bookingId);
            return ResponseEntity.ok(ApiResponse.success("Xác nhận lịch hẹn thành công", confirmedBooking));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi xác nhận lịch hẹn: " + e.getMessage()));
        }
    }

    /**
     * Customer/Admin hủy booking với response
     * 
     * Frontend: SWP391_FE/src/pages/User/Dashboard/index.jsx
     * - Tương tự cancelBooking nhưng trả về response khác
     */
    @PatchMapping("/{bookingId}/cancel-with-response")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> cancelBookingWithResponse(@PathVariable Integer bookingId) {
        try {
            ResponseEntity<?> response = bookingService.cancelBookingWithResponse(bookingId);
            if (response.getBody() instanceof BookingResponseDTO) {
                return ResponseEntity.ok(ApiResponse.success("Hủy lịch hẹn thành công", (BookingResponseDTO) response.getBody()));
            } else {
                return ResponseEntity.ok(ApiResponse.success("Hủy lịch hẹn thành công", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi hủy lịch hẹn: " + e.getMessage()));
        }
    }

    /**
     * WORKFLOW STEP 4: Staff cập nhật kết quả xét nghiệm
     * 
     * Frontend: SWP391_FE/src/components/staff/TestResultForm.jsx
     * - Staff nhập kết quả xét nghiệm chi tiết
     * - Tự động chuyển status từ SAMPLE_COLLECTED → COMPLETED
     * - Trigger WebSocket notification đến customer tracking page
     * - Customer nhận được real-time update và có thể xem kết quả ngay
     * 
     * @param bookingId ID của booking cần cập nhật kết quả
     * @param resultRequest Chứa result, resultType, notes, resultDate
     * @return BookingResponseDTO với status COMPLETED và kết quả
     */
    @PatchMapping("/{bookingId}/test-result")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> updateTestResult(@PathVariable Integer bookingId,
                                                              @RequestBody UpdateTestResultRequestDTO resultRequest) {
        try {
            // Cập nhật kết quả xét nghiệm trong database
            BookingResponseDTO updatedBooking = bookingService.updateTestResult(bookingId, resultRequest);
            return ResponseEntity.ok(ApiResponse.success("Cập nhật kết quả xét nghiệm thành công", updatedBooking));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi cập nhật kết quả xét nghiệm: " + e.getMessage()));
        }
    }

    /**
     * WORKFLOW STEP 3: Staff lấy mẫu xét nghiệm và nhập tên bác sĩ phụ trách
     * 
     * Frontend: SWP391_FE/src/components/staff/SampleCollectionForm.jsx
     * - Staff điền thông tin người lấy mẫu và tên bác sĩ phụ trách
     * - Tạo SampleCollectionProfile với doctorName field
     * - Chuyển status từ CONFIRMED → SAMPLE_COLLECTED
     * - Trigger WebSocket notification: PENDING → SAMPLE_COLLECTED
     * - Customer nhận được real-time update về việc đã lấy mẫu
     * 
     * @param bookingId ID của booking cần lấy mẫu
     * @param sampleCollectionRequest Chứa thông tin người lấy mẫu và doctorName
     * @return BookingResponseDTO với status SAMPLE_COLLECTED và SampleCollectionProfile
     */
    @PostMapping("/{bookingId}/sample-collection")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> collectSample(
            @PathVariable Integer bookingId,
            @RequestBody @Valid SampleCollectionRequestDTO sampleCollectionRequest) {
        try {
            BookingResponseDTO updatedBooking = bookingService.collectSample(bookingId, sampleCollectionRequest);
            return ResponseEntity.ok(ApiResponse.success("Lấy mẫu xét nghiệm thành công", updatedBooking));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy mẫu xét nghiệm: " + e.getMessage()));
        }
    }

    /**
     * Lấy thông tin SampleCollectionProfile của booking
     * 
     * Frontend: SWP391_FE/src/components/staff/SampleCollectionForm.jsx
     * - Staff có thể xem và chỉnh sửa thông tin mẫu đã lấy
     * - Customer có thể xem thông tin mẫu trong TestResultModal
     * 
     * @param bookingId ID của booking cần xem thông tin mẫu
     * @return SampleCollectionResponseDTO với thông tin chi tiết mẫu
     */
    @GetMapping("/{bookingId}/sample-collection")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF', 'ROLE_MANAGER', 'ROLE_CUSTOMER')")
    public ResponseEntity<ApiResponse<SampleCollectionResponseDTO>> getSampleCollectionProfile(@PathVariable Integer bookingId) {
        try {
            SampleCollectionResponseDTO profile = bookingService.getSampleCollectionProfile(bookingId);
            return ResponseEntity.ok(ApiResponse.success("Lấy thông tin mẫu xét nghiệm thành công", profile));
        } catch (ServiceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy thông tin mẫu xét nghiệm: " + e.getMessage()));
        }
    }

    /**
     * Cập nhật thông tin SampleCollectionProfile
     * 
     * Frontend: SWP391_FE/src/components/staff/SampleCollectionForm.jsx
     * - Staff có thể chỉnh sửa thông tin mẫu đã lấy
     * - Cập nhật doctorName và các thông tin khác
     * 
     * @param bookingId ID của booking cần cập nhật thông tin mẫu
     * @param sampleCollectionRequest Thông tin mẫu mới
     * @return BookingResponseDTO với SampleCollectionProfile đã cập nhật
     */
    @PutMapping("/{bookingId}/sample-collection")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> updateSampleCollectionProfile(
            @PathVariable Integer bookingId,
            @RequestBody @Valid SampleCollectionRequestDTO sampleCollectionRequest) {
        try {
            BookingResponseDTO updatedBooking = bookingService.updateSampleCollectionProfile(bookingId, sampleCollectionRequest);
            return ResponseEntity.ok(ApiResponse.success("Cập nhật thông tin mẫu xét nghiệm thành công", updatedBooking));
        } catch (ServiceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi cập nhật thông tin mẫu xét nghiệm: " + e.getMessage()));
        }
    }

    /**
     * Staff/Admin xem tất cả booking với phân trang
     * 
     * Frontend: SWP391_FE/src/pages/Staff/StaffAppointments.jsx
     * - Staff xem danh sách tất cả booking để quản lý
     * - Có thể filter theo status, customer, service, date range
     * - Hiển thị booking theo từng status: PENDING, CONFIRMED, SAMPLE_COLLECTED, COMPLETED
     * 
     * @param pageNumber Số trang (default: 1)
     * @param pageSize Số booking mỗi trang (default: 10)
     * @return BookingPageResponseDTO với danh sách booking và thông tin phân trang
     */
    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<BookingPageResponseDTO>> getAllBookingsForStaffOrManager(
        @RequestParam(defaultValue = "1") int pageNumber,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
            BookingPageResponseDTO bookings = bookingService.getAllBookingsForStaff(pageable);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách lịch hẹn thành công", bookings));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách lịch hẹn: " + e.getMessage()));
        }
    }

    /**
     * Staff/Admin filter booking theo nhiều tiêu chí
     * 
     * Frontend: SWP391_FE/src/pages/Staff/StaffAppointments.jsx
     * - Staff có thể filter booking theo: status, customer, service, date range
     * - Tìm kiếm theo tên customer hoặc tên service
     * - Sắp xếp theo field và direction
     * - Hỗ trợ pagination
     * 
     * @param pageNumber Số trang
     * @param pageSize Số booking mỗi trang
     * @param status Filter theo status (PENDING, CONFIRMED, SAMPLE_COLLECTED, COMPLETED)
     * @param customerId Filter theo customer ID
     * @param serviceId Filter theo service ID
     * @param fromDate Filter từ ngày
     * @param toDate Filter đến ngày
     * @param customerName Tìm kiếm theo tên customer
     * @param serviceName Tìm kiếm theo tên service
     * @param sortBy Field để sắp xếp (default: createdAt)
     * @param sortDirection Hướng sắp xếp (ASC/DESC, default: DESC)
     * @return BookingPageResponseDTO với danh sách booking đã filter
     */
    @GetMapping("/filter")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<BookingPageResponseDTO>> getBookingsWithFilters(
        @RequestParam(defaultValue = "1") int pageNumber,
        @RequestParam(defaultValue = "10") int pageSize,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) Integer customerId,
        @RequestParam(required = false) Integer serviceId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
        @RequestParam(required = false) String customerName,
        @RequestParam(required = false) String serviceName,
        @RequestParam(defaultValue = "createdAt") String sortBy,
        @RequestParam(defaultValue = "DESC") String sortDirection
    ) {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
            BookingFilterRequestDTO filter = new BookingFilterRequestDTO();
            filter.setStatus(status);
            filter.setCustomerId(customerId);
            filter.setServiceId(serviceId);
            filter.setFromDate(fromDate);
            filter.setToDate(toDate);
            filter.setCustomerName(customerName);
            filter.setServiceName(serviceName);
            filter.setSortBy(sortBy);
            filter.setSortDirection(sortDirection);

            BookingPageResponseDTO bookings = bookingService.getBookingsWithFilters(filter, pageable);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách lịch hẹn thành công", bookings));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách lịch hẹn: " + e.getMessage()));
        }
    }

    /**
     * Staff/Admin xem booking theo status cụ thể
     * 
     * Frontend: SWP391_FE/src/pages/Staff/StaffAppointments.jsx
     * - Staff có thể xem booking theo từng status riêng biệt
     * - Ví dụ: xem tất cả booking PENDING để xác nhận
     * - Xem tất cả booking SAMPLE_COLLECTED để upload kết quả
     * 
     * @param status Status cần filter (PENDING, CONFIRMED, SAMPLE_COLLECTED, COMPLETED)
     * @param pageNumber Số trang
     * @param pageSize Số booking mỗi trang
     * @return BookingPageResponseDTO với danh sách booking theo status
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<BookingPageResponseDTO>> getBookingsByStatus(
        @PathVariable String status,
        @RequestParam(defaultValue = "1") int pageNumber,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
            BookingPageResponseDTO bookings = bookingService.getBookingsByStatus(status, pageable);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách lịch hẹn theo trạng thái thành công", bookings));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách lịch hẹn: " + e.getMessage()));
        }
    }

    /**
     * Staff/Admin xem booking theo customer cụ thể
     * 
     * Frontend: SWP391_FE/src/pages/Staff/StaffAppointments.jsx
     * - Staff có thể xem tất cả booking của một customer
     * - Hữu ích khi customer gọi điện hỏi về booking của họ
     * 
     * @param customerId ID của customer cần xem booking
     * @param pageNumber Số trang
     * @param pageSize Số booking mỗi trang
     * @return BookingPageResponseDTO với danh sách booking của customer
     */
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<BookingPageResponseDTO>> getBookingsByCustomer(
        @PathVariable Integer customerId,
        @RequestParam(defaultValue = "1") int pageNumber,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
            BookingPageResponseDTO bookings = bookingService.getBookingsByCustomer(customerId, pageable);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách lịch hẹn theo khách hàng thành công", bookings));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách lịch hẹn: " + e.getMessage()));
        }
    }

    /**
     * Staff/Admin xem booking theo service cụ thể
     * 
     * Frontend: SWP391_FE/src/pages/Staff/StaffAppointments.jsx
     * - Staff có thể xem tất cả booking của một service
     * - Hữu ích để quản lý workload theo từng loại xét nghiệm
     * 
     * @param serviceId ID của service cần xem booking
     * @param pageNumber Số trang
     * @param pageSize Số booking mỗi trang
     * @return BookingPageResponseDTO với danh sách booking của service
     */
    @GetMapping("/service/{serviceId}")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<BookingPageResponseDTO>> getBookingsByService(
        @PathVariable Integer serviceId,
        @RequestParam(defaultValue = "1") int pageNumber,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
            BookingPageResponseDTO bookings = bookingService.getBookingsByService(serviceId, pageable);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách lịch hẹn theo dịch vụ thành công", bookings));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách lịch hẹn: " + e.getMessage()));
        }
    }

    /**
     * Staff/Admin xem booking theo khoảng thời gian
     * 
     * Frontend: SWP391_FE/src/pages/Staff/StaffAppointments.jsx
     * - Staff có thể xem booking trong một khoảng thời gian cụ thể
     * - Hữu ích để lập báo cáo theo ngày/tuần/tháng
     * 
     * @param fromDate Ngày bắt đầu
     * @param toDate Ngày kết thúc
     * @param pageNumber Số trang
     * @param pageSize Số booking mỗi trang
     * @return BookingPageResponseDTO với danh sách booking trong khoảng thời gian
     */
    @GetMapping("/date-range")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<BookingPageResponseDTO>> getBookingsByDateRange(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
        @RequestParam(defaultValue = "1") int pageNumber,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
            BookingPageResponseDTO bookings = bookingService.getBookingsByDateRange(fromDate, toDate, pageable);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách lịch hẹn theo khoảng thời gian thành công", bookings));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách lịch hẹn: " + e.getMessage()));
        }
    }

    /**
     * Staff/Admin xem tổng số booking
     * 
     * Frontend: SWP391_FE/src/pages/Staff/StaffDashboard.jsx
     * - Hiển thị tổng số booking trong dashboard
     * - Dùng để thống kê tổng quan
     * 
     * @return Long với tổng số booking
     */
    @GetMapping("/statistics/total")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getTotalBookings() {
        try {
            long totalBookings = bookingService.getTotalBookings();
            return ResponseEntity.ok(ApiResponse.success("Lấy tổng số lịch hẹn thành công", totalBookings));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy tổng số lịch hẹn: " + e.getMessage()));
        }
    }

    /**
     * Staff/Admin xem số booking theo status
     * 
     * Frontend: SWP391_FE/src/pages/Staff/StaffDashboard.jsx
     * - Hiển thị số booking theo từng status trong dashboard
     * - Ví dụ: 10 PENDING, 5 CONFIRMED, 3 SAMPLE_COLLECTED, 20 COMPLETED
     * 
     * @param status Status cần đếm
     * @return Long với số booking theo status
     */
    @GetMapping("/statistics/status/{status}")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getBookingsByStatus(@PathVariable String status) {
        try {
            long count = bookingService.getBookingsByStatus(status);
            return ResponseEntity.ok(ApiResponse.success("Lấy số lịch hẹn theo trạng thái thành công", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy số lịch hẹn: " + e.getMessage()));
        }
    }

    /**
     * Staff/Admin xem số booking theo customer
     * 
     * Frontend: SWP391_FE/src/pages/Staff/StaffDashboard.jsx
     * - Hiển thị số booking của một customer cụ thể
     * - Dùng để phân tích customer behavior
     * 
     * @param customerId ID của customer cần đếm booking
     * @return Long với số booking của customer
     */
    @GetMapping("/statistics/customer/{customerId}")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getBookingsByCustomer(@PathVariable Integer customerId) {
        try {
            long count = bookingService.getBookingsByCustomer(customerId);
            return ResponseEntity.ok(ApiResponse.success("Lấy số lịch hẹn theo khách hàng thành công", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy số lịch hẹn: " + e.getMessage()));
        }
    }

    /**
     * Staff/Admin xem số booking theo service
     * 
     * Frontend: SWP391_FE/src/pages/Staff/StaffDashboard.jsx
     * - Hiển thị số booking của một service cụ thể
     * - Dùng để phân tích popularity của từng service
     * 
     * @param serviceId ID của service cần đếm booking
     * @return Long với số booking của service
     */
    @GetMapping("/statistics/service/{serviceId}")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getBookingsByService(@PathVariable Integer serviceId) {
        try {
            long count = bookingService.getBookingsByService(serviceId);
            return ResponseEntity.ok(ApiResponse.success("Lấy số lịch hẹn theo dịch vụ thành công", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy số lịch hẹn: " + e.getMessage()));
        }
    }

    /**
     * Staff/Admin xem số booking theo khoảng thời gian
     * 
     * Frontend: SWP391_FE/src/pages/Staff/StaffDashboard.jsx
     * - Hiển thị số booking trong một khoảng thời gian
     * - Dùng để lập báo cáo theo thời gian
     * 
     * @param fromDate Ngày bắt đầu
     * @param toDate Ngày kết thúc
     * @return Long với số booking trong khoảng thời gian
     */
    @GetMapping("/statistics/date-range")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getBookingsByDateRange(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        try {
            long count = bookingService.getBookingsByDateRange(fromDate, toDate);
            return ResponseEntity.ok(ApiResponse.success("Lấy số lịch hẹn theo khoảng thời gian thành công", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy số lịch hẹn: " + e.getMessage()));
        }
    }

    /**
     * Legacy API - Staff/Admin xem tất cả booking (cũ)
     * 
     * Frontend: SWP391_FE/src/pages/Staff/StaffAppointments.jsx (cũ)
     * - API cũ, được giữ lại để tương thích ngược
     * - Sử dụng PageResponse thay vì BookingPageResponseDTO
     * 
     * @param pageNumber Số trang
     * @param pageSize Số booking mỗi trang
     * @return PageResponse<BookingResponseDTO> với danh sách booking
     */
    @GetMapping("/all-legacy")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<BookingResponseDTO>>> getAllBookingsLegacy(
        @RequestParam(defaultValue = "1") int pageNumber,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
            Page<BookingResponseDTO> bookings = bookingService.getAllBookingsForStaffLegacy(pageable);
            PageResponse<BookingResponseDTO> response = new PageResponse<>();
            response.setContent(bookings.getContent());
            response.setPageNumber(bookings.getNumber() + 1);
            response.setPageSize(bookings.getSize());
            response.setTotalElements(bookings.getTotalElements());
            response.setTotalPages(bookings.getTotalPages());
            response.setHasNext(bookings.hasNext());
            response.setHasPrevious(bookings.hasPrevious());
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách lịch hẹn thành công", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách lịch hẹn: " + e.getMessage()));
        }
    }
}
