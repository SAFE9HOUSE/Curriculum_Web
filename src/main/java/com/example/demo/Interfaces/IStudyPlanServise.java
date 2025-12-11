package com.example.demo.Interfaces;

import com.example.demo.dto.StudyPlansDisciplinesResponseDto;

public interface IStudyPlanServise {
    StudyPlansDisciplinesResponseDto getDisciplinesByStudyPlanId(Long curriculumId);    
}
