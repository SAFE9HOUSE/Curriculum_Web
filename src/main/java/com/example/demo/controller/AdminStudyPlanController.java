package com.example.demo.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.StudyPlan;
import com.example.demo.dto.ResponseMetadata;
import com.example.demo.dto.StudyPlanContentDeleteRequest;
import com.example.demo.dto.StudyPlanContentRequest;
import com.example.demo.dto.StudyPlanContentUpdateRequest;
import com.example.demo.dto.SuccessResponse;
import com.example.demo.exceptions.ValidateFieldId;
import com.example.demo.exceptions.ValidateStudyPlanId;
import com.example.demo.services.StudyPlanContentService;
import com.example.demo.services.StudyPlansService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/fields/{fieldId}/study-plans")
public class AdminStudyPlanController {

    private final StudyPlansService _studyService;
    private final StudyPlanContentService _contentService;

    public AdminStudyPlanController(StudyPlansService studyService,
        StudyPlanContentService contentService) {
        _studyService = studyService;
        _contentService = contentService;
    }

    // создание учебного плана
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<SuccessResponse<StudyPlan>> createStudyPlan(
        @PathVariable String fieldId,
        @Valid 
        @RequestBody StudyPlan request) {

        Long validFieldId = ValidateFieldId.validateAndConvertFieldId(fieldId);
        
        StudyPlan createdPlan = _studyService.createStudyPlan(validFieldId, request);
        
        ResponseMetadata metadata = new ResponseMetadata();
        metadata.setResponseTime(LocalDateTime.now());
        
        SuccessResponse<StudyPlan> response = new SuccessResponse<>();
        response.setData(createdPlan);
        response.setMetadata(metadata);
    
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    // архивация учебного плана (только админ)
    @PatchMapping("/{planId}/archive")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<StudyPlan>> toggleArchiveStatus(
        @PathVariable String fieldId,
        @PathVariable String planId) {
        
        Long validFieldId = ValidateFieldId.validateAndConvertFieldId(fieldId);
        Long validStudyPlanId = ValidateStudyPlanId.validateAndConvertStudyPlanId(planId);
        
        StudyPlan updatedPlan = _studyService.toggleArchiveStatus(validFieldId, validStudyPlanId);
    
        ResponseMetadata metadata = new ResponseMetadata();
        metadata.setResponseTime(LocalDateTime.now());
    
        SuccessResponse<StudyPlan> response = new SuccessResponse<>();
        response.setData(updatedPlan);
        response.setMetadata(metadata);
    
        return ResponseEntity.ok(response);
    }

    // удаление учебного плана
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @DeleteMapping("/{planId}")
    public ResponseEntity<SuccessResponse<String>> deleteStudyPlan(
        @PathVariable String fieldId,
        @PathVariable String planId) {
    
        Long validFieldId = ValidateFieldId.validateAndConvertFieldId(fieldId);
        Long validStudyPlanId = ValidateStudyPlanId.validateAndConvertStudyPlanId(planId);
    
        _studyService.deleteStudyPlan(validFieldId, validStudyPlanId);
    
        ResponseMetadata metadata = new ResponseMetadata();
        metadata.setResponseTime(LocalDateTime.now());
    
        SuccessResponse<String> response = new SuccessResponse<>();
        response.setData(String.format(
            "Учебный план с ID %s удален из направления с ID %s", 
            planId, fieldId));
        
        response.setMetadata(metadata);
    
        return ResponseEntity.ok(response);
    }

    // обновление учебного плана
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @PutMapping("/{planId}")
    public ResponseEntity<SuccessResponse<StudyPlan>> updateStudyPlan(
        @PathVariable String fieldId,
        @PathVariable String planId,
        @Valid
        @RequestBody StudyPlan updates) {

        Long validFieldId = ValidateFieldId.validateAndConvertFieldId(fieldId);
        Long validStudyPlanID = ValidateStudyPlanId.validateAndConvertStudyPlanId(planId);
        
        StudyPlan updateStudyPlan = _studyService.updateStudyPlan(validFieldId, 
            validStudyPlanID, updates);
        
        ResponseMetadata metadata = new ResponseMetadata();
        metadata.setResponseTime(LocalDateTime.now());
        
        SuccessResponse<StudyPlan> response = new SuccessResponse<>();
        response.setData(updateStudyPlan);
        response.setMetadata(metadata);
    
        return ResponseEntity.ok(response);
    }
    
    // создание контента учебного плана
    @PostMapping("/{planId}/content")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<SuccessResponse<String>> addContentToPlan(
        @PathVariable String fieldId,
        @PathVariable String planId,
        @Valid @RequestBody StudyPlanContentRequest request) {
    
        Long validFieldId = ValidateFieldId.validateAndConvertFieldId(fieldId);
        Long validPlanId = ValidateStudyPlanId.validateAndConvertStudyPlanId(planId);
    
       _contentService.addContentToPlan(validFieldId, validPlanId, request);

        ResponseMetadata metadata = new ResponseMetadata();
        metadata.setResponseTime(LocalDateTime.now());
    
        SuccessResponse<String> response = new SuccessResponse<>();
        response.setData("Контент успешно добавлен в учебный план");
        response.setMetadata(metadata);
    
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    // удаление контента учебного плана
    @DeleteMapping("/{planId}/content")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<SuccessResponse<String>> removeContentFromPlan(
        @PathVariable String fieldId,
        @PathVariable String planId,
        @Valid @RequestBody StudyPlanContentDeleteRequest request) {
    
        Long validFieldId = ValidateFieldId.validateAndConvertFieldId(fieldId);
        Long validPlanId = ValidateStudyPlanId.validateAndConvertStudyPlanId(planId);
    
        _contentService.removeContentFromPlan(validFieldId, validPlanId, request);
    
        ResponseMetadata metadata = new ResponseMetadata();
        metadata.setResponseTime(LocalDateTime.now());
    
        SuccessResponse<String> response = new SuccessResponse<>();
        response.setData("Контент успешно удален из учебного плана");
        response.setMetadata(metadata);
    
        return ResponseEntity.ok(response);
    }
    
    // частичное обновление контента учебного плана
    @PutMapping("/{planId}/content")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<SuccessResponse<String>> updateContentInPlan(
        @PathVariable String fieldId,
        @PathVariable String planId,
        @Valid @RequestBody StudyPlanContentUpdateRequest request) {

        Long validFieldId = ValidateFieldId.validateAndConvertFieldId(fieldId);
        Long validPlanId = ValidateStudyPlanId.validateAndConvertStudyPlanId(planId);

        _contentService.updateContentInPlan(validFieldId, validPlanId, request);

        ResponseMetadata metadata = new ResponseMetadata();
        metadata.setResponseTime(LocalDateTime.now());

        SuccessResponse<String> response = new SuccessResponse<>();
        response.setData("Контент учебного плана успешно обновлен");
        response.setMetadata(metadata);

        return ResponseEntity.ok(response);
    }
}
