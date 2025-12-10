package Darb.curriculum_web.dto;

import lombok.Value;
import java.util.List;

@Value
public class TermsGroupDto {
    
    Integer term; 
    List<DisciplineInTermDto> disciplines; // Дисциплины ТОЛЬКО этого семестра
}
