package com.example.gender_healthcare_service.service;

import com.example.gender_healthcare_service.dto.request.BlogPostRequestDTO;
import com.example.gender_healthcare_service.dto.response.BlogPostResponseDTO;
import com.example.gender_healthcare_service.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

public interface BlogService {
    
    // CRUD Operations
    PageResponse<BlogPostResponseDTO> getAllBlogPosts(int pageNumber, int pageSize);
    
    BlogPostResponseDTO getBlogPostById(Integer postId);
    
    BlogPostResponseDTO createBlogPost(BlogPostRequestDTO request, MultipartFile coverImage, Authentication authentication);
    
    BlogPostResponseDTO updateBlogPost(Integer postId, BlogPostRequestDTO request, MultipartFile coverImage, Authentication authentication);
    
    boolean deleteBlogPost(Integer postId, Authentication authentication);
    
    // Search and Filter
    PageResponse<BlogPostResponseDTO> searchBlogPosts(String keyword, int pageNumber, int pageSize);
    
    PageResponse<BlogPostResponseDTO> getBlogPostsByCategory(Integer categoryId, int pageNumber, int pageSize);
    
    PageResponse<BlogPostResponseDTO> getBlogPostsByAuthor(Integer authorId, int pageNumber, int pageSize);
    
    PageResponse<BlogPostResponseDTO> getPublishedBlogPosts(int pageNumber, int pageSize);
    
    // Image Management
    String uploadCoverImage(MultipartFile file, Integer postId);
    
    boolean deleteCoverImage(Integer postId);
    
    // Analytics
    void incrementViews(Integer postId);
    
    void toggleLike(Integer postId, Integer userId);
    
    boolean isLikedByUser(Integer postId, Integer userId);
}
