package Darb.curriculum_web.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "discipline")
@Data
public class Discipline {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "discipline_id")
    private Long disciplineId;

    @Column(name = "discipline_code", length = 50)
    private String disciplineCode;

    @Column(name = "discipline_name", nullable = false, length = 200)
    private String disciplineName;

    @Column(name = "hours_total", nullable = false)
    private Integer hoursTotal;

    @Column(name = "hours_independent")
    private Integer hoursIndependent;

    @Column(name = "otchet", nullable = false, length = 100)
    private String otchet;

    @Column(name = "description")
    private String description;

    @Column(name = "competences")
    private String competences;

}
