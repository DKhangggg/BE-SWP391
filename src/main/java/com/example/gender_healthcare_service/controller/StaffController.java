package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.dto.request.StaffRequestDTO;
import com.example.gender_healthcare_service.dto.request.StaffUpdateUserRequestDTO;
import com.example.gender_healthcare_service.dto.response.ApiResponse;
import com.example.gender_healthcare_service.entity.User;
import com.example.gender_healthcare_service.service.StaffService;
import com.example.gender_healthcare_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ApiResponse<?>> getAllStaff() {
        try {
            Object staff = staffService.getAllStaff();
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách nhân viên thành công", staff));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách nhân viên: " + e.getMessage()));
        }
    }

    @GetMapping("/{staffId}")
    public ResponseEntity<ApiResponse<User>> getStaffById(@PathVariable Integer id) {
        try {
            User user = staffService.getStaffById(id);
            if (user != null) {
                return ResponseEntity.ok(ApiResponse.success("Lấy thông tin nhân viên thành công", user));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Không tìm thấy nhân viên"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy thông tin nhân viên: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createStaff(@RequestBody StaffRequestDTO dto) {
        try {
            Object staff = staffService.createStaff(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Tạo nhân viên thành công", staff));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi tạo nhân viên: " + e.getMessage()));
        }
    }

    @PutMapping("/{staffId}")
    public ResponseEntity<ApiResponse<User>> updateStaff(@PathVariable Integer id, @RequestBody StaffRequestDTO dto) {
        try {
            User updated = staffService.updateStaff(id, dto);
            if (updated != null) {
                return ResponseEntity.ok(ApiResponse.success("Cập nhật nhân viên thành công", updated));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Không tìm thấy nhân viên"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi cập nhật nhân viên: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{staffId}")
    public ResponseEntity<ApiResponse<String>> deleteStaff(@PathVariable Integer id) {
        try {
            boolean deleted = staffService.deleteStaff(id);
            if (deleted) {
                return ResponseEntity.ok(ApiResponse.success("Xóa nhân viên thành công", null));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Không tìm thấy nhân viên"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi xóa nhân viên: " + e.getMessage()));
        }
    }

    @PatchMapping("/{staffId}/status")
    public ResponseEntity<ApiResponse<String>> updateStatus(@PathVariable Integer id, @RequestBody StaffUpdateUserRequestDTO dto) {
        try {
            boolean updated = staffService.updateStatus(id, dto.getStatus());
            if (updated) {
                return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái nhân viên thành công", null));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Không tìm thấy nhân viên"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi cập nhật trạng thái nhân viên: " + e.getMessage()));
        }
    }

    // ========== STAFF DASHBOARD APIs ==========
    
    @GetMapping("/dashboard/stats")
    @PreAuthorize("hasAuthority('ROLE_STAFF')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // Mock data cho staff dashboard - có thể implement sau
            stats.put("totalBookings", 150);
            stats.put("pendingBookings", 25);
            stats.put("completedBookings", 120);
            stats.put("todayBookings", 8);
            
            return ResponseEntity.ok(ApiResponse.success("Lấy thống kê dashboard thành công", stats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy thống kê dashboard: " + e.getMessage()));
        }
    }
}
