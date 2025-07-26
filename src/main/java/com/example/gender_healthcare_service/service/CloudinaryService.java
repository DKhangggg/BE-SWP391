package com.example.gender_healthcare_service.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface CloudinaryService {
    

    Map<String, Object> uploadImage(MultipartFile file) throws IOException;
    

    Map<String, Object> deleteImage(String publicId) throws IOException;

    String getImageUrl(String publicId);

    /**
     * Kiểm tra ảnh có tồn tại trên Cloudinary không
     */
    boolean imageExists(String publicId);

    /**
     * Lấy thông tin ảnh từ Cloudinary
     */
    Map<String, Object> getImageInfo(String publicId);

    /**
     * Tạo URL ảnh với fallback
     */
    String getImageUrlWithFallback(String publicId, String fallbackUrl);

    /**
     * Xóa ảnh cũ khi upload ảnh mới
     */
    void deleteOldImage(String oldPublicId);
} 