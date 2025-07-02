package com.example.gender_healthcare_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "BlogCategories")
public class BlogCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CategoryID")
    private Integer categoryID;

    @Column(name = "CategoryName", nullable = false, length = 100)
    private String categoryName;

    @Column(name = "Description", length = 500)
    private String description;

    @Column(name = "CreatedAt")
    private LocalDate createdAt;

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;

    @Column(name = "UpdatedAt")
    private LocalDate updatedAt;

    @ManyToMany(mappedBy = "categories")
    private Set<BlogPost> blogPosts = new HashSet<>();
}
