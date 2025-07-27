package com.example.gender_healthcare_service.service;

import java.util.Map;

public interface DefaultImageService {
    
    /**
     * Upload ảnh mặc định lên Cloudinary
     */
    Map<String, Object> uploadDefaultImage(String imageType, String base64Image);
    
    /**
     * Lấy URL ảnh mặc định theo loại
     */
    String getDefaultImageUrl(String imageType);
    
    /**
     * Lấy tất cả ảnh mặc định
     */
    Map<String, String> getAllDefaultImages();
    
    /**
     * Cập nhật ảnh mặc định
     */
    Map<String, Object> updateDefaultImage(String imageType, String base64Image);
    
    /**
     * Xóa ảnh mặc định
     */
    boolean deleteDefaultImage(String imageType);
    
    /**
     * Kiểm tra ảnh mặc định có tồn tại không
     */
    boolean defaultImageExists(String imageType);
} 