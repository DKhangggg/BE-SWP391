package com.example.gender_healthcare_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "Locations")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Nationalized
    @Column(name = "name", nullable = false)
    private String name;

    @Nationalized
    @Column(name = "address", nullable = false)
    private String address;

    @Nationalized
    @Column(name = "phone", nullable = false)
    private String phone;

    @Nationalized
    @Column(name = "hours")
    private String hours;

    @Nationalized
    @Column(name = "status")
    private String status;

    @ColumnDefault("0")
    @Column(name = "isDeleted")
    private Boolean isDeleted = false;

    @Column(name = "createAt")
    private LocalDateTime createAt;

    @Column(name = "updateAt")
    private LocalDateTime updateAt;
} 