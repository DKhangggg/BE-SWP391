package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.dto.request.BlogPostRequestDTO;
import com.example.gender_healthcare_service.dto.response.BlogPostResponseDTO;
import com.example.gender_healthcare_service.entity.BlogCategory;
import com.example.gender_healthcare_service.entity.BlogPost;
import com.example.gender_healthcare_service.entity.User;
import com.example.gender_healthcare_service.mapper.BlogMapper;
import com.example.gender_healthcare_service.repository.BlogCategoryRepository;
import com.example.gender_healthcare_service.repository.BlogPostRepository;
import com.example.gender_healthcare_service.repository.UserRepository;
import com.example.gender_healthcare_service.service.BlogService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BlogServiceImpl implements BlogService {

    @Autowired
    private BlogPostRepository blogPostRepository;

    @Autowired
    private BlogCategoryRepository blogCategoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BlogMapper blogMapper;

    @Override
    public Page<BlogPostResponseDTO> getBlogPosts(Pageable pageable) {
        Page<BlogPost> posts = blogPostRepository.findAll(pageable);
        return posts.map(blogPost -> blogMapper.toResponseDTO(blogPost));
    }

    @Override
    public BlogPost getBlogPostById(Integer postId) throws Exception {
        return blogPostRepository.findById(postId)
                .orElseThrow(() -> new Exception("Blog post not found with ID: " + postId));
    }

    @Override
    public boolean createBlogPost(BlogPostRequestDTO blogPostRequestDTO, Authentication authentication) throws Exception {
        try {
            User user = getUserFromAuthentication(authentication);

            Set<BlogCategory> blogCategories = new HashSet<>();
            for(Integer categoryId : blogPostRequestDTO.getCategoryIds()) {
                BlogCategory category = blogCategoryRepository.findById(categoryId)
                        .orElseThrow(() -> new Exception("Category not found with ID: " + categoryId));
                blogCategories.add(category);
            }

            BlogPost blogPost = new BlogPost();
            blogPost.setTitle(blogPostRequestDTO.getTitle());
            blogPost.setContent(blogPostRequestDTO.getContent());
            blogPost.setCategories(blogCategories);
            blogPost.setAuthor(user);
            blogPost.setCreatedAt(LocalDateTime.now());
            blogPost.setUpdatedAt(LocalDateTime.now());
            BlogPost savedPost = blogPostRepository.save(blogPost);
            return true;
        } catch (Exception e) {
            throw new Exception("Error creating blog post: " + e.getMessage());
        }
    }

    @Override
    public BlogPost updateBlogPost(Integer postId, BlogPostRequestDTO blogPostRequestDTO, Authentication authentication) throws Exception {
        User user = getUserFromAuthentication(authentication);
        BlogPost existingPost = blogPostRepository.findById(postId)
                .orElseThrow(() -> new Exception("Blog post not found with ID: " + postId));
        
        if (!existingPost.getAuthor().getId().equals(user.getId()) &&
            !hasAdminRole(authentication)) {
            throw new Exception("You don't have permission to update this blog post");
        }

        if (blogPostRequestDTO.getCategoryIds() != null) {
            Set<BlogCategory> blogCategories = new HashSet<>();
            for(Integer categoryId : blogPostRequestDTO.getCategoryIds()) {
                BlogCategory category = blogCategoryRepository.findById(categoryId)
                        .orElseThrow(() -> new Exception("Category not found with ID: " + categoryId));
                existingPost.getCategories().add(category);
            }
        }
        existingPost.setTitle(blogPostRequestDTO.getTitle());
        existingPost.setContent(blogPostRequestDTO.getContent());
        existingPost.setUpdatedAt(LocalDateTime.now());
        return blogPostRepository.save(existingPost);
    }
    @Override
    public boolean deleteBlogPost(Integer postId) throws Exception {
        BlogPost post = blogPostRepository.findById(postId)
                .orElseThrow(() -> new Exception("Blog post not found with ID: " + postId));
        
        blogPostRepository.delete(post);
        return true;
    }

    @Override
    public Page<BlogPost> getBlogPostsByCategory(Integer categoryId, Pageable pageable) throws Exception {
        BlogCategory category = blogCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new Exception("Category not found with ID: " + categoryId));
        
        return blogPostRepository.findByCategory(category, pageable);
    }

    @Override
    public Page<BlogPost> searchBlogPosts(String keyword, Pageable pageable) {
        return blogPostRepository.searchBlogPosts(keyword, pageable);
    }

    @Override
    public Page<BlogPost> getFeaturedBlogPosts(Pageable pageable) {
        return blogPostRepository.findFeaturedPosts(pageable);
    }

    @Override
    public Page<BlogPost> getBlogPostsByAuthor(Integer userId, Pageable pageable) throws Exception {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found with ID: " + userId));
        return blogPostRepository.findByAuthor(author, pageable);
    }

    private User getUserFromAuthentication(Authentication authentication) {
        String username = authentication.getName();
        User user= userRepository.findUserByUsername(username);
        if( user == null) {
                throw  new RuntimeException("User not found");}
        return user;
    }
    private boolean hasAdminRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN") ||
                          authority.getAuthority().equals("ROLE_MANAGER"));
    }
}
