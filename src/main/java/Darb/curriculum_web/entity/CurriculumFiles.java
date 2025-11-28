package Darb.curriculum_web.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "curriculum_files")
@Data
public class CurriculumFiles {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long fileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curriculum_id", nullable = false) 
    private Curriculum curriculum;

    @Column(name = "file_name", nullable = false, length = 200)
    private String fileName;

    @Column(name = "file_path", nullable = false, length = 300)
    private String filePath;

    @Column(name = "file_type", length = 50)
    private String fileType;

}
