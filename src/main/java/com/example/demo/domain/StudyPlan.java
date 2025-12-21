package com.example.demo.domain;

import java.time.LocalDateTime;
  
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "curriculum")
@Data
public class StudyPlan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "curriculum_id", updatable = false, insertable = false)
    private Long studyPlanId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_id", nullable = false, updatable = false) 
    @JsonIgnore
    private FieldOfStudy field;
    
    @Size(max = 200, message = "Название учебного плана не должно превышать 200 символов")
    @Column(name = "curriculum_name", nullable = false, length = 200)
    private String studyPlanName;
    
    @Min(value = 2015, message = "Год начала действия учебного плана не может быть раньше 2015")
    @Max(value = 2025, message = "Год начала действия учебного плана не может быть позже 2025")
    @Column(name = "year_start", nullable = false)
    private Integer yearStart;
    
    @Min(value = 2025, message = "Год окончания действия учебного плана не может быть раньше 2017")
    @Max(value = 2035, message = "Год окончания действия учебного плана не может быть позже 2035")
    @Column(name = "year_end", nullable = false)
    private Integer yearEnd;
    
    @Column(name = "archive_status")
    private Boolean archiveStatus = false;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "last_modified")
    private LocalDateTime lastModified;
    
    @Size(max = 300, message = "Путь к файлам учебного плана не должен превышать 300 символов")
    @Column(name = "file_path", length = 300)
    private String filePath;
    
    @Min(value = 1, message = "Курс не может быть меньше 1")
    @Max(value = 10, message = "Курс не может быть больше 10")
    @Column(name = "course", nullable = false)
    private Integer course;
    
    @Size(max = 20, message = "Статус или версия учебного плана ограничены 20 символами")
    @Column(name = "status", length = 20, nullable = false)
    private String status = "1.00";

    @PreUpdate
    public void preUpdate() {
        this.lastModified = LocalDateTime.now();
    }
    
}
