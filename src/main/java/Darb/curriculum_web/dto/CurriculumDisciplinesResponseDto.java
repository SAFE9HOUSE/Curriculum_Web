package Darb.curriculum_web.dto;

import lombok.Value;
import java.util.List;

@Value
public class CurriculumDisciplinesResponseDto {
    Long curriculumId;
    String curriculumName;
    Integer course; 
    FieldOfStudyBriefDto fieldOfStudy;
    List<DisciplineDto> disciplines;
}
