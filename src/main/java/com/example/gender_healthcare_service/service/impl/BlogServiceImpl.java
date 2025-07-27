package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.dto.request.BlogPostRequestDTO;
import com.example.gender_healthcare_service.dto.response.BlogPostResponseDTO;
import com.example.gender_healthcare_service.dto.response.PageResponse;
import com.example.gender_healthcare_service.entity.BlogCategory;
import com.example.gender_healthcare_service.entity.BlogPost;
import com.example.gender_healthcare_service.entity.User;
import com.example.gender_healthcare_service.mapper.BlogMapper;
import com.example.gender_healthcare_service.repository.BlogCategoryRepository;
import com.example.gender_healthcare_service.repository.BlogPostRepository;
import com.example.gender_healthcare_service.repository.UserRepository;
import com.example.gender_healthcare_service.service.BlogService;
import com.example.gender_healthcare_service.service.CloudinaryService;
import com.example.gender_healthcare_service.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final BlogPostRepository blogPostRepository;
    private final BlogCategoryRepository blogCategoryRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BlogMapper blogMapper;
    private final CloudinaryService cloudinaryService;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BlogPostResponseDTO> getAllBlogPosts(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<BlogPost> blogPosts = blogPostRepository.findAllByIsDeletedFalse(pageable);
        
        if (blogPosts.isEmpty()) {
            return new PageResponse<BlogPostResponseDTO>(new ArrayList<>(), pageNumber, pageSize, 0, 0);
        }
        
        return createPageResponse(blogPosts);
    }

    @Override
    @Transactional(readOnly = true)
    public BlogPostResponseDTO getBlogPostById(Integer postId) {
        BlogPost blogPost = blogPostRepository.findByIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết với ID: " + postId));
        
        incrementViews(postId);
        
        return blogMapper.toResponseDTO(blogPost);
    }

    @Override
    @Transactional
    public BlogPostResponseDTO createBlogPost(BlogPostRequestDTO request, MultipartFile coverImage, Authentication authentication) {
        User author = getUserFromAuthentication(authentication);
        
        Set<BlogCategory> categories = validateAndGetCategories(request.getCategoryIds());
        
        BlogPost blogPost = new BlogPost();
        blogPost.setTitle(request.getTitle());
        blogPost.setContent(request.getContent());
        blogPost.setSummary(request.getSummary());
        blogPost.setTags(request.getTags());
        
        // Tự động tạo slug từ title
        String slug = generateUniqueSlug(request.getTitle());
        blogPost.setSlug(slug);
        
        blogPost.setAuthor(author);
        blogPost.setCategories(categories);
        blogPost.setCreatedAt(LocalDateTime.now());
        blogPost.setUpdatedAt(LocalDateTime.now());
        blogPost.setIsPublished(request.getIsPublished() != null ? request.getIsPublished() : false);
        blogPost.setIsDeleted(false);
        
        BlogPost savedPost = blogPostRepository.save(blogPost);
        
        // Upload cover image sau khi có postID
        if (coverImage != null && !coverImage.isEmpty()) {
            String imageUrl = uploadCoverImage(coverImage, savedPost.getPostID());
            // Không cần save lại vì uploadCoverImage đã tự động update database
        }
        
        return blogMapper.toResponseDTO(savedPost);
    }

    @Override
    @Transactional
    public BlogPostResponseDTO updateBlogPost(Integer postId, BlogPostRequestDTO request, MultipartFile coverImage, Authentication authentication) {
        User currentUser = getUserFromAuthentication(authentication);
        BlogPost existingPost = blogPostRepository.findByIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết với ID: " + postId));
        
        if (!existingPost.getAuthor().getId().equals(currentUser.getId()) && 
            !hasAdminRole(authentication)) {
            throw new RuntimeException("Bạn không có quyền cập nhật bài viết này");
        }
        
        existingPost.setTitle(request.getTitle());
        existingPost.setContent(request.getContent());
        existingPost.setSummary(request.getSummary());
        existingPost.setTags(request.getTags());
        
        // Cập nhật slug nếu title thay đổi
        String newSlug = generateUniqueSlug(request.getTitle());
        existingPost.setSlug(newSlug);
        
        existingPost.setUpdatedAt(LocalDateTime.now());
        
        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            Set<BlogCategory> categories = validateAndGetCategories(request.getCategoryIds());
            existingPost.setCategories(categories);
        }
        
        if (coverImage != null && !coverImage.isEmpty()) {
            if (existingPost.getCoverImageUrl() != null) {
                deleteCoverImage(postId);
            }
            String imageUrl = uploadCoverImage(coverImage, postId);
            // Không cần set coverImageUrl vì uploadCoverImage đã tự động update database
        }
        
        BlogPost updatedPost = blogPostRepository.save(existingPost);
        return blogMapper.toResponseDTO(updatedPost);
    }

    @Override
    @Transactional
    public boolean deleteBlogPost(Integer postId, Authentication authentication) {
        User currentUser = getUserFromAuthentication(authentication);
        BlogPost blogPost = blogPostRepository.findByIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết với ID: " + postId));
        
        if (!blogPost.getAuthor().getId().equals(currentUser.getId()) && 
            !hasAdminRole(authentication)) {
            throw new RuntimeException("Bạn không có quyền xóa bài viết này");
        }
        
        blogPost.setIsDeleted(true);
        blogPost.setUpdatedAt(LocalDateTime.now());
        blogPostRepository.save(blogPost);
        
        if (blogPost.getCoverImageUrl() != null) {
            deleteCoverImage(postId);
        }
        
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BlogPostResponseDTO> searchBlogPosts(String keyword, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<BlogPost> blogPosts = blogPostRepository.searchBlogPostsByKeyword(keyword, pageable);
        
        if (blogPosts.isEmpty()) {
            return new PageResponse<BlogPostResponseDTO>(new ArrayList<>(), pageNumber, pageSize, 0, 0);
        }
        
        return createPageResponse(blogPosts);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BlogPostResponseDTO> getBlogPostsByCategory(Integer categoryId, int pageNumber, int pageSize) {
        BlogCategory category = blogCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + categoryId));
        
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<BlogPost> blogPosts = blogPostRepository.findByCategoriesAndIsDeletedFalse(category, pageable);
        
        if (blogPosts.isEmpty()) {
            return new PageResponse<BlogPostResponseDTO>(new ArrayList<>(), pageNumber, pageSize, 0, 0);
        }
        
        return createPageResponse(blogPosts);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BlogPostResponseDTO> getBlogPostsByAuthor(Integer authorId, int pageNumber, int pageSize) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + authorId));
        
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<BlogPost> blogPosts = blogPostRepository.findByAuthorAndIsDeletedFalse(author, pageable);
        
        if (blogPosts.isEmpty()) {
            return new PageResponse<BlogPostResponseDTO>(new ArrayList<>(), pageNumber, pageSize, 0, 0);
        }
        
        return createPageResponse(blogPosts);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BlogPostResponseDTO> getPublishedBlogPosts(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<BlogPost> blogPosts = blogPostRepository.findByIsPublishedTrueAndIsDeletedFalse(pageable);
        
        if (blogPosts.isEmpty()) {
            return new PageResponse<BlogPostResponseDTO>(new ArrayList<>(), pageNumber, pageSize, 0, 0);
        }
        
        return createPageResponse(blogPosts);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlogPostResponseDTO> getLatestBlogPosts(int limit) {
        List<BlogPost> latestPosts = blogPostRepository.findLatestPublishedPosts(limit);
        List<BlogPostResponseDTO> responseDTOs = new ArrayList<>();
        
        for (BlogPost post : latestPosts) {
            responseDTOs.add(blogMapper.toResponseDTO(post));
        }
        
        return responseDTOs;
    }

    @Override
    public String uploadCoverImage(MultipartFile file, Integer postId) {
        try {
            Map<String, Object> uploadResult = cloudinaryService.uploadImage(file);
            String secureUrl = (String) uploadResult.get("secure_url");
            
            // Chỉ update database nếu postId được cung cấp và khác null
            if (postId != null) {
                BlogPost blogPost = blogPostRepository.findById(postId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết với ID: " + postId));
                blogPost.setCoverImageUrl(secureUrl);
                blogPostRepository.save(blogPost);
            }
            
            return secureUrl;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tải lên hình ảnh: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteCoverImage(Integer postId) {
        return true;
    }

    @Override
    @Transactional
    public void incrementViews(Integer postId) {
        blogPostRepository.incrementViews(postId);
    }

    @Override
    @Transactional
    public void toggleLike(Integer postId, Integer userId) {
        BlogPost blogPost = blogPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết"));
        
        blogPost.setLikes(blogPost.getLikes() + 1);
        blogPostRepository.save(blogPost);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isLikedByUser(Integer postId, Integer userId) {
        return false;
    }

    private User getUserFromAuthentication(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("Không tìm thấy người dùng");
        }
        return user;
    }

    private boolean hasAdminRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN") ||
                                     authority.getAuthority().equals("ROLE_MANAGER"));
    }

    private Set<BlogCategory> validateAndGetCategories(Set<Integer> categoryIds) {
        Set<BlogCategory> categories = new HashSet<>();
        for (Integer categoryId : categoryIds) {
            BlogCategory category = blogCategoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + categoryId));
            categories.add(category);
        }
        return categories;
    }

    private PageResponse<BlogPostResponseDTO> createPageResponse(Page<BlogPost> blogPosts) {
        PageResponse<BlogPostResponseDTO> response = new PageResponse<>();
        response.setContent(blogPosts.map(blogMapper::toResponseDTO).getContent());
        response.setPageNumber(blogPosts.getNumber() + 1);
        response.setPageSize(blogPosts.getSize());
        response.setTotalElements(blogPosts.getTotalElements());
        response.setTotalPages(blogPosts.getTotalPages());
        response.setHasNext(blogPosts.hasNext());
        response.setHasPrevious(blogPosts.hasPrevious());
        return response;
    }
    
    private String generateUniqueSlug(String title) {
        String baseSlug = SlugUtils.generateSlug(title);
        String uniqueSlug = baseSlug;
        int counter = 1;
        
        while (blogPostRepository.countBySlugAndIsDeletedFalse(uniqueSlug) > 0) {
            uniqueSlug = baseSlug + "-" + counter;
            counter++;
        }
        
        return uniqueSlug;
    }
}
