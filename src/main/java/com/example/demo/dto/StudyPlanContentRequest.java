package com.example.demo.dto;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.util.List;

@Data
public class StudyPlanContentRequest {
    
    @NotEmpty(message = "Список блоков не может быть пустым")
    private List<DisciplineTermBlockDto> blocks;
    
    @Data
    public static class DisciplineTermBlockDto {
        
        @NotNull(message = "ID дисциплины обязателен")
        private Long disciplineId;
        
        @NotNull(message = "Семестр обязателен")
        @Min(value = 1, message = "Семестр должен быть не менее 1")
        @Max(value = 2, message = "Семестр должен быть не более 2")
        private Integer term;
        
        @Min(value = 0, message = "Часы в семестре не могут быть отрицательными")
        @Max(value = 1000, message = "Часы в семестре не могут превышать 1000")
        private Integer termHours;
        
        private List<TeacherAssignmentDto> teachers;
    }
    
    @Data
    public static class TeacherAssignmentDto {
        
        @NotNull(message = "ID преподавателя обязателен")
        private Long teacherId;
        
        @Size(max = 50, message = "Роль преподавателя не должна превышать 50 символов")
        private String teacherRole;
        
        @Min(value = 0, message = "Назначенные часы не могут быть отрицательными")
        @Max(value = 200, message = "Назначенные часы не могут превышать 200")
        private Integer hoursAssigned;
    }
}