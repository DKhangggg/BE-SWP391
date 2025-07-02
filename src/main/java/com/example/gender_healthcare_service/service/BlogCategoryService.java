package com.example.gender_healthcare_service.service;

import com.example.gender_healthcare_service.dto.request.BlogCategoryRequestDTO;
import com.example.gender_healthcare_service.dto.response.BlogCategoryWithPostsDTO;
import com.example.gender_healthcare_service.entity.BlogCategory;

import java.util.List;

public interface BlogCategoryService {
    List<BlogCategory> getAllCategories();

    BlogCategory getCategoryById(Integer categoryId) throws Exception;

    BlogCategory createCategory(BlogCategoryRequestDTO blogCategoryRequestDTO) throws Exception;

    BlogCategory updateCategory(Integer categoryId, BlogCategoryRequestDTO blogCategoryRequestDTO) throws Exception;

    boolean deleteCategory(Integer categoryId) throws Exception;

    BlogCategoryWithPostsDTO getCategoryWithPosts(Integer categoryId) throws Exception;

    List<BlogCategoryWithPostsDTO> getAllCategoriesWithPosts();
}
