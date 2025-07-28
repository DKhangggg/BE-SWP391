package com.example.gender_healthcare_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.Nationalized;

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

    @Nationalized
    @Column(name = "CategoryName", nullable = false, length = 100)
    private String categoryName;

    @Column(name = "Slug", unique = true, length = 100)
    private String slug;

    @Nationalized
    @Column(name = "Description", length = 500)
    private String description;

    @Size(max = 500)
    @Column(name = "ThumbnailUrl", length = 500)
    private String thumbnailUrl;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @JsonIgnore
    @ManyToMany(mappedBy = "categories")
    private Set<BlogPost> blogPosts = new HashSet<>();
}
