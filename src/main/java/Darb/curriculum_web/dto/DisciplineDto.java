package Darb.curriculum_web.dto;

import lombok.Value;
import java.util.List;

@Value
public class DisciplineDto {
    Long disciplineId;
    String disciplineCode;
    String disciplineName;
    Integer term; 
    Integer totalHours; 
    Integer independentHours;
    String report;
    String description;
    String competences;
    List<TeacherInDisciplineDto> teachers; 
}
