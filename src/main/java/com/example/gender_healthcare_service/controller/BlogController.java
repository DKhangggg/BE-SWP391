package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.dto.request.BlogCategoryRequestDTO;
import com.example.gender_healthcare_service.dto.request.BlogPostRequestDTO;
import com.example.gender_healthcare_service.dto.response.BlogCategoryDTO;
import com.example.gender_healthcare_service.dto.response.BlogCategoryWithPostsDTO;
import com.example.gender_healthcare_service.dto.response.BlogPostResponseDTO;
import com.example.gender_healthcare_service.dto.response.PageResponse;
import com.example.gender_healthcare_service.entity.BlogCategory;
import com.example.gender_healthcare_service.entity.BlogPost;
import com.example.gender_healthcare_service.service.BlogCategoryService;
import com.example.gender_healthcare_service.service.BlogService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blog")
public class BlogController {
    @Autowired
    private BlogService blogService;

    @Autowired
    private BlogCategoryService blogCategoryService;

    @Autowired
    private ModelMapper modelMapper;

    // Blog Post APIs
    @GetMapping("/posts")
    public ResponseEntity<PageResponse<BlogPostResponseDTO>> getBlogPosts(
        @RequestParam(defaultValue = "1") int pageNumber,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
            Page<BlogPostResponseDTO> blogPosts = blogService.getBlogPosts(pageable);
            if(blogPosts.getTotalElements() == 0) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            PageResponse<BlogPostResponseDTO> response = new PageResponse<>();
            response.setContent(blogPosts.getContent());
            response.setPageNumber(blogPosts.getNumber() + 1);
            response.setPageSize(blogPosts.getSize());
            response.setTotalElements(blogPosts.getTotalElements());
            response.setTotalPages(blogPosts.getTotalPages());
            response.setHasNext(blogPosts.hasNext());
            response.setHasPrevious(blogPosts.hasPrevious());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<?> getBlogPostById(@PathVariable Integer postId) {
        try {
            BlogPost blogPost = blogService.getBlogPostById(postId);
            return ResponseEntity.ok(blogPost);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/posts")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONSULTANT', 'STAFF', 'MANAGER')")
    public ResponseEntity<?> createBlogPost(@RequestBody BlogPostRequestDTO blogPostRequestDTO, Authentication authentication) {
        try {
            boolean done = blogService.createBlogPost(blogPostRequestDTO, authentication);
            return ResponseEntity.status(HttpStatus.CREATED).body(done);
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không có quyền thực hiện thao tác này!");
        } catch (org.springframework.security.authentication.AuthenticationCredentialsNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn chưa đăng nhập hoặc token không hợp lệ!");
        } catch (Exception e) {
            String msg = e.getMessage() != null ? e.getMessage() : "Lỗi không xác định khi tạo bài viết";
            if (msg.contains("Category not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
    }

    @PutMapping("/posts/{postId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONSULTANT', 'STAFF', 'MANAGER')")
    public ResponseEntity<?> updateBlogPost(@PathVariable Integer postId, @RequestBody BlogPostRequestDTO blogPostRequestDTO, Authentication authentication) {
        try {
            BlogPost updatedBlogPost = blogService.updateBlogPost(postId, blogPostRequestDTO, authentication);
            return ResponseEntity.ok(updatedBlogPost);
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không có quyền thực hiện thao tác này!");
        } catch (org.springframework.security.authentication.AuthenticationCredentialsNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn chưa đăng nhập hoặc token không hợp lệ!");
        } catch (Exception e) {
            String msg = e.getMessage() != null ? e.getMessage() : "Lỗi không xác định khi cập nhật bài viết";
            if (msg.contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
    }

    @DeleteMapping("/posts/{postId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> deleteBlogPost(@PathVariable Integer postId, Authentication authentication) {
        try {
            boolean isDeleted = blogService.deleteBlogPost(postId);
            if (isDeleted) {
                return ResponseEntity.ok("Blog post deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to delete blog post");
            }
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không có quyền thực hiện thao tác này!");
        } catch (org.springframework.security.authentication.AuthenticationCredentialsNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn chưa đăng nhập hoặc token không hợp lệ!");
        } catch (Exception e) {
            String msg = e.getMessage() != null ? e.getMessage() : "Lỗi không xác định khi xóa bài viết";
            if (msg.contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
    }

    @GetMapping("/posts/category/{categoryId}")
    public ResponseEntity<PageResponse<BlogPostResponseDTO>> getBlogPostsByCategory(
        @PathVariable Integer categoryId,
        @RequestParam(defaultValue = "1") int pageNumber,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
            Page<BlogPost> blogPosts = blogService.getBlogPostsByCategory(categoryId, pageable);
            if(blogPosts.getTotalElements() == 0) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            PageResponse<BlogPostResponseDTO> response = new PageResponse<>();
            response.setContent(blogPosts.getContent().stream()
                .map(post -> org.modelmapper.ModelMapper.class.cast(blogService).map(post, BlogPostResponseDTO.class))
                .collect(java.util.stream.Collectors.toList()));
            response.setPageNumber(blogPosts.getNumber() + 1);
            response.setPageSize(blogPosts.getSize());
            response.setTotalElements(blogPosts.getTotalElements());
            response.setTotalPages(blogPosts.getTotalPages());
            response.setHasNext(blogPosts.hasNext());
            response.setHasPrevious(blogPosts.hasPrevious());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/posts/search")
    public ResponseEntity<PageResponse<BlogPostResponseDTO>> searchBlogPosts(
        @RequestParam String keyword,
        @RequestParam(defaultValue = "1") int pageNumber,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
            Page<BlogPost> blogPosts = blogService.searchBlogPosts(keyword, pageable);
            if(blogPosts.getTotalElements() == 0) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            PageResponse<BlogPostResponseDTO> response = new PageResponse<>();
            response.setContent(blogPosts.getContent().stream()
                .map(post -> org.modelmapper.ModelMapper.class.cast(blogService).map(post, BlogPostResponseDTO.class))
                .collect(java.util.stream.Collectors.toList()));
            response.setPageNumber(blogPosts.getNumber() + 1);
            response.setPageSize(blogPosts.getSize());
            response.setTotalElements(blogPosts.getTotalElements());
            response.setTotalPages(blogPosts.getTotalPages());
            response.setHasNext(blogPosts.hasNext());
            response.setHasPrevious(blogPosts.hasPrevious());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/posts/featured")
    public ResponseEntity<PageResponse<BlogPostResponseDTO>> getFeaturedBlogPosts(
        @RequestParam(defaultValue = "1") int pageNumber,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
            Page<BlogPost> featuredPosts = blogService.getFeaturedBlogPosts(pageable);
            if(featuredPosts.getTotalElements() == 0) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            PageResponse<BlogPostResponseDTO> response = new PageResponse<>();
            response.setContent(featuredPosts.getContent().stream()
                .map(post -> org.modelmapper.ModelMapper.class.cast(blogService).map(post, BlogPostResponseDTO.class))
                .collect(java.util.stream.Collectors.toList()));
            response.setPageNumber(featuredPosts.getNumber() + 1);
            response.setPageSize(featuredPosts.getSize());
            response.setTotalElements(featuredPosts.getTotalElements());
            response.setTotalPages(featuredPosts.getTotalPages());
            response.setHasNext(featuredPosts.hasNext());
            response.setHasPrevious(featuredPosts.hasPrevious());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Blog Category APIs

    @GetMapping("/categories")
    public ResponseEntity<List<BlogCategoryDTO>> getBlogCategories() {
        try {
            List<BlogCategory> categories = blogCategoryService.getAllCategories();
            if (categories.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            List<BlogCategoryDTO> categoryDTOs = categories.stream()
                .map(category -> modelMapper.map(category, BlogCategoryDTO.class))
                .collect(java.util.stream.Collectors.toList());

            return ResponseEntity.ok(categoryDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<BlogCategoryDTO> getBlogCategoryById(@PathVariable Integer categoryId) {
        try {
            BlogCategory category = blogCategoryService.getCategoryById(categoryId);
            BlogCategoryDTO categoryDTO = modelMapper.map(category, BlogCategoryDTO.class);
            return ResponseEntity.ok(categoryDTO);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/categories")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> createBlogCategory(@RequestBody BlogCategoryRequestDTO blogCategoryRequestDTO, Authentication authentication) {
        try {
            BlogCategory createdCategory = blogCategoryService.createCategory(blogCategoryRequestDTO);
            BlogCategoryDTO categoryDTO = modelMapper.map(createdCategory, BlogCategoryDTO.class);
            return ResponseEntity.status(HttpStatus.CREATED).body(categoryDTO);
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không có quyền thực hiện thao tác này!");
        } catch (org.springframework.security.authentication.AuthenticationCredentialsNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn chưa đăng nhập hoặc token không hợp lệ!");
        } catch (Exception e) {
            String msg = e.getMessage() != null ? e.getMessage() : "Lỗi không xác định khi tạo danh mục";
            if (msg.contains("already exists")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tên danh mục đã tồn tại!");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
    }

    @PutMapping("/categories/{categoryId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> updateBlogCategory(@PathVariable Integer categoryId,
                                             @RequestBody BlogCategoryRequestDTO blogCategoryRequestDTO, Authentication authentication) {
        try {
            BlogCategory updatedCategory = blogCategoryService.updateCategory(categoryId, blogCategoryRequestDTO);
            BlogCategoryDTO categoryDTO = modelMapper.map(updatedCategory, BlogCategoryDTO.class);
            return ResponseEntity.ok(categoryDTO);
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không có quyền thực hiện thao tác này!");
        } catch (org.springframework.security.authentication.AuthenticationCredentialsNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn chưa đăng nhập hoặc token không hợp lệ!");
        } catch (Exception e) {
            String msg = e.getMessage() != null ? e.getMessage() : "Lỗi không xác định khi cập nhật danh mục";
            if (msg.contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy danh mục!");
            }
            if (msg.contains("already exists")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tên danh mục đã tồn tại!");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
    }

    @DeleteMapping("/categories/{categoryId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> deleteBlogCategory(@PathVariable Integer categoryId, Authentication authentication) {
        try {
            boolean isDeleted = blogCategoryService.deleteCategory(categoryId);
            if (isDeleted) {
                return ResponseEntity.ok("Danh mục đã được xóa thành công!");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Không thể xóa danh mục!");
            }
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không có quyền thực hiện thao tác này!");
        } catch (org.springframework.security.authentication.AuthenticationCredentialsNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn chưa đăng nhập hoặc token không hợp lệ!");
        } catch (Exception e) {
            String msg = e.getMessage() != null ? e.getMessage() : "Lỗi không xác định khi xóa danh mục";
            if (msg.contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy danh mục!");
            }
            if (msg.contains("has posts")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Không thể xóa danh mục đang có bài viết!");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
    }

    @GetMapping("/categories/{categoryId}/with-posts")
    public ResponseEntity<BlogCategoryWithPostsDTO> getCategoryWithPosts(@PathVariable Integer categoryId) {
        try {
            BlogCategoryWithPostsDTO category = blogCategoryService.getCategoryWithPosts(categoryId);
            return ResponseEntity.ok(category);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/categories/with-posts")
    public ResponseEntity<List<BlogCategoryWithPostsDTO>> getAllCategoriesWithPosts() {
        try {
            List<BlogCategoryWithPostsDTO> categories = blogCategoryService.getAllCategoriesWithPosts();
            if (categories.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
