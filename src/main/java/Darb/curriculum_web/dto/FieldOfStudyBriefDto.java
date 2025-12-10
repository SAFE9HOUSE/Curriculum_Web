package Darb.curriculum_web.dto;

import lombok.Value;

@Value
public class FieldOfStudyBriefDto {
    Long fieldId;
    String fieldCode;
    String fieldName;
    String degreeLevel; 
    Integer studyLength;
    String profileName;
}
