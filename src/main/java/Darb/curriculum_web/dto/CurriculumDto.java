package Darb.curriculum_web.dto;

import lombok.Data;
import java.time.LocalDateTime;

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
}