package com.example.demo.controller;
import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ResponseMetadata;
import com.example.demo.dto.StudyPlansDisciplinesResponseDto;
import com.example.demo.dto.SuccessResponse;
import com.example.demo.services.StudyPlansService;
import com.example.demo.services.PdfGenerateService;
import org.springframework.http.*;

import com.example.demo.exceptions.ValidateStudyPlanId;

@RestController
@RequestMapping("/api/curricula")
public class StudyPlansController {

    private final StudyPlansService _studyPlanService;
    private final PdfGenerateService _pdfGenerateService;

    public StudyPlansController(StudyPlansService studyPlanService,
        PdfGenerateService pdfGenerateService) {
        _studyPlanService = studyPlanService;
        _pdfGenerateService = pdfGenerateService;
    }

    @GetMapping("/{studyPlanId}")
    public ResponseEntity<SuccessResponse<StudyPlansDisciplinesResponseDto>> getStudyPlanDisciplines(
            @PathVariable String studyPlanId) {
               
        Long validateStudyPlanId = ValidateStudyPlanId.validateAndConvertStudyPlanId(studyPlanId);

        StudyPlansDisciplinesResponseDto data = _studyPlanService.getDisciplinesByStudyPlanId(validateStudyPlanId);

        ResponseMetadata metadata = new ResponseMetadata();
        metadata.setResponseTime(LocalDateTime.now());
        metadata.setObjectsCount(data.getDisciplines().size()); 

        SuccessResponse<StudyPlansDisciplinesResponseDto> response = new SuccessResponse<>();
        response.setData(data);
        response.setMetadata(metadata);

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/{studyPlanId}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getStudyPlanPdf
    (@PathVariable String studyPlanId) throws Exception {
        
        Long validateStudyPlanId = ValidateStudyPlanId.validateAndConvertStudyPlanId(studyPlanId);
        StudyPlansDisciplinesResponseDto data = _studyPlanService.getDisciplinesByStudyPlanId(validateStudyPlanId);
        byte[] pdf = _pdfGenerateService.generateStudyPlanPdf(data);
    
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"study-plan-" + studyPlanId + ".pdf\"")
                .body(pdf);
    }
    
}


