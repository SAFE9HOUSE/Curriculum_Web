package com.example.demo.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.StudyPlan;
import com.example.demo.dto.ResponseMetadata;
import com.example.demo.dto.SuccessResponse;
import com.example.demo.services.StudyPlansService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/fields/{fieldId}/study-plans")
public class AdminStudyPlanController {

    private final StudyPlansService _studyService;

    public AdminStudyPlanController(StudyPlansService studyService) {
        _studyService = studyService;
    }

    // создание учебного плана
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<SuccessResponse<StudyPlan>> createStudyPlan(
        @PathVariable Long fieldId,
        @Valid @RequestBody StudyPlan request) {
        
        StudyPlan createdPlan = _studyService.createStudyPlan(fieldId, request);
        
        ResponseMetadata metadata = new ResponseMetadata();
        metadata.setResponseTime(LocalDateTime.now());
        metadata.setObjectsCount(1);
        
        SuccessResponse<StudyPlan> response = new SuccessResponse<>();
        response.setData(createdPlan);
        response.setMetadata(metadata);
    
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    // архивация учебного плана (только админ)
    @PatchMapping("/{planId}/archive")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<StudyPlan>> toggleArchiveStatus(
        @PathVariable Long fieldId,
        @PathVariable Long planId) {
   
    
        StudyPlan updatedPlan = _studyService.toggleArchiveStatus(fieldId, planId);
    
        ResponseMetadata metadata = new ResponseMetadata();
        metadata.setResponseTime(LocalDateTime.now());
        metadata.setObjectsCount(1);
    
        SuccessResponse<StudyPlan> response = new SuccessResponse<>();
        response.setData(updatedPlan);
        response.setMetadata(metadata);
    
        return ResponseEntity.ok(response);
    }
}
