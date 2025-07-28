package com.example.gender_healthcare_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "Messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MessageID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ConversationID", nullable = false)
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SenderID", nullable = false)
    private User sender;

    @Column(name = "Content", length = 2000, nullable = false)
    private String content;

    @Column(name = "MessageType", length = 20, nullable = false)
    private String messageType = "TEXT"; // TEXT, IMAGE, FILE

    @Column(name = "Status", length = 20)
    @ColumnDefault("'SENT'")
    private String status = "SENT"; // SENT, DELIVERED, READ

    @ColumnDefault("getdate()")
    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @Column(name = "ReadAt")
    private LocalDateTime readAt;

    @ColumnDefault("0")
    @Column(name = "IsEdited")
    private Boolean isEdited = false;

    @Column(name = "EditedAt")
    private LocalDateTime editedAt;

    @ColumnDefault("0")
    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        if (isEdited != null && isEdited) {
            editedAt = LocalDateTime.now();
        }
    }
}
