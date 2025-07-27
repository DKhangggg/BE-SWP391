package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.dto.response.ApiResponse;
import com.example.gender_healthcare_service.service.DefaultImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/default-images")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class DefaultImageController {

    private final DefaultImageService defaultImageService;


    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> uploadDefaultImage(
            @RequestParam String imageType,
            @RequestParam String base64Image) {
        try {
            Map<String, Object> result = defaultImageService.uploadDefaultImage(imageType, base64Image);
            return ResponseEntity.ok(ApiResponse.success("Upload ảnh mặc định thành công", result));
        } catch (Exception e) {
            log.error("Error uploading default image: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("Lỗi khi upload ảnh mặc định: " + e.getMessage()));
        }
    }

    /**
     * Lấy URL ảnh mặc định theo loại
     */
    @GetMapping("/{imageType}")
    public ResponseEntity<ApiResponse<String>> getDefaultImageUrl(@PathVariable String imageType) {
        try {
            String imageUrl = defaultImageService.getDefaultImageUrl(imageType);
            return ResponseEntity.ok(ApiResponse.success("Lấy URL ảnh mặc định thành công", imageUrl));
        } catch (Exception e) {
            log.error("Error getting default image URL: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("Lỗi khi lấy URL ảnh mặc định: " + e.getMessage()));
        }
    }

    /**
     * Lấy tất cả ảnh mặc định
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, String>>> getAllDefaultImages() {
        try {
            Map<String, String> allImages = defaultImageService.getAllDefaultImages();
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách ảnh mặc định thành công", allImages));
        } catch (Exception e) {
            log.error("Error getting all default images: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("Lỗi khi lấy danh sách ảnh mặc định: " + e.getMessage()));
        }
    }

    /**
     * Cập nhật ảnh mặc định
     */
    @PutMapping("/{imageType}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateDefaultImage(
            @PathVariable String imageType,
            @RequestParam String base64Image) {
        try {
            Map<String, Object> result = defaultImageService.updateDefaultImage(imageType, base64Image);
            return ResponseEntity.ok(ApiResponse.success("Cập nhật ảnh mặc định thành công", result));
        } catch (Exception e) {
            log.error("Error updating default image: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("Lỗi khi cập nhật ảnh mặc định: " + e.getMessage()));
        }
    }

    /**
     * Xóa ảnh mặc định
     */
    @DeleteMapping("/{imageType}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Boolean>> deleteDefaultImage(@PathVariable String imageType) {
        try {
            boolean result = defaultImageService.deleteDefaultImage(imageType);
            if (result) {
                return ResponseEntity.ok(ApiResponse.success("Xóa ảnh mặc định thành công", true));
            } else {
                return ResponseEntity.ok(ApiResponse.success("Ảnh mặc định không tồn tại", false));
            }
        } catch (Exception e) {
            log.error("Error deleting default image: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("Lỗi khi xóa ảnh mặc định: " + e.getMessage()));
        }
    }

    /**
     * Kiểm tra ảnh mặc định có tồn tại không
     */
    @GetMapping("/{imageType}/exists")
    public ResponseEntity<ApiResponse<Boolean>> checkDefaultImageExists(@PathVariable String imageType) {
        try {
            boolean exists = defaultImageService.defaultImageExists(imageType);
            return ResponseEntity.ok(ApiResponse.success("Kiểm tra ảnh mặc định thành công", exists));
        } catch (Exception e) {
            log.error("Error checking default image exists: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("Lỗi khi kiểm tra ảnh mặc định: " + e.getMessage()));
        }
    }
} 