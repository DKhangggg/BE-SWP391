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

import java.time.LocalDate;
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
        return blogCategoryRepository.findAll();
    }

    @Override
    public BlogCategory getCategoryById(Integer categoryId) throws Exception {
        return blogCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new Exception("Blog category not found with ID: " + categoryId));
    }

    @Override
    public BlogCategory createCategory(BlogCategoryRequestDTO blogCategoryRequestDTO) throws Exception {
        BlogCategory existingCategory = blogCategoryRepository.findByCategoryName(blogCategoryRequestDTO.getName());
        if (existingCategory != null) {
            throw new Exception("A category with this name already exists");
        }

        BlogCategory blogCategory = new BlogCategory();
        blogCategory.setCategoryName(blogCategoryRequestDTO.getName());
        blogCategory.setDescription(blogCategoryRequestDTO.getDescription());
        blogCategory.setCreatedAt(LocalDate.now());
        blogCategory.setUpdatedAt(LocalDate.now());

        return blogCategoryRepository.save(blogCategory);
    }

    @Override
    public BlogCategory updateCategory(Integer categoryId, BlogCategoryRequestDTO blogCategoryRequestDTO) throws Exception {
        BlogCategory existingCategory = blogCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new Exception("Blog category not found with ID: " + categoryId));

        BlogCategory categoryWithSameName = blogCategoryRepository.findByCategoryName(blogCategoryRequestDTO.getName());
        if (categoryWithSameName != null && !categoryWithSameName.getCategoryID().equals(categoryId)) {
            throw new Exception("Another category with this name already exists");
        }

        existingCategory.setCategoryName(blogCategoryRequestDTO.getName());
        existingCategory.setDescription(blogCategoryRequestDTO.getDescription());
        existingCategory.setUpdatedAt(LocalDate.now());

        return blogCategoryRepository.save(existingCategory);
    }

    @Override
    public boolean deleteCategory(Integer categoryId) throws Exception {
        BlogCategory category = blogCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new Exception("Blog category not found with ID: " + categoryId));

        // Check if there are any blog posts associated with this category
        if (category.getBlogPosts() != null && !category.getBlogPosts().isEmpty()) {
            throw new Exception("Cannot delete category because it has associated blog posts");
        }

        blogCategoryRepository.delete(category);
        return true;
    }

    @Override
    public BlogCategoryWithPostsDTO getCategoryWithPosts(Integer categoryId) throws Exception {
        BlogCategory category = blogCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new Exception("Blog category not found with ID: " + categoryId));

        return mapCategoryToWithPostsDTO(category);
    }

    @Override
    public List<BlogCategoryWithPostsDTO> getAllCategoriesWithPosts() {
        List<BlogCategory> categories = blogCategoryRepository.findAll();
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
                post.getCreatedAt() != null ? post.getCreatedAt() : null
        );
    }
}
