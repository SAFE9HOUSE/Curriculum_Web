package com.example.demo.exceptions;

public class ValidateFieldId {
    
    public static Long validateAndConvertFieldId(String idInput) {
       
        if (idInput == null || idInput.trim().isEmpty()) {
            throw new IllegalArgumentException("ID направления не может быть пустым");
        }
    
        String trimmed = idInput.trim();
    
        // Проверяем, что строка содержит только цифры (целое число)
        if (!trimmed.matches("\\d+")) {
        throw new IllegalArgumentException("ID направления должен быть целым положительным числом");
        }
    
        try {
            Long id = Long.parseLong(trimmed);
        
            if (id <= 0) {
                throw new IllegalArgumentException("ID направления должен быть больше 0");
            }
        
            return id;
        
        }   
        
        catch (NumberFormatException e) {
        
            // На всякий случай, если очень большое число
            throw new IllegalArgumentException("ID направления не может быть таким большим");
        }
    }
}
