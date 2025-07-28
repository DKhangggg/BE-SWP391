package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.dto.request.BlogCategoryRequestDTO;
import com.example.gender_healthcare_service.dto.response.BlogCategoryWithPostsDTO;
import com.example.gender_healthcare_service.dto.response.BlogPostMinimalDTO;
import com.example.gender_healthcare_service.entity.BlogCategory;
import com.example.gender_healthcare_service.entity.BlogPost;
import com.example.gender_healthcare_service.repository.BlogCategoryRepository;
import com.example.gender_healthcare_service.repository.BlogPostRepository;
import com.example.gender_healthcare_service.service.BlogCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlogCategoryServiceImpl implements BlogCategoryService {

    @Autowired
    private BlogCategoryRepository blogCategoryRepository;

    @Autowired
    private BlogPostRepository blogPostRepository;

    @Override
    public List<BlogCategory> getAllCategories() {
        return blogCategoryRepository.findAllActive();
    }

    @Override
    public BlogCategory getCategoryById(Integer categoryId) throws Exception {
        return blogCategoryRepository.findByIdAndIsDeletedFalse(categoryId)
                .orElseThrow(() -> new Exception("Không tìm thấy danh mục với ID: " + categoryId));
    }

    @Override
    public BlogCategory createCategory(BlogCategoryRequestDTO blogCategoryRequestDTO) throws Exception {
        // Kiểm tra tên danh mục đã tồn tại
        BlogCategory existingCategory = blogCategoryRepository.findByCategoryName(blogCategoryRequestDTO.getCategoryName());
        if (existingCategory != null && !existingCategory.getIsDeleted()) {
            throw new Exception("Đã tồn tại danh mục với tên này");
        }

        // Tạo slug nếu không được cung cấp
        String slug = blogCategoryRequestDTO.getSlug();
        if (slug == null || slug.trim().isEmpty()) {
            slug = generateSlug(blogCategoryRequestDTO.getCategoryName());
        }

        // Kiểm tra slug đã tồn tại
        if (blogCategoryRepository.findBySlug(slug).isPresent()) {
            throw new Exception("Đã tồn tại danh mục với slug này");
        }

        BlogCategory blogCategory = new BlogCategory();
        blogCategory.setCategoryName(blogCategoryRequestDTO.getCategoryName());
        blogCategory.setSlug(slug);
        blogCategory.setDescription(blogCategoryRequestDTO.getDescription());
        blogCategory.setThumbnailUrl(blogCategoryRequestDTO.getThumbnailUrl());
        blogCategory.setCreatedAt(LocalDateTime.now());
        blogCategory.setUpdatedAt(LocalDateTime.now());
        blogCategory.setIsDeleted(false);

        return blogCategoryRepository.save(blogCategory);
    }

    @Override
    public BlogCategory updateCategory(Integer categoryId, BlogCategoryRequestDTO blogCategoryRequestDTO) throws Exception {
        BlogCategory existingCategory = blogCategoryRepository.findByIdAndIsDeletedFalse(categoryId)
                .orElseThrow(() -> new Exception("Không tìm thấy danh mục với ID: " + categoryId));

        // Kiểm tra tên danh mục đã tồn tại
        BlogCategory categoryWithSameName = blogCategoryRepository.findByCategoryName(blogCategoryRequestDTO.getCategoryName());
        if (categoryWithSameName != null && !categoryWithSameName.getCategoryID().equals(categoryId) && !categoryWithSameName.getIsDeleted()) {
            throw new Exception("Đã tồn tại danh mục khác với tên này");
        }

        // Xử lý slug
        String slug = blogCategoryRequestDTO.getSlug();
        if (slug == null || slug.trim().isEmpty()) {
            slug = generateSlug(blogCategoryRequestDTO.getCategoryName());
        }

        // Kiểm tra slug đã tồn tại (trừ chính nó)
        blogCategoryRepository.findBySlug(slug).ifPresent(category -> {
            if (!category.getCategoryID().equals(categoryId)) {
                throw new RuntimeException("Đã tồn tại danh mục với slug này");
            }
        });

        existingCategory.setCategoryName(blogCategoryRequestDTO.getCategoryName());
        existingCategory.setSlug(slug);
        existingCategory.setDescription(blogCategoryRequestDTO.getDescription());
        existingCategory.setThumbnailUrl(blogCategoryRequestDTO.getThumbnailUrl());
        existingCategory.setUpdatedAt(LocalDateTime.now());

        return blogCategoryRepository.save(existingCategory);
    }

    @Override
    public boolean deleteCategory(Integer categoryId) throws Exception {
        BlogCategory category = blogCategoryRepository.findByIdAndIsDeletedFalse(categoryId)
                .orElseThrow(() -> new Exception("Không tìm thấy danh mục với ID: " + categoryId));

        // Kiểm tra có bài viết liên quan không (chỉ kiểm tra bài viết chưa bị xóa)
        if (category.getBlogPosts() != null &&
            category.getBlogPosts().stream().anyMatch(post -> !post.getIsDeleted())) {
            throw new Exception("Không thể xóa danh mục vì có bài viết liên quan");
        }

        // Soft delete
        category.setIsDeleted(true);
        category.setUpdatedAt(LocalDateTime.now());
        blogCategoryRepository.save(category);
        return true;
    }

    @Override
    public BlogCategoryWithPostsDTO getCategoryWithPosts(Integer categoryId) throws Exception {
        BlogCategory category = blogCategoryRepository.findByIdAndIsDeletedFalse(categoryId)
                .orElseThrow(() -> new Exception("Không tìm thấy danh mục với ID: " + categoryId));

        return mapCategoryToWithPostsDTO(category);
    }

    @Override
    public List<BlogCategoryWithPostsDTO> getAllCategoriesWithPosts() {
        List<BlogCategory> categories = blogCategoryRepository.findAllActive();
        return categories.stream()
                .map(this::mapCategoryToWithPostsDTO)
                .collect(Collectors.toList());
    }

    private BlogCategoryWithPostsDTO mapCategoryToWithPostsDTO(BlogCategory category) {
        BlogCategoryWithPostsDTO dto = new BlogCategoryWithPostsDTO();
        dto.setCategoryID(category.getCategoryID());
        dto.setCategoryName(category.getCategoryName());
        dto.setDescription(category.getDescription());

        List<BlogPostMinimalDTO> postDTOs = new ArrayList<>();
        if (category.getBlogPosts() != null) {
            postDTOs = category.getBlogPosts().stream()
                    .filter(post -> !post.getIsDeleted() && post.getIsPublished())
                    .map(post -> mapToMinimalPostDTO(post))
                    .collect(Collectors.toList());
        }

        dto.setPosts(postDTOs);
        return dto;
    }

    private BlogPostMinimalDTO mapToMinimalPostDTO(BlogPost post) {
        return new BlogPostMinimalDTO(
                post.getPostID(),
                post.getTitle(),
                post.getCreatedAt()
        );
    }

    /**
     * Tạo slug từ tên danh mục
     */
    private String generateSlug(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            return "";
        }

        return categoryName.toLowerCase()
                .trim()
                .replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a")
                .replaceAll("[èéẹẻẽêềếệểễ]", "e")
                .replaceAll("[ìíịỉĩ]", "i")
                .replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o")
                .replaceAll("[ùúụủũưừứựửữ]", "u")
                .replaceAll("[ỳýỵỷỹ]", "y")
                .replaceAll("[đ]", "d")
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }
}
