package Darb.curriculum_web.dto;

import lombok.Value;

@Value 
public class TeacherInDisciplineDto {
    
    Long teacherId;
    String fio;
    String role; 
    Integer hoursAssigned; 
}
