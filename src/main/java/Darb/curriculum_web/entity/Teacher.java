package Darb.curriculum_web.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "teacher")
@Data
public class Teacher {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "teacher_id")
    private Long teacherId;

    @Column(name = "fio", nullable = false, length = 300)
    private String fio;

    @Column(name = "department", nullable = false, length = 200)
    private String department;

    @Column(name = "post", length = 200)
    private String post;

    @Column(name = "academic_status", length = 100)
    private String academicStatus;

    @Column(name = "academic_degree", length = 100)
    private String academicDegree;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "information")
    private String information;
    
}
