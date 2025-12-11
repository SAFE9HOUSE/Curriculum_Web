package com.example.demo.dto;
import lombok.Value;

@Value 
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
