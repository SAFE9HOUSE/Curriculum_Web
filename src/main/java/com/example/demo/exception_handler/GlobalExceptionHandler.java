package com.example.demo.exception_handler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.demo.dto.ErrorResponse;
import com.example.demo.dto.ResponseMetadata;
import com.example.demo.exceptions.FieldNotFoundException;
import com.example.demo.exceptions.StudyPlanNotFoundException;
import com.example.demo.exceptions.DuplicateResourceException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FieldNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(FieldNotFoundException ex) {
        
        ResponseMetadata metadata = createMetadata();
        
        ErrorResponse response = new ErrorResponse();
        response.setError("Направление не найдено");
        response.setContent(ex.getMessage());
        response.setMetadata(metadata);
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(StudyPlanNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCurriculumNotFound(StudyPlanNotFoundException ex) {
        
        ResponseMetadata metadata = createMetadata();
        
        ErrorResponse response = new ErrorResponse();
        response.setError("Учебный план не найден"); 
        response.setContent(ex.getMessage());
        response.setMetadata(metadata);
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        
        ResponseMetadata metadata = createMetadata();
        
        ErrorResponse response = new ErrorResponse();
        response.setError("Неверный запрос");
        response.setContent(ex.getMessage());
        response.setMetadata(metadata);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        
        ResponseMetadata metadata = createMetadata();
        
        ErrorResponse response = new ErrorResponse();
        response.setError("Внутренняя ошибка сервера (возможно неверный путь)");
        response.setContent("Обратитесь к администратору");
        response.setMetadata(metadata);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResourceException(Exception ex) {
        
        ResponseMetadata metadata = createMetadata();
        
        ErrorResponse response = new ErrorResponse();
        response.setError("Запрос отклонен");
        response.setContent(ex.getMessage());
        response.setMetadata(metadata);
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        
        // Собираем все ошибки в список
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.toList());
        
        ErrorResponse response = new ErrorResponse();
        response.setError("Ошибка валидации");
        response.setContent("Неверные данные: " + String.join(", ", errors));
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Вспомогательный метод для создания метаданных
    private ResponseMetadata createMetadata() {
        
        ResponseMetadata metadata = new ResponseMetadata();
        metadata.setResponseTime(LocalDateTime.now());
        return metadata;
    }
}