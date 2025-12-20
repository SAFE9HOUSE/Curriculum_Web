package com.example.demo.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.FieldOfStudy;
import com.example.demo.dto.ResponseMetadata;
import com.example.demo.dto.SuccessResponse;
import com.example.demo.exceptions.ValidateFieldId;
import com.example.demo.services.FieldOfStudyService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/fields")
@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
public class AdminFieldController {

    private final FieldOfStudyService _fieldService;

    public AdminFieldController(FieldOfStudyService fieldService) {
        _fieldService = fieldService;
    }
    
    // cоздание направления
    @PostMapping
    public ResponseEntity<SuccessResponse<FieldOfStudy>> createField(
        @Valid 
        @RequestBody FieldOfStudy fieldRequest) { 
        
        FieldOfStudy createdField = _fieldService.createField(fieldRequest);
        
        ResponseMetadata metadata = new ResponseMetadata();
        metadata.setResponseTime(LocalDateTime.now());
        
        SuccessResponse<FieldOfStudy> response = new SuccessResponse<>();
        response.setData(createdField);
        response.setMetadata(metadata);
    
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // удаление направления
    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<String>> deleteField(
        @PathVariable String id) {
        
        Long validId = ValidateFieldId.validateAndConvertFieldId(id);
        _fieldService.deleteField(validId);
        
        ResponseMetadata metadata = new ResponseMetadata();
        metadata.setResponseTime(LocalDateTime.now());
        
        SuccessResponse<String> response = new SuccessResponse<>();
        response.setData("Направление с ID " + id + " удалено");
        response.setMetadata(metadata);
    
        return ResponseEntity.ok(response);
    }
    
    // изменение направления
    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<FieldOfStudy>> updateField(
        @PathVariable String id,
        @Valid 
        @RequestBody FieldOfStudy updates) {
        
        Long validId = ValidateFieldId.validateAndConvertFieldId(id);
        FieldOfStudy updatedField = _fieldService.updateField(validId, updates);
        
        ResponseMetadata metadata = new ResponseMetadata();
        metadata.setResponseTime(LocalDateTime.now());
        
        SuccessResponse<FieldOfStudy> response = new SuccessResponse<>();
        response.setData(updatedField);
        response.setMetadata(metadata);
        
        return ResponseEntity.ok(response);
    }

}
