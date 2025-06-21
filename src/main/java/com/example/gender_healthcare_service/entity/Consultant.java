package com.example.gender_healthcare_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

@Getter
@Setter
@Entity
@Table(name = "Consultants")
public class Consultant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ConsultantID", nullable = false)
    private Integer id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ConsultantID", nullable = false)
    private User user;

    @Nationalized
    @Column(name = "Biography", length = 1000)
    private String biography;

    @Nationalized
    @Column(name = "Qualifications", length = 500)
    private String qualifications;

    @Column(name = "ExperienceYears")
    private Integer experienceYears;


    @Nationalized
    @Column(name = "Specialization", length = 100)
    private String specialization;

    @ColumnDefault("0")
    @Column(name = "IsDeleted")
    private Boolean isDeleted;

}