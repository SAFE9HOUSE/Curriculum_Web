package com.example.demo.dto;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.util.List;

@Data
public class StudyPlanContentDeleteRequest {
    
    @NotEmpty(message = "Список блоков для удаления не может быть пустым")
    private List<DisciplineTermDeleteDto> blocks;
    
    @Data
    public static class DisciplineTermDeleteDto {
        
        @NotNull(message = "ID дисциплины обязателен")
        private Long disciplineId;
        
        @NotNull(message = "Семестр обязателен")
        @Min(1) @Max(2)
        private Integer term;
    }
}