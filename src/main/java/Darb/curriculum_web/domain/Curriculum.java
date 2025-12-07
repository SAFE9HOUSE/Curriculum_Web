package Darb.curriculum_web.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;
import jakarta.validation.constraints.Min; 
import jakarta.validation.constraints.Max;  
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "curriculum")
@Data
public class Curriculum {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "curriculum_id")
    private Long curriculumId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_id", nullable = false) 
    @JsonIgnore
    private FieldOfStudy field;

    @Column(name = "curriculum_name", nullable = false, length = 200)
    private String curriculumName;

    @Column(name = "year_start", nullable = false)
    @Min(value = 2015, message = "The year can't be earlier than 2015")
    @Max(value = 2026, message = "The year cannot be later than 2026")
    private Integer yearStart;

    @Column(name = "year_end", nullable = false)
    @Min(value = 2025, message = "The year can't be earlier than 2025")
    @Max(value = 2050, message = "The year cannot be later than 2050")
    private Integer yearEnd;

    @Column(name = "archive_status")
    private boolean archiveStatus = false;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "last_modified")
    private LocalDateTime lastModified;

    @Column(name = "file_path", length = 300)
    private String filePath;

    @Column(name = "course", nullable = false)
    @Min(value = 1, message = "The course number cannot be less than 1")
    @Max(value = 6, message = "the course number cannot be more than 6")
    private Integer course;

    @Column(name = "status", length = 20)
    private String status = "draft";

    @PreUpdate
    public void preUpdate() {
        this.lastModified = LocalDateTime.now();
    }
    
}
