package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.service.CloudinaryService;
import com.example.gender_healthcare_service.service.DefaultImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultImageServiceImpl implements DefaultImageService {

    private final CloudinaryService cloudinaryService;
    
    // Cache cho các ảnh mặc định
    private final Map<String, String> defaultImageUrls = new HashMap<>();
    
    // Các loại ảnh mặc định
    private static final String[] IMAGE_TYPES = {
        "user_avatar_default",
        "consultant_avatar_default", 
        "blog_cover_default",
        "service_icon_default",
        "notification_icon_default"
    };

    @Override
    public Map<String, Object> uploadDefaultImage(String imageType, String base64Image) {
        try {
            // Decode base64 image
            byte[] imageBytes = Base64.getDecoder().decode(base64Image.split(",")[1]);
            
            // Tạo MultipartFile từ byte array
            java.io.ByteArrayInputStream inputStream = new java.io.ByteArrayInputStream(imageBytes);
            org.springframework.web.multipart.MultipartFile file = new org.springframework.web.multipart.MultipartFile() {
                @Override
                public String getName() {
                    return "image";
                }

                @Override
                public String getOriginalFilename() {
                    return imageType + ".png";
                }

                @Override
                public String getContentType() {
                    return "image/png";
                }

                @Override
                public boolean isEmpty() {
                    return imageBytes.length == 0;
                }

                @Override
                public long getSize() {
                    return imageBytes.length;
                }

                @Override
                public byte[] getBytes() throws IOException {
                    return imageBytes;
                }

                @Override
                public java.io.InputStream getInputStream() throws IOException {
                    return inputStream;
                }

                @Override
                public void transferTo(java.io.File dest) throws IOException, IllegalStateException {
                    java.nio.file.Files.write(dest.toPath(), imageBytes);
                }
            };
            
            // Upload lên Cloudinary với folder riêng cho default images
            Map<String, Object> uploadResult = cloudinaryService.uploadImage(file);
            
            // Lưu URL vào cache
            String imageUrl = (String) uploadResult.get("secure_url");
            defaultImageUrls.put(imageType, imageUrl);
            
            log.info("Default image uploaded successfully: {} -> {}", imageType, imageUrl);
            return uploadResult;
            
        } catch (Exception e) {
            log.error("Error uploading default image: {}", e.getMessage());
            throw new RuntimeException("Lỗi khi upload ảnh mặc định: " + e.getMessage());
        }
    }

    @Override
    public String getDefaultImageUrl(String imageType) {
        // Kiểm tra cache trước
        if (defaultImageUrls.containsKey(imageType)) {
            return defaultImageUrls.get(imageType);
        }
        
        // Nếu không có trong cache, trả về URL mặc định
        String defaultUrl = getHardcodedDefaultUrl(imageType);
        defaultImageUrls.put(imageType, defaultUrl);
        return defaultUrl;
    }

    @Override
    public Map<String, String> getAllDefaultImages() {
        Map<String, String> allImages = new HashMap<>();
        
        for (String imageType : IMAGE_TYPES) {
            allImages.put(imageType, getDefaultImageUrl(imageType));
        }
        
        return allImages;
    }

    @Override
    public Map<String, Object> updateDefaultImage(String imageType, String base64Image) {
        // Xóa ảnh cũ nếu có
        deleteDefaultImage(imageType);
        
        // Upload ảnh mới
        return uploadDefaultImage(imageType, base64Image);
    }

    @Override
    public boolean deleteDefaultImage(String imageType) {
        try {
            // Xóa khỏi cache
            defaultImageUrls.remove(imageType);
            
            // Xóa khỏi Cloudinary nếu có public_id
            String publicId = "gender-healthcare/" + imageType;
            if (cloudinaryService.imageExists(publicId)) {
                cloudinaryService.deleteImage(publicId);
                log.info("Default image deleted successfully: {}", imageType);
                return true;
            }
            
            return false;
        } catch (Exception e) {
            log.error("Error deleting default image: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean defaultImageExists(String imageType) {
        return defaultImageUrls.containsKey(imageType) || 
               cloudinaryService.imageExists("gender-healthcare/" + imageType);
    }
    
    /**
     * Lấy URL mặc định hardcode cho các loại ảnh
     */
    private String getHardcodedDefaultUrl(String imageType) {
        switch (imageType) {
            case "user_avatar_default":
                return "https://res.cloudinary.com/ddigxv6m7/image/upload/v1705123456/gender-healthcare/defaults/user-avatar-default.png";
            case "consultant_avatar_default":
                return "https://res.cloudinary.com/ddigxv6m7/image/upload/v1705123456/gender-healthcare/defaults/consultant-avatar-default.png";
            case "blog_cover_default":
                return "https://res.cloudinary.com/ddigxv6m7/image/upload/v1705123456/gender-healthcare/defaults/blog-cover-default.png";
            case "service_icon_default":
                return "https://res.cloudinary.com/ddigxv6m7/image/upload/v1705123456/gender-healthcare/defaults/service-icon-default.png";
            case "notification_icon_default":
                return "https://res.cloudinary.com/ddigxv6m7/image/upload/v1705123456/gender-healthcare/defaults/notification-icon-default.png";
            default:
                return "https://res.cloudinary.com/ddigxv6m7/image/upload/v1705123456/gender-healthcare/defaults/generic-default.png";
        }
    }
} 