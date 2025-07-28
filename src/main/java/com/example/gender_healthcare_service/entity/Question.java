package com.example.gender_healthcare_service.entity;

import com.example.gender_healthcare_service.entity.enumpackage.QuestionStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Nationalized;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Data
@Entity
@Table(name = "Questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer questionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Nationalized
    @Column(name = "category", nullable = false)
    private String category;

    @Nationalized
    @Column(name = "content", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionStatus status;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers = new ArrayList<>();

    @Column(name = "is_public", nullable = false)
    private boolean isPublic;

    @Column(name = "is_answered", nullable = false)
    private boolean isAnswered = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name="updated_at")
    private  LocalDateTime updatedAt;
    @Column(name = "isDeleted")
    private boolean isDeleted = false;
    @Column(name = "QuestionDate")
    private LocalDateTime questionDate;

}
