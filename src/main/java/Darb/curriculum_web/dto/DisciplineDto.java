package Darb.curriculum_web.dto;

import lombok.Value;
import java.util.List;

@Value
public class DisciplineDto {
    Long disciplineId;
    String disciplineCode;
    String disciplineName;
    Integer semester; 
    Integer totalHours; 
    List<TeacherInDisciplineDto> teachers; 
}
