package com.example.gender_healthcare_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID", nullable = false)
    private User user;

    @Nationalized
    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private boolean isRead = false;

    private String link;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Nationalized
    @Column(name = "Type")
    private String type; // CONSULTATION, TEST_RESULT, CYCLE_REMINDER, QUESTION_ANSWERED

    @Nationalized
    @Column(name = "Title")
    private String title;

    @Nationalized
    @Column(name = "Description")
    private String description;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

