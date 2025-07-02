package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.dto.request.BlogCategoryRequestDTO;
import com.example.gender_healthcare_service.dto.request.BlogPostRequestDTO;
import com.example.gender_healthcare_service.dto.response.BlogCategoryWithPostsDTO;
import com.example.gender_healthcare_service.dto.response.BlogPostResponseDTO;
import com.example.gender_healthcare_service.entity.BlogCategory;
import com.example.gender_healthcare_service.entity.BlogPost;
import com.example.gender_healthcare_service.service.BlogCategoryService;
import com.example.gender_healthcare_service.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

    // Blog Post APIs
    @GetMapping("/posts")
    public ResponseEntity<?> getBlogPosts(Pageable pageable) {
        try {
            Page<BlogPostResponseDTO> blogPosts = blogService.getBlogPosts(pageable);
            if(blogPosts.getTotalElements() == 0) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No posts found");
            }
            return ResponseEntity.ok(blogPosts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching blog posts: " + e.getMessage());
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
    public ResponseEntity<?> createBlogPost(@RequestBody BlogPostRequestDTO blogPostRequestDTO,
                                          Authentication authentication) {
        try {
            boolean done=blogService.createBlogPost(blogPostRequestDTO, authentication);
            return ResponseEntity.status(HttpStatus.CREATED).body(done);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/posts/{postId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONSULTANT', 'STAFF', 'MANAGER')")
    public ResponseEntity<?> updateBlogPost(@PathVariable Integer postId,
                                         @RequestBody BlogPostRequestDTO blogPostRequestDTO,
                                         Authentication authentication) {
        try {
            BlogPost updatedBlogPost = blogService.updateBlogPost(postId, blogPostRequestDTO, authentication);
            return ResponseEntity.ok(updatedBlogPost);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
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
                return ResponseEntity.badRequest().body("Failed to delete blog post");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/posts/category/{categoryId}")
    public ResponseEntity<?> getBlogPostsByCategory(@PathVariable Integer categoryId, Pageable pageable) {
        try {
            Page<BlogPost> blogPosts = blogService.getBlogPostsByCategory(categoryId, pageable);
            if(blogPosts.getTotalElements() == 0) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No posts found for this category");
            }
            return ResponseEntity.ok(blogPosts);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/posts/search")
    public ResponseEntity<?> searchBlogPosts(@RequestParam String keyword, Pageable pageable) {
        try {
            Page<BlogPost> blogPosts = blogService.searchBlogPosts(keyword, pageable);
            if(blogPosts.getTotalElements() == 0) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No posts found for your search");
            }
            return ResponseEntity.ok(blogPosts);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/posts/featured")
    public ResponseEntity<?> getFeaturedBlogPosts(Pageable pageable) {
        try {
            Page<BlogPost> featuredPosts = blogService.getFeaturedBlogPosts(pageable);
            if(featuredPosts.getTotalElements() == 0) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No featured posts found");
            }
            return ResponseEntity.ok(featuredPosts);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Blog Category APIs

    @GetMapping("/categories")
    public ResponseEntity<?> getBlogCategories() {
        try {
            List<BlogCategory> categories = blogCategoryService.getAllCategories();
            if (categories.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No blog categories found");
            }
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching blog categories: " + e.getMessage());
        }
    }

    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<?> getBlogCategoryById(@PathVariable Integer categoryId) {
        try {
            BlogCategory category = blogCategoryService.getCategoryById(categoryId);
            return ResponseEntity.ok(category);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/categories")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> createBlogCategory(@RequestBody BlogCategoryRequestDTO blogCategoryRequestDTO) {
        try {
            BlogCategory createdCategory = blogCategoryService.createCategory(blogCategoryRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/categories/{categoryId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> updateBlogCategory(@PathVariable Integer categoryId,
                                             @RequestBody BlogCategoryRequestDTO blogCategoryRequestDTO) {
        try {
            BlogCategory updatedCategory = blogCategoryService.updateCategory(categoryId, blogCategoryRequestDTO);
            return ResponseEntity.ok(updatedCategory);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/categories/{categoryId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> deleteBlogCategory(@PathVariable Integer categoryId) {
        try {
            boolean isDeleted = blogCategoryService.deleteCategory(categoryId);
            if (isDeleted) {
                return ResponseEntity.ok("Blog category deleted successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to delete blog category");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/categories/{categoryId}/with-posts")
    public ResponseEntity<?> getCategoryWithPosts(@PathVariable Integer categoryId) {
        try {
            BlogCategoryWithPostsDTO category = blogCategoryService.getCategoryWithPosts(categoryId);
            return ResponseEntity.ok(category);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/categories/with-posts")
    public ResponseEntity<?> getAllCategoriesWithPosts() {
        try {
            List<BlogCategoryWithPostsDTO> categories = blogCategoryService.getAllCategoriesWithPosts();
            if (categories.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No blog categories found");
            }
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching blog categories: " + e.getMessage());
        }
    }
}
