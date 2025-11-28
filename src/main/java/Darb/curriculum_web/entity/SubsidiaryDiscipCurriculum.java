package Darb.curriculum_web.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "subsidiary_discip_curriculum")
@Data
public class SubsidiaryDiscipCurriculum {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subsidiary_discip_curriculum_id")
    private Long subsidiaryDiscipCurriculumId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discipline_id", nullable = false) 
    private Discipline discipline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curriculum_id", nullable = false) 
    private Curriculum curriculum;

    @Column(name = "term", nullable = false)
    private Integer term;

    @Column(name = "term_hours")
    private Integer termHours;

}
