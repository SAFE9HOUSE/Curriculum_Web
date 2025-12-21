package com.example.demo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "curriculum_files")
@Data
public class StudyPlanFiles {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long fileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curriculum_id", nullable = false) 
    private StudyPlan studyPlan;
    
    @jakarta.validation.constraints.NotNull(message = "Название файла обязательно")
    @Size(max = 200, message = "Название файла не должно превышать 200 символов")
    @Column(name = "file_name", nullable = false, length = 200)
    private String fileName;
    
    @jakarta.validation.constraints.NotNull(message = "Указание пути файла обязательно")
    @Size(max = 300, message = "Путь файла не должен превышать 300 символов")
    @Column(name = "file_path", nullable = false, length = 300)
    private String filePath;
    
    @Size(max = 50, message = "Тип файла не должен превышать 50 символов")
    @Column(name = "file_type", length = 50)
    private String fileType;
    
}
