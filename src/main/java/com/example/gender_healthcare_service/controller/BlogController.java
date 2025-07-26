package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.dto.request.BlogPostRequestDTO;
import com.example.gender_healthcare_service.dto.response.ApiResponse;
import com.example.gender_healthcare_service.dto.response.BlogPostResponseDTO;
import com.example.gender_healthcare_service.dto.response.PageResponse;
import com.example.gender_healthcare_service.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/blog")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    // ==================== CRUD OPERATIONS ====================

    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<PageResponse<BlogPostResponseDTO>>> getAllBlogPosts(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            PageResponse<BlogPostResponseDTO> response = blogService.getAllBlogPosts(pageNumber, pageSize);
            if (response.getContent().isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error("Chưa có bài viết nào trong hệ thống"));
            }
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách bài viết thành công", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách bài viết: " + e.getMessage()));
        }
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<BlogPostResponseDTO>> getBlogPostById(@PathVariable Integer postId) {
        try {
            BlogPostResponseDTO blogPost = blogService.getBlogPostById(postId);
            return ResponseEntity.ok(ApiResponse.success("Lấy bài viết thành công", blogPost));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Không tìm thấy bài viết với ID: " + postId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy bài viết: " + e.getMessage()));
        }
    }

    @PostMapping("/posts")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONSULTANT', 'STAFF', 'MANAGER')")
    public ResponseEntity<ApiResponse<BlogPostResponseDTO>> createBlogPost(
            @RequestPart("blogPost") BlogPostRequestDTO blogPostRequest,
            @RequestPart(value = "coverImage", required = false) MultipartFile coverImage,
            Authentication authentication) {
        try {
            BlogPostResponseDTO createdPost = blogService.createBlogPost(blogPostRequest, coverImage, authentication);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Tạo bài viết thành công", createdPost));
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Bạn không có quyền thực hiện thao tác này!"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi không xác định khi tạo bài viết"));
        }
    }

    @PutMapping("/posts/{postId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONSULTANT', 'STAFF', 'MANAGER')")
    public ResponseEntity<ApiResponse<BlogPostResponseDTO>> updateBlogPost(
            @PathVariable Integer postId,
            @RequestPart("blogPost") BlogPostRequestDTO blogPostRequest,
            @RequestPart(value = "coverImage", required = false) MultipartFile coverImage,
            Authentication authentication) {
        try {
            BlogPostResponseDTO updatedPost = blogService.updateBlogPost(postId, blogPostRequest, coverImage, authentication);
            return ResponseEntity.ok(ApiResponse.success("Cập nhật bài viết thành công", updatedPost));
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Bạn không có quyền thực hiện thao tác này!"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi không xác định khi cập nhật bài viết"));
        }
    }

    @DeleteMapping("/posts/{postId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONSULTANT', 'STAFF', 'MANAGER')")
    public ResponseEntity<ApiResponse<String>> deleteBlogPost(@PathVariable Integer postId, Authentication authentication) {
        try {
            boolean isDeleted = blogService.deleteBlogPost(postId, authentication);
            if (isDeleted) {
                return ResponseEntity.ok(ApiResponse.success("Bài viết đã được xóa thành công!", null));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Không thể xóa bài viết!"));
            }
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Bạn không có quyền thực hiện thao tác này!"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi không xác định khi xóa bài viết"));
        }
    }

    // ==================== SEARCH AND FILTER ====================

    @GetMapping("/posts/search")
    public ResponseEntity<ApiResponse<PageResponse<BlogPostResponseDTO>>> searchBlogPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            PageResponse<BlogPostResponseDTO> response = blogService.searchBlogPosts(keyword, pageNumber, pageSize);
            if (response.getContent().isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error("Không tìm thấy bài viết nào với từ khóa: " + keyword));
            }
            return ResponseEntity.ok(ApiResponse.success("Tìm kiếm bài viết thành công", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi tìm kiếm bài viết: " + e.getMessage()));
        }
    }

    @GetMapping("/posts/category/{categoryId}")
    public ResponseEntity<ApiResponse<PageResponse<BlogPostResponseDTO>>> getBlogPostsByCategory(
            @PathVariable Integer categoryId,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            PageResponse<BlogPostResponseDTO> response = blogService.getBlogPostsByCategory(categoryId, pageNumber, pageSize);
            if (response.getContent().isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error("Không tìm thấy bài viết nào trong danh mục này"));
            }
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách bài viết theo danh mục thành công", response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Không tìm thấy danh mục với ID: " + categoryId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy bài viết theo danh mục: " + e.getMessage()));
        }
    }

    @GetMapping("/posts/author/{authorId}")
    public ResponseEntity<PageResponse<BlogPostResponseDTO>> getBlogPostsByAuthor(
            @PathVariable Integer authorId,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            PageResponse<BlogPostResponseDTO> response = blogService.getBlogPostsByAuthor(authorId, pageNumber, pageSize);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/posts/published")
    public ResponseEntity<ApiResponse<PageResponse<BlogPostResponseDTO>>> getPublishedBlogPosts(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            PageResponse<BlogPostResponseDTO> response = blogService.getPublishedBlogPosts(pageNumber, pageSize);
            if (response.getContent().isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error("Chưa có bài viết nào được xuất bản"));
            }
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách bài viết đã xuất bản thành công", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách bài viết đã xuất bản: " + e.getMessage()));
        }
    }

    // ==================== IMAGE MANAGEMENT ====================

    @PostMapping("/posts/{postId}/cover-image")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONSULTANT', 'STAFF', 'MANAGER')")
    public ResponseEntity<?> uploadCoverImage(
            @PathVariable Integer postId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        try {
            String imageUrl = blogService.uploadCoverImage(file, postId);
            return ResponseEntity.ok(imageUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi upload hình ảnh");
        }
    }

    @DeleteMapping("/posts/{postId}/cover-image")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONSULTANT', 'STAFF', 'MANAGER')")
    public ResponseEntity<?> deleteCoverImage(@PathVariable Integer postId, Authentication authentication) {
        try {
            boolean isDeleted = blogService.deleteCoverImage(postId);
            if (isDeleted) {
                return ResponseEntity.ok("Hình ảnh đã được xóa thành công!");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Không thể xóa hình ảnh!");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi xóa hình ảnh");
        }
    }

    // ==================== ANALYTICS ====================

    @PostMapping("/posts/{postId}/like")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'CONSULTANT', 'STAFF', 'MANAGER')")
    public ResponseEntity<?> toggleLike(@PathVariable Integer postId, Authentication authentication) {
        try {
            // TODO: Get user ID from authentication
            Integer userId = 1; // Placeholder
            blogService.toggleLike(postId, userId);
            return ResponseEntity.ok("Đã cập nhật trạng thái like!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi cập nhật like");
        }
    }

    @GetMapping("/posts/{postId}/is-liked")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'CONSULTANT', 'STAFF', 'MANAGER')")
    public ResponseEntity<Boolean> isLikedByUser(@PathVariable Integer postId, Authentication authentication) {
        try {
            // TODO: Get user ID from authentication
            Integer userId = 1; // Placeholder
            boolean isLiked = blogService.isLikedByUser(postId, userId);
            return ResponseEntity.ok(isLiked);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }
}
