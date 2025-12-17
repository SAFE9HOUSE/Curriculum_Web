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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Entity
@Table(name = "field_of_study")
@Data
public class FieldOfStudy {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "field_id", updatable = false, insertable = false)
    private Long fieldId;
    
    @NotBlank(message = "Код направления обязателен")
    @Size(max = 20, message = "Код не должен превышать 20 символов")
    @Column(name = "field_code", nullable = false, length = 20)
    private String fieldCode;
    
    @NotBlank(message = "Название направления обязательно")
    @Size(max = 200, message = "Название не должно превышать 200 символов")
    @Column(name = "field_name", nullable = false, length = 200)
    private String fieldName;
    
    @NotBlank(message = "Уровень образования обязателен")
    @Size(max = 200, message = "Уровень образования не должен превышать 200 символов")
    @Column(name = "degree_level", nullable = false, length = 200)
    private String degreeLevel;
    
    @NotNull(message = "Длительность обучения обязательна")
    @Min(value = 1, message = "Длительность должна быть не менее 1 года")
    @Max(value = 10, message = "Длительность должна быть не более 10 лет")
    @Column(name = "study_length", nullable = false)
    private Integer studyLength;
    
    @NotBlank(message = "Наименование профиля обязательно")
    @Size(max = 200, message = "Наименование профиля не должно превышать 200 символов")
    @Column(name = "profile_name", nullable = false, length = 200, unique = true)
    private String profileName;
    
    @Size(max = 200, message = "Квалификация не должна превышать 200 символов")
    @Column(name = "qualification", length = 200)
    private String qualification;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();
}
