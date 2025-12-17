package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.FieldOfStudy;
import com.example.demo.dto.ResponseMetadata;
import com.example.demo.dto.SuccessResponse;
import com.example.demo.services.FieldOfStudyService;

import com.example.demo.exceptions.ValidateFieldId;

@RestController
@RequestMapping("/api/fields")
public class FieldOfStudyController {
    private final FieldOfStudyService _fieldService;

    public FieldOfStudyController(FieldOfStudyService fieldService) {
        _fieldService = fieldService;
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<List<FieldOfStudy>>> getAllFields(
        @RequestParam(required = false) String search) {
        
            List<FieldOfStudy> fields = _fieldService.getAllFields(search);
        
            ResponseMetadata metadata = new ResponseMetadata();
            metadata.setResponseTime(LocalDateTime.now());
            metadata.setObjectsCount(fields.size()); 
        
            SuccessResponse<List<FieldOfStudy>> response = new SuccessResponse<>();
            response.setData(fields);
            response.setMetadata(metadata);
        
            return ResponseEntity.ok(response);
        }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<Map<String, Object>>> getFieldWithCurricula(@PathVariable String id) {
        
        Long validId = ValidateFieldId.validateAndConvertFieldId(id);
        
        Map<String, Object> serviceData = _fieldService.getFieldWithCurricula(validId);
        
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
