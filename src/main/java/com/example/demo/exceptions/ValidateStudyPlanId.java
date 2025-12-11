package com.example.demo.exceptions;

public class ValidateStudyPlanId {

      public static Long validateAndConvertStudyPlanId(String idInput) {
       
        if (idInput == null || idInput.trim().isEmpty()) {
            throw new IllegalArgumentException("ID учебного плана не может быть пустым");
        }
    
        String trimmed = idInput.trim();
    
        if (!trimmed.matches("\\d+")) {
        throw new IllegalArgumentException("ID учебного плана должен быть целым положительным числом");
        }
    
        try {
            Long id = Long.parseLong(trimmed);
        
            if (id <= 0) {
                throw new IllegalArgumentException("ID учебного плана должен быть больше 0");
            }
        
            return id;
        
        }   
        
        catch (NumberFormatException e) {
        
            throw new IllegalArgumentException("ID учебного плана не может быть таким большим");
        }
    }
}
