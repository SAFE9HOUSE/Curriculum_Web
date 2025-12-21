package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudyPlanContentUpdateRequest {
    
    @NotNull(message = "Список обновлений не может быть null")
    private List<DisciplineUpdateDto> updates;
    
    @Data 
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DisciplineUpdateDto {
        
        @NotNull(message = "ID дисциплины обязателен")
        @Positive(message = "ID дисциплины должен быть положительным")
        private Long disciplineId;
        
        @NotNull(message = "Текущий семестр обязателен")
        @Min(value = 1, message = "Семестр должен быть не менее 1")
        @Max(value = 2, message = "Семестр должен быть не более 2")
        private Integer currentTerm;
        
        @NotNull(message = "Новый семестр обязателен")
        @Min(value = 1, message = "Семестр должен быть не менее 1")
        @Max(value = 2, message = "Семестр должен быть не более 2")
        private Integer newTerm;
        
        @Min(value = 0, message = "Часы не могут быть отрицательными")
        @Max(value = 1000, message = "Максимально 1000 часов")
        private Integer newTermHours;
        
        private List<StudyPlanContentRequest.TeacherAssignmentDto> newTeachers;
    }
}