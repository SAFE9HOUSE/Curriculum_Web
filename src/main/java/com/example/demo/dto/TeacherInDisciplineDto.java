package com.example.demo.dto;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value 
@EqualsAndHashCode(of = {"teacherId", "role"})  // Уникальность по teacherId + role
public class TeacherInDisciplineDto {
   
    Long teacherId;
    String fio;
    String role; 
    Integer hoursAssigned; 
    String department;
    String post;
    String academicStatus;
    String academicDegree;
    String email;
    String phone;
    String information;
}
