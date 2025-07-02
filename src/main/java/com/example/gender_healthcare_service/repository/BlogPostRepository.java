package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.entity.BlogCategory;
import com.example.gender_healthcare_service.entity.BlogPost;
import com.example.gender_healthcare_service.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Integer> {


    @Query("SELECT b FROM BlogPost b JOIN b.categories c WHERE c = :category")
    Page<BlogPost> findByCategory(@Param("category") BlogCategory category, Pageable pageable);

    Page<BlogPost> findByAuthor(User author, Pageable pageable);

    @Query("SELECT b FROM BlogPost b WHERE b.isPublished = true ORDER BY b.createdAt DESC")
    Page<BlogPost> findFeaturedPosts(Pageable pageable);

    @Query("SELECT b FROM BlogPost b WHERE b.title LIKE %:keyword% OR b.content LIKE %:keyword%")
    Page<BlogPost> searchBlogPosts(@Param("keyword") String keyword, Pageable pageable);
}
