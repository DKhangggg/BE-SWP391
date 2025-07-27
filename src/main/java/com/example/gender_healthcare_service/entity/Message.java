package com.example.gender_healthcare_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

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

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ConversationID", nullable = false)
    private Chat conversation;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SenderID", nullable = false)
    private User sender;

    @Size(max = 2000)
    @NotNull
    @Nationalized
    @Column(name = "Content", nullable = false, length = 2000)
    private String content;

    @Size(max = 50)
    @NotNull
    @Nationalized
    @Column(name = "MessageType", nullable = false, length = 50)
    private String messageType; // TEXT, IMAGE, FILE

    @Size(max = 500)
    @Column(name = "AttachmentUrl", length = 500)
    private String attachmentUrl;

    @Size(max = 255)
    @Column(name = "FileName", length = 255)
    private String fileName;

    @Column(name = "FileSize")
    private Long fileSize;

    @Size(max = 50)
    @NotNull
    @Nationalized
    @Column(name = "Status", nullable = false, length = 50)
    private String status; // SENT, DELIVERED, READ

    @ColumnDefault("getdate()")
    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @Column(name = "ReadAt")
    private LocalDateTime readAt;

    @Column(name = "IsEdited")
    private Boolean isEdited = false;

    @Column(name = "EditedAt")
    private LocalDateTime editedAt;

    @ColumnDefault("0")
    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;
} 