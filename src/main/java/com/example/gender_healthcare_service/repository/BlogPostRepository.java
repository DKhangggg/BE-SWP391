package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.entity.BlogPost;
import com.example.gender_healthcare_service.entity.BlogCategory;
import com.example.gender_healthcare_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Integer> {
    List<BlogPost> findByCategory(BlogCategory category);
    List<BlogPost> findByAuthor(User author);
}

