package com.example.gender_healthcare_service.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.gender_healthcare_service.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    @Override
    public Map<String, Object> uploadImage(MultipartFile file) throws IOException {
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("File không được để trống!");
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("Chỉ chấp nhận file ảnh!");
            }

            // Check file size (max 10MB)
            if (file.getSize() > 10 * 1024 * 1024) {
                throw new IllegalArgumentException("Kích thước file không được vượt quá 10MB!");
            }

            // Upload to Cloudinary
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                    "folder", "gender-healthcare",
                    "resource_type", "image",
                    "allowed_formats", new String[]{"jpg", "jpeg", "png", "gif", "webp"},
                    "transformation", "f_auto,q_auto"
                )
            );

            log.info("Image uploaded successfully: {}", uploadResult.get("public_id"));
            return uploadResult;

        } catch (Exception e) {
            log.error("Error uploading image: {}", e.getMessage());
            throw new IOException("Lỗi khi upload ảnh: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> deleteImage(String publicId) throws IOException {
        try {
            Map<String, Object> deleteResult = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("Image deleted successfully: {}", publicId);
            return deleteResult;
        } catch (Exception e) {
            log.error("Error deleting image: {}", e.getMessage());
            throw new IOException("Lỗi khi xóa ảnh: " + e.getMessage());
        }
    }

    @Override
    public String getImageUrl(String publicId) {
        return cloudinary.url().generate(publicId);
    }

    /**
     * Kiểm tra ảnh có tồn tại trên Cloudinary không
     */
    @Override
    public boolean imageExists(String publicId) {
        try {
            Map<String, Object> result = cloudinary.api().resource(publicId, ObjectUtils.emptyMap());
            return result != null && result.get("public_id") != null;
        } catch (Exception e) {
            log.warn("Image not found on Cloudinary: {}", publicId);
            return false;
        }
    }

    /**
     * Lấy thông tin ảnh từ Cloudinary
     */
    @Override
    public Map<String, Object> getImageInfo(String publicId) {
        try {
            return cloudinary.api().resource(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            log.error("Error getting image info: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Tạo URL ảnh với fallback
     */
    @Override
    public String getImageUrlWithFallback(String publicId, String fallbackUrl) {
        if (publicId == null || publicId.trim().isEmpty()) {
            return fallbackUrl;
        }
        
        try {
            if (imageExists(publicId)) {
                return cloudinary.url().generate(publicId);
            } else {
                log.warn("Image not found on Cloudinary, using fallback: {}", publicId);
                return fallbackUrl;
            }
        } catch (Exception e) {
            log.error("Error generating image URL: {}", e.getMessage());
            return fallbackUrl;
        }
    }

    /**
     * Xóa ảnh cũ khi upload ảnh mới
     */
    @Override
    public void deleteOldImage(String oldPublicId) {
        if (oldPublicId != null && !oldPublicId.trim().isEmpty()) {
            try {
                deleteImage(oldPublicId);
                log.info("Old image deleted successfully: {}", oldPublicId);
            } catch (Exception e) {
                log.warn("Failed to delete old image: {}", oldPublicId);
                // Không throw exception vì đây không phải lỗi nghiêm trọng
            }
        }
    }
} 