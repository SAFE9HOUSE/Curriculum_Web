package com.example.demo.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.FieldOfStudy;
import com.example.demo.dto.ResponseMetadata;
import com.example.demo.dto.SuccessResponse;
import com.example.demo.services.FieldOfStudyService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/fields")
@PreAuthorize("hasRole('MANAGER', 'ADMIN')")
public class AdminFieldController {

    private final FieldOfStudyService _fieldService;

    public AdminFieldController(FieldOfStudyService fieldService) {
        _fieldService = fieldService;
    }
    
    // cоздание направления
    @PostMapping
    public ResponseEntity<SuccessResponse<FieldOfStudy>> createField(
        @Valid @RequestBody FieldOfStudy fieldRequest) { 
        
        FieldOfStudy createdField = _fieldService.createField(fieldRequest);
        
        ResponseMetadata metadata = new ResponseMetadata();
        metadata.setResponseTime(LocalDateTime.now());
        metadata.setObjectsCount(1);
        
        SuccessResponse<FieldOfStudy> response = new SuccessResponse<>();
        response.setData(createdField);
        response.setMetadata(metadata);
    
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
