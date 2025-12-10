package Darb.curriculum_web.dto;

import lombok.Value;
import java.util.List;

@Value
public class DisciplineInTermDto {
    Long disciplineId;
    String disciplineCode;
    String disciplineName;
    Integer term;
    Integer termHours;
    Integer independentHours;
    String report;
    String description;
    String competences;
    List<TeacherInDisciplineDto> teachers; 
}
