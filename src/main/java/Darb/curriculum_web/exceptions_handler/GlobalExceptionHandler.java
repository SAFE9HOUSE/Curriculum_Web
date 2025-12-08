package Darb.curriculum_web.exceptions_handler;

import Darb.curriculum_web.dto.ErrorResponse;
import Darb.curriculum_web.dto.ResponseMetadata;
import Darb.curriculum_web.exceptions.CurriculumNotFoundException;
import Darb.curriculum_web.exceptions.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * Глобальный обработчик исключений для всего приложения.
 * Формирует стандартизированные ответы об ошибках в строго заданном формате.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Обработка отсутствия направления
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(EntityNotFoundException ex) {
        ResponseMetadata metadata = createMetadata();
        
        ErrorResponse response = new ErrorResponse();
        response.setError("Направление не найдено");
        response.setContent(ex.getMessage());
        response.setMetadata(metadata);
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(CurriculumNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCurriculumNotFound(CurriculumNotFoundException ex) {
        ResponseMetadata metadata = createMetadata();
        
        ErrorResponse response = new ErrorResponse();
        response.setError("Учебный план не найден"); // ← Чёткое описание
        response.setContent(ex.getMessage());
        response.setMetadata(metadata);
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Обработка некорректных параметров (например, null ID)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ResponseMetadata metadata = createMetadata();
        
        ErrorResponse response = new ErrorResponse();
        response.setError("Неверный запрос");
        response.setContent(ex.getMessage());
        response.setMetadata(metadata);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Обработка всех остальных ошибок
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        ResponseMetadata metadata = createMetadata();
        
        ErrorResponse response = new ErrorResponse();
        response.setError("Внутренняя ошибка сервера (возможно неверный путь)");
        response.setContent("Обратитесь к администратору");
        response.setMetadata(metadata);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // Вспомогательный метод для создания метаданных
    private ResponseMetadata createMetadata() {
        ResponseMetadata metadata = new ResponseMetadata();
        metadata.setResponseTime(LocalDateTime.now());
        return metadata;
    }
}