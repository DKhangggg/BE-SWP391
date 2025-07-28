package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.dto.request.BlogCategoryRequestDTO;
import com.example.gender_healthcare_service.dto.response.ApiResponse;
import com.example.gender_healthcare_service.dto.response.BlogCategoryWithPostsDTO;
import com.example.gender_healthcare_service.entity.BlogCategory;
import com.example.gender_healthcare_service.service.BlogCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blog/categories")
@RequiredArgsConstructor
public class BlogCategoryController {

    private final BlogCategoryService blogCategoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BlogCategory>>> getAllCategories() {
        try {
            List<BlogCategory> categories = blogCategoryService.getAllCategories();
            if (categories.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error("Chưa có danh mục nào trong hệ thống"));
            }
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách danh mục thành công", categories));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách danh mục: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<BlogCategory>> getCategoryById(@PathVariable Integer categoryId) {
        try {
            BlogCategory category = blogCategoryService.getCategoryById(categoryId);
            return ResponseEntity.ok(ApiResponse.success("Lấy thông tin danh mục thành công", category));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Không tìm thấy danh mục: " + e.getMessage()));
        }
    }

    /**
     * Tạo danh mục mới (ADMIN/STAFF/MANAGER)
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER')")
    public ResponseEntity<ApiResponse<BlogCategory>> createCategory(
            @RequestBody BlogCategoryRequestDTO categoryRequest) {
        try {
            BlogCategory createdCategory = blogCategoryService.createCategory(categoryRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Tạo danh mục thành công", createdCategory));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Lỗi khi tạo danh mục: " + e.getMessage()));
        }
    }

    /**
     * Cập nhật danh mục (ADMIN/STAFF/MANAGER)
     */
    @PutMapping("/{categoryId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER')")
    public ResponseEntity<ApiResponse<BlogCategory>> updateCategory(
            @PathVariable Integer categoryId,
            @RequestBody BlogCategoryRequestDTO categoryRequest) {
        try {
            BlogCategory updatedCategory = blogCategoryService.updateCategory(categoryId, categoryRequest);
            return ResponseEntity.ok(ApiResponse.success("Cập nhật danh mục thành công", updatedCategory));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Lỗi khi cập nhật danh mục: " + e.getMessage()));
        }
    }

    /**
     * Xóa danh mục (ADMIN/STAFF/MANAGER)
     */
    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER')")
    public ResponseEntity<ApiResponse<String>> deleteCategory(@PathVariable Integer categoryId) {
        try {
            boolean deleted = blogCategoryService.deleteCategory(categoryId);
            if (deleted) {
                return ResponseEntity.ok(ApiResponse.success("Xóa danh mục thành công", "Danh mục đã được xóa"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Không thể xóa danh mục"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Lỗi khi xóa danh mục: " + e.getMessage()));
        }
    }

    /**
     * Lấy danh mục với danh sách bài viết (PUBLIC API)
     */
    @GetMapping("/{categoryId}/with-posts")
    public ResponseEntity<ApiResponse<BlogCategoryWithPostsDTO>> getCategoryWithPosts(
            @PathVariable Integer categoryId) {
        try {
            BlogCategoryWithPostsDTO categoryWithPosts = blogCategoryService.getCategoryWithPosts(categoryId);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh mục với bài viết thành công", categoryWithPosts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Không tìm thấy danh mục: " + e.getMessage()));
        }
    }

    /**
     * Lấy tất cả danh mục với danh sách bài viết (PUBLIC API)
     */
    @GetMapping("/with-posts")
    public ResponseEntity<ApiResponse<List<BlogCategoryWithPostsDTO>>> getAllCategoriesWithPosts() {
        try {
            List<BlogCategoryWithPostsDTO> categoriesWithPosts = blogCategoryService.getAllCategoriesWithPosts();
            if (categoriesWithPosts.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error("Chưa có danh mục nào trong hệ thống"));
            }
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách danh mục với bài viết thành công", categoriesWithPosts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách danh mục với bài viết: " + e.getMessage()));
        }
    }
}
