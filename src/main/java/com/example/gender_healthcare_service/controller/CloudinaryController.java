package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.dto.response.ApiResponse;
import com.example.gender_healthcare_service.dto.response.CloudinaryUploadResponseDTO;
import com.example.gender_healthcare_service.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/cloudinary")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class CloudinaryController {

    private final CloudinaryService cloudinaryService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<CloudinaryUploadResponseDTO>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            Map<String, Object> uploadResult = cloudinaryService.uploadImage(file);
            
            CloudinaryUploadResponseDTO response = new CloudinaryUploadResponseDTO(
                "Upload thành công!",
                (String) uploadResult.get("public_id"),
                (String) uploadResult.get("url"),
                (String) uploadResult.get("secure_url"),
                (String) uploadResult.get("format"),
                ((Number) uploadResult.get("bytes")).longValue(),
                ((Number) uploadResult.get("width")).intValue(),
                ((Number) uploadResult.get("height")).intValue(),
                (String) uploadResult.get("resource_type"),
                (String) uploadResult.get("created_at")
            );
            
            return ResponseEntity.ok(ApiResponse.success("Upload ảnh thành công", response));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (IOException e) {
            log.error("Upload error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi upload ảnh: " + e.getMessage()));
        }
    }

    @DeleteMapping("/delete/{publicId}")
    public ResponseEntity<ApiResponse<String>> deleteImage(@PathVariable String publicId) {
        try {
            Map<String, Object> deleteResult = cloudinaryService.deleteImage(publicId);
            return ResponseEntity.ok(ApiResponse.success("Xóa ảnh thành công!", null));
        } catch (IOException e) {
            log.error("Delete error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi xóa ảnh: " + e.getMessage()));
        }
    }

    @GetMapping("/url/{publicId}")
    public ResponseEntity<ApiResponse<Map<String, String>>> getImageUrl(@PathVariable String publicId) {
        try {
            String url = cloudinaryService.getImageUrl(publicId);
            return ResponseEntity.ok(ApiResponse.success("Lấy URL ảnh thành công", Map.of("url", url)));
        } catch (Exception e) {
            log.error("Get URL error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy URL ảnh: " + e.getMessage()));
        }
    }
} 