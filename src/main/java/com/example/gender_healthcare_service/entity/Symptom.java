package com.example.gender_healthcare_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import org.hibernate.annotations.Nationalized;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Symptoms")
public class Symptom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SymptomID")
    private Integer id;

    @Nationalized
    @Column(name = "SymptomName", nullable = false, unique = true)
    private String symptomName;

    @Nationalized
    @Column(name = "Category")
    private String category;

    @Nationalized
    @Column(name = "Description")
    private String description;

    @OneToMany(mappedBy = "symptom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SymptomLog> symptomLogs;

    @Column(name = "IsActive")
    private Boolean isActive = true;
}
