package com.example.gender_healthcare_service.dto.response;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class BlogPostResponseDTO {

    private Integer postID;
    private String title;
    private String slug;
    private String summary;
    private String content;
    private String coverImageUrl;
    private String tags;
    private Integer views;
    private Integer likes;
    private Integer commentsCount;
    private Boolean isPublished;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Author information
    private AuthorDTO author;
    
    // Categories
    private List<CategoryDTO> categories;
    
    // User interaction
    private Boolean isLikedByCurrentUser;

    @Data
    public static class AuthorDTO {
        private Integer id;
        private String fullName;
        private String username;
        private String avatarUrl;
    }
    
    @Data
    public static class CategoryDTO {
        private Integer categoryID;
        private String categoryName;
        private String slug;
        private String thumbnailUrl;
    }
}
