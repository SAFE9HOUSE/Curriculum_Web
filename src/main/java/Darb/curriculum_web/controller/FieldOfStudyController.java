package Darb.curriculum_web.controller;

import Darb.curriculum_web.domain.FieldOfStudy;
import Darb.curriculum_web.dto.ResponseMetadata;
import Darb.curriculum_web.dto.SuccessResponse;
import Darb.curriculum_web.service.FieldOfStudyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fields")
public class FieldOfStudyController {

    private final FieldOfStudyService fieldService;

    // Constructor injection вместо @Autowired на поле
    public FieldOfStudyController(FieldOfStudyService fieldService) {
        this.fieldService = fieldService;
    }

    // Единый формат для списка
    @GetMapping
    public ResponseEntity<SuccessResponse<List<FieldOfStudy>>> getAllFields(
        @RequestParam(required = false) String search
    ) {
        List<FieldOfStudy> fields = fieldService.getAllFields(search);
        
        ResponseMetadata metadata = new ResponseMetadata();
        metadata.setResponseTime(LocalDateTime.now());
        metadata.setObjectsCount(fields.size()); // Для списка можно использовать как totalCount
        
        SuccessResponse<List<FieldOfStudy>> response = new SuccessResponse<>();
        response.setData(fields);
        response.setMetadata(metadata);
        
        return ResponseEntity.ok(response);
    }

    // Уже существующий метод с улучшениями
    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<Map<String, Object>>> getFieldWithCurricula(@PathVariable Long id) {
        Map<String, Object> serviceData = fieldService.getFieldWithCurricula(id);
        
        List<?> curricula = (List<?>) serviceData.get("uchebniye_plany");

        ResponseMetadata metadata = new ResponseMetadata();
        metadata.setObjectsCount(curricula.size());
        metadata.setResponseTime(LocalDateTime.now());

        SuccessResponse<Map<String, Object>> response = new SuccessResponse<>();
        response.setData(serviceData);
        response.setMetadata(metadata);
        
        return ResponseEntity.ok(response);
    }
}