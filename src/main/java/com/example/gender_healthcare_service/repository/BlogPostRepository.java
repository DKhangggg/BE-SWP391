package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.entity.BlogCategory;
import com.example.gender_healthcare_service.entity.BlogPost;
import com.example.gender_healthcare_service.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Integer> {

    // ==================== BASIC CRUD WITH SOFT DELETE ====================
    
    @Query("SELECT b FROM BlogPost b WHERE b.isDeleted = false")
    Page<BlogPost> findAllByIsDeletedFalse(Pageable pageable);
    
    @Query("SELECT b FROM BlogPost b WHERE b.id = :id AND b.isDeleted = false")
    Optional<BlogPost> findByIdAndIsDeletedFalse(@Param("id") Integer id);
    
    // ==================== SEARCH AND FILTER ====================
    
    @Query("SELECT b FROM BlogPost b WHERE (b.title LIKE %:keyword% OR b.content LIKE %:keyword% OR b.summary LIKE %:keyword%) AND b.isDeleted = false")
    Page<BlogPost> searchBlogPostsByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT b FROM BlogPost b JOIN b.categories c WHERE c = :category AND b.isDeleted = false")
    Page<BlogPost> findByCategoriesAndIsDeletedFalse(@Param("category") BlogCategory category, Pageable pageable);
    
    @Query("SELECT b FROM BlogPost b WHERE b.author = :author AND b.isDeleted = false")
    Page<BlogPost> findByAuthorAndIsDeletedFalse(@Param("author") User author, Pageable pageable);
    
    @Query("SELECT b FROM BlogPost b WHERE b.isPublished = true AND b.isDeleted = false ORDER BY b.createdAt DESC")
    Page<BlogPost> findByIsPublishedTrueAndIsDeletedFalse(Pageable pageable);
    
    // ==================== EXISTING METHODS (KEEP FOR COMPATIBILITY) ====================
    
    @Query("SELECT b FROM BlogPost b JOIN b.categories c WHERE c = :category")
    Page<BlogPost> findByCategory(@Param("category") BlogCategory category, Pageable pageable);

    Page<BlogPost> findByAuthor(User author, Pageable pageable);

    @Query("SELECT b FROM BlogPost b WHERE b.isPublished = true ORDER BY b.createdAt DESC")
    Page<BlogPost> findFeaturedPosts(Pageable pageable);

    @Query("SELECT b FROM BlogPost b WHERE b.title LIKE %:keyword% OR b.content LIKE %:keyword%")
    Page<BlogPost> searchBlogPosts(@Param("keyword") String keyword, Pageable pageable);
    
    // ==================== ANALYTICS ====================
    
    @Modifying
    @Query("UPDATE BlogPost b SET b.views = b.views + 1 WHERE b.id = :postId")
    void incrementViews(@Param("postId") Integer postId);
    
    @Modifying
    @Query("UPDATE BlogPost b SET b.likes = b.likes + 1 WHERE b.id = :postId")
    void incrementLikes(@Param("postId") Integer postId);
    
    @Modifying
    @Query("UPDATE BlogPost b SET b.likes = b.likes - 1 WHERE b.id = :postId AND b.likes > 0")
    void decrementLikes(@Param("postId") Integer postId);
}
