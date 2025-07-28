package com.example.gender_healthcare_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

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

    @Nationalized
    @Column(name = "Title", nullable = false, length = 200)
    private String title;

    @Column(name = "Slug", unique = true, length = 255)
    private String slug;

    @Nationalized
    @Column(name = "Summary", length = 500)
    private String summary;

    @Lob
    @Nationalized
    @Column(name = "Content", nullable = false)
    private String content;

    @Column(name = "CoverImageUrl", length = 500)
    private String coverImageUrl;

    @Column(name = "Tags", length = 1000)
    private String tags; // JSON string of tags

    @Column(name = "Views")
    @ColumnDefault("0")
    private Integer views = 0;

    @Column(name = "Likes")
    @ColumnDefault("0")
    private Integer likes = 0;

    @Column(name = "CommentsCount")
    @ColumnDefault("0")
    private Integer commentsCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AuthorID", nullable = false)
    private User author;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "BlogPost_Categories",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<BlogCategory> categories = new HashSet<>();

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