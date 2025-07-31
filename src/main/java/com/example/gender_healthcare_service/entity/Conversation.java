package com.example.gender_healthcare_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "Conversations")
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ConversationID", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CustomerID", nullable = false)
    private User customer;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ConsultantID", nullable = false)
    private User consultant;

    @Nationalized
    @Column(name = "LastMessage", length = 1000)
    private String lastMessage;

    @Column(name = "LastMessageTime")
    private LocalDateTime lastMessageTime;

    @Column(name = "CustomerUnreadCount")
    @ColumnDefault("0")
    private Integer customerUnreadCount = 0;

    @Column(name = "ConsultantUnreadCount")
    @ColumnDefault("0")
    private Integer consultantUnreadCount = 0;

    @Nationalized
    @Column(name = "Status", length = 50)
    @ColumnDefault("'ACTIVE'")
    private String status = "ACTIVE"; // ACTIVE, ARCHIVED, BLOCKED

    @ColumnDefault("getdate()")
    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @ColumnDefault("getdate()")
    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @ColumnDefault("0")
    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Message> messages = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper methods
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }

    public boolean isArchived() {
        return "ARCHIVED".equals(status);
    }

    public boolean isBlocked() {
        return "BLOCKED".equals(status);
    }

    public void incrementCustomerUnread() {
        this.customerUnreadCount++;
    }

    public void incrementConsultantUnread() {
        this.consultantUnreadCount++;
    }

    public void resetCustomerUnread() {
        this.customerUnreadCount = 0;
    }

    public void resetConsultantUnread() {
        this.consultantUnreadCount = 0;
    }
} 