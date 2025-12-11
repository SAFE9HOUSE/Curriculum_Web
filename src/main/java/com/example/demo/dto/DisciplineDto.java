package com.example.demo.dto;

import java.util.List;

import lombok.Value;

@Value
public class DisciplineDto {
    
    Long disciplineId;
    String disciplineCode;
    String disciplineName;
    Integer term; 
    Integer totalHours; 
    Integer independentHours;
    String report;
    String description;
    String competences;
    List<TeacherInDisciplineDto> teachers; 
}
