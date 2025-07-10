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
@Table(name = "Reminders")
public class Reminder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ReminderID", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "UserID", nullable = false)
    private User user;

    @Size(max = 255)
    @NotNull
    @Nationalized
    @Column(name = "ReminderType", nullable = false)
    private String reminderType;

    @NotNull
    @Column(name = "ReminderDate", nullable = false)
    private LocalDateTime reminderDate;

    @NotNull
    @Column(name = "ReminderTime", nullable = false)
    private LocalDateTime reminderTime;

    @Size(max = 200)
    @Nationalized
    @Column(name = "Message", length = 200)
    private String message;

    @ColumnDefault("0")
    @Column(name = "IsSent")
    private Boolean isSent;

    @ColumnDefault("getdate()")
    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @ColumnDefault("0")
    @Column(name = "IsDeleted")
    private Boolean isDeleted;

}