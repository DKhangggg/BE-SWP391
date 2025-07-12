package com.example.gender_healthcare_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ConsultantAvailabilities", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"ConsultantID", "DayOfWeek", "TimeSlotID"}))
public class ConsultantAvailability {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AvailabilityID", nullable = false)
    private Integer id;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ConsultantID", nullable = false)
    private Consultant consultant;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TimeSlotID", nullable = false)
    private TimeSlot timeSlot;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "DayOfWeek", nullable = false)
    private DayOfWeek dayOfWeek;
    
    @NotNull
    @Column(name = "IsAvailable", nullable = false)
    private Boolean isAvailable = true;
    
    @Column(name = "MaxBookings", nullable = false)
    @ColumnDefault("1")
    private Integer maxBookings = 1; // Số lượng booking tối đa cho time slot này
    
    @Column(name = "Notes", length = 500)
    private String notes;
    
    @ColumnDefault("getdate()")
    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;
    
    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;
    
    @ColumnDefault("0")
    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;
    
    // Relationship với ConsultantSchedule được tạo từ availability này
    @OneToMany(mappedBy = "consultantAvailability", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConsultantSchedule> consultantSchedules;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 