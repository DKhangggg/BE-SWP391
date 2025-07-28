package com.example.gender_healthcare_service.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface CloudinaryService {
    Map<String, Object> uploadImage(MultipartFile file) throws IOException;
    Map<String, Object> deleteImage(String publicId) throws IOException;
    String getImageUrl(String publicId);
    boolean imageExists(String publicId);
    Map<String, Object> getImageInfo(String publicId);
    String getImageUrlWithFallback(String publicId, String fallbackUrl);
    void deleteOldImage(String oldPublicId);
}