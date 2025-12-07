package Darb.curriculum_web.dto;

import Darb.curriculum_web.domain.FieldOfStudy;
import Darb.curriculum_web.domain.Curriculum;
import lombok.Data;
import java.util.List;

@Data
public class FieldWithCurriculaResponse {
    private FieldOfStudy direction_info;
    private List<Curriculum> uchebniye_plany;
}