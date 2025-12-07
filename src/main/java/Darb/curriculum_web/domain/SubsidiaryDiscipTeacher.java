package Darb.curriculum_web.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "subsidiary_discip_teacher")
@Data
public class SubsidiaryDiscipTeacher {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subsidiary_discip_teacher_id")
    private Long subsidiaryDiscipTeacherId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subsidiary_discip_curriculum_id", nullable = false) 
    private SubsidiaryDiscipCurriculum subsidiaryDiscipCurriculum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false) 
    private Teacher teacher;

    @Column(name = "teacher_role", length = 50)
    private String teacherRole = "lecturer";
    
    @Column(name = "hours_assigned")
    private Integer hoursAssigned;
    
}
