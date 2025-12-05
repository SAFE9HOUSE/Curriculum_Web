package Darb.curriculum_web.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;
import jakarta.validation.constraints.Min; 
import jakarta.validation.constraints.Max;  

@Entity
@Table(name = "field_of_study")
@Data // ← эта аннотация автоматически создаёт геттеры, сеттеры, toString, equals, hashCode
public class FieldOfStudy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "field_id")
    private Long fieldId;

    @Column(name = "field_code", nullable = false, length = 20)
    private String fieldCode;

    @Column(name = "field_name", nullable = false, length = 200)
    private String fieldName;

    @Column(name = "degree_level", nullable = false, length = 200)
    private String degreeLevel;

    @Column(name = "study_length", nullable = false)
    @Min(value = 1, message = "The value cannot be zero or negative")
    @Max(value = 10, message = "The value cannot be more than 10")
    private Integer studyLength;

    @Column(name = "profile_name", nullable = false, length = 200, unique = true)
    private String profileName;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();
    
}
    

