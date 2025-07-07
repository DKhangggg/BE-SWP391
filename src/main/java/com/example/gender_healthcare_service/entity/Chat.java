package com.example.gender_healthcare_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Chat {
    @Id
    @Column(name = "QuestionID", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CustomerID", nullable = false)
    private User customer; // Renamed from customerID to customer

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ConsultantID")
    private User consultant; // Renamed from consultantID to consultant

    @Size(max = 1000)
    @NotNull
    @Nationalized
    @Column(name = "QuestionText", nullable = false, length = 1000)
    private String questionText;

    @Size(max = 2000)
    @Nationalized
    @Column(name = "AnswerText", length = 2000)
    private String answerText;

    @Size(max = 255)
    @NotNull
    @Nationalized
    @Column(name = "Status", nullable = false)
    private String status;

    @ColumnDefault("getdate()")
    @Column(name = "CreatedAt")
    private LocalDate createdAt;

    @Column(name = "AnsweredAt")
    private LocalDate answeredAt;

    @ColumnDefault("0")
    @Column(name = "IsDeleted")
    private Boolean isDeleted;

}