package com.example.demo.controller;
import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ResponseMetadata;
import com.example.demo.dto.StudyPlansDisciplinesResponseDto;
import com.example.demo.dto.SuccessResponse;
import com.example.demo.services.StudyPlansService;


@RestController
@RequestMapping("/api/curricula")
public class StudyPlansController {

    private final StudyPlansService _studyPlanService;

    public StudyPlansController(StudyPlansService studyPlanService) {
        _studyPlanService = studyPlanService;
    }

    @GetMapping("/{studyPlanId}")
    public ResponseEntity<SuccessResponse<StudyPlansDisciplinesResponseDto>> getStudyPlanDisciplines(
            @PathVariable Long studyPlanId) {

        if (studyPlanId == null || studyPlanId <= 0) {
            throw new IllegalArgumentException("Id учебного плана должно быть положительным целым числом");
        }

        StudyPlansDisciplinesResponseDto data = _studyPlanService.getDisciplinesByStudyPlanId(studyPlanId);

        ResponseMetadata metadata = new ResponseMetadata();
        metadata.setResponseTime(LocalDateTime.now());
        metadata.setObjectsCount(data.getDisciplines().size()); 

        SuccessResponse<StudyPlansDisciplinesResponseDto> response = new SuccessResponse<>();
        response.setData(data);
        response.setMetadata(metadata);

        return ResponseEntity.ok(response);
    }
}
