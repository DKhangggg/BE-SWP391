package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.dto.response.CloudinaryUploadResponseDTO;
import com.example.gender_healthcare_service.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
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
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            log.error("Upload error: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Lỗi khi upload ảnh: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{publicId}")
    public ResponseEntity<?> deleteImage(@PathVariable String publicId) {
        try {
            Map<String, Object> deleteResult = cloudinaryService.deleteImage(publicId);
            return ResponseEntity.ok("Xóa ảnh thành công!");
        } catch (IOException e) {
            log.error("Delete error: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Lỗi khi xóa ảnh: " + e.getMessage());
        }
    }

    @GetMapping("/url/{publicId}")
    public ResponseEntity<?> getImageUrl(@PathVariable String publicId) {
        try {
            String url = cloudinaryService.getImageUrl(publicId);
            return ResponseEntity.ok(Map.of("url", url));
        } catch (Exception e) {
            log.error("Get URL error: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Lỗi khi lấy URL ảnh: " + e.getMessage());
        }
    }
} 