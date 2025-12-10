package Darb.curriculum_web.dto;

import lombok.Data;
import java.time.LocalDateTime;

import Darb.curriculum_web.domain.Curriculum;

@Data
public class CurriculumDto {

    private Long curriculumId;
    private String curriculumName;
    private Integer yearStart;
    private Integer yearEnd;
    private Boolean archiveStatus;
    private LocalDateTime createdDate;
    private LocalDateTime lastModified;
    private String filePath;
    private Integer course;
    private String status;

    public CurriculumDto(Curriculum curriculum) {
        this.curriculumId = curriculum.getCurriculumId();
        this.curriculumName = curriculum.getCurriculumName();
        this.yearStart = curriculum.getYearStart();
        this.yearEnd = curriculum.getYearEnd();
        this.archiveStatus = curriculum.getArchiveStatus();
        this.createdDate = curriculum.getCreatedDate();
        this.lastModified = curriculum.getLastModified();
        this.filePath = curriculum.getFilePath();
        this.course = curriculum.getCourse();
        this.status = curriculum.getStatus();
    }
    
}