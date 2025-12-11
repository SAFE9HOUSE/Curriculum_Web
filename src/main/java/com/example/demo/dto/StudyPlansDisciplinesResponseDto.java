package com.example.demo.dto;

import java.util.List;

import lombok.Value;

@Value
public class StudyPlansDisciplinesResponseDto {
    
    Long studyPlanId;
    String studyPlanName;
    Integer course; 
    Integer yearStart;
    Integer yearEnd;
    Boolean archiveStatus;
    String filePath;
    String status;
    FieldOfStudyBriefDto fieldOfStudy;
    List<DisciplineDto> disciplines;
}
