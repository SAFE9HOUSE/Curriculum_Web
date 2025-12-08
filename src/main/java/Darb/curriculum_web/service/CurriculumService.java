package Darb.curriculum_web.service;

import Darb.curriculum_web.dto.CurriculumDisciplinesResponseDto;

public interface CurriculumService {
    CurriculumDisciplinesResponseDto getDisciplinesByCurriculumId(Long curriculumId);
}
