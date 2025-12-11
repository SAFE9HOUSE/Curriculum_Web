package com.example.demo.dto;

import java.time.LocalDateTime;

import com.example.demo.domain.StudyPlan;

import lombok.Data;

@Data
public class StudyPlanDto {
    
    private Long StudyPlanId;
    private String StudyPlanName;
    private Integer yearStart;
    private Integer yearEnd;
    private Boolean archiveStatus;
    private LocalDateTime createdDate;
    private LocalDateTime lastModified;
    private String filePath;
    private Integer course;
    private String status;

    public StudyPlanDto(StudyPlan curriculum) {
        this.StudyPlanId = curriculum.getStudyPlanId();
        this.StudyPlanName = curriculum.getStudyPlanName();
        this.yearStart = curriculum.getYearStart();
        this.yearEnd = curriculum.getYearEnd();
        this.archiveStatus = curriculum.getArchiveStatus();
        this.createdDate = curriculum.getCreatedDate();
        this.lastModified = curriculum.getLastModified();
        this.filePath = curriculum.getFilePath();
        this.course = curriculum.getCourse();
        this.status = curriculum.getStatus();
    }
}
