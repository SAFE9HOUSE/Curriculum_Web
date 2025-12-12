package com.example.demo.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Entity
@Table(name = "field_of_study")
@Data
public class FieldOfStudy {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "field_id")
    private Long fieldId;

    @Column(name = "field_code", nullable = false, length = 20)
    private String fieldCode;

    @Column(name = "field_name", nullable = false, length = 200)
    private String fieldName;

    @Column(name = "degree_level", nullable = false, length = 200)
    private String degreeLevel;

    @Column(name = "study_length", nullable = false)
    @Min(value = 1, message = "The value cannot be zero or negative")
    @Max(value = 10, message = "The value cannot be more than 10")
    private Integer studyLength;

    @Column(name = "profile_name", nullable = false, length = 200, unique = true)
    private String profileName;

    @Column(name = "qualification", length = 200)
    private String qualification;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();
}
