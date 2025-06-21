package com.example.gender_healthcare_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/homepage")
public class HomepageController {

    @GetMapping("/details")
    public ResponseEntity<?> getHomepageDetails() {
        return null;
    }

    @GetMapping("/blog/posts")
    public ResponseEntity<?> getBlogPosts() {
        return null;
    }

    @GetMapping("/blog/posts/{postId}")
    public ResponseEntity<?> getBlogPostById(@PathVariable String postId) {
        return null;
    }
}

