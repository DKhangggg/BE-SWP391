package com.example.gender_healthcare_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "BlogPosts")
public class BlogPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PostID")
    private Integer postID;

    @Column(name = "Title", nullable = false, length = 200)
    private String title;

    @Lob
    @Column(name = "Content", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AuthorID", nullable = false)
    private User author;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "BlogPost_Categories",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<BlogCategory> categories = new HashSet<>();

    @ColumnDefault("getdate()")
    @Column(name = "PublishedDate")
    private LocalDateTime publishedDate;

    @ColumnDefault("getdate()")
    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @ColumnDefault("getdate()")
    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @Column(name = "IsPublished")
    private Boolean isPublished = false;

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;

    public void addCategory(BlogCategory category) {
        this.categories.add(category);
        category.getBlogPosts().add(this);
    }

    public void removeCategory(BlogCategory category) {
        this.categories.remove(category);
        category.getBlogPosts().remove(this);
    }
}