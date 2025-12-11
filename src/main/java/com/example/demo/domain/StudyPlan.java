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
import lombok.Data;

@Entity
@Table(name = "curriculum")
@Data
public class StudyPlan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "curriculum_id")
    private Long studyPlanId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_id", nullable = false) 
    @JsonIgnore
    private FieldOfStudy field;

    @Column(name = "curriculum_name", nullable = false, length = 200)
    private String studyPlanName;

    @Column(name = "year_start", nullable = false)
    @Min(value = 2015, message = "The year can't be earlier than 2015")
    @Max(value = 2026, message = "The year cannot be later than 2026")
    private Integer yearStart;

    @Column(name = "year_end", nullable = false)
    @Min(value = 2025, message = "The year can't be earlier than 2025")
    @Max(value = 2050, message = "The year cannot be later than 2050")
    private Integer yearEnd;

    @Column(name = "archive_status")
    private Boolean archiveStatus = false;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "last_modified")
    private LocalDateTime lastModified;

    @Column(name = "file_path", length = 300)
    private String filePath;

    @Column(name = "course", nullable = false)
    @Min(value = 1, message = "The course number cannot be less than 1")
    @Max(value = 6, message = "the course number cannot be more than 6")
    private Integer course;

    @Column(name = "status", length = 20)
    private String status = "draft";

    @PreUpdate
    public void preUpdate() {
        this.lastModified = LocalDateTime.now();
    }
}
