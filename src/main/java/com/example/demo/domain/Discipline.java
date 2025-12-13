package com.example.demo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    @Column(name = "report", nullable = false, length = 100)
    private String report;

    @Column(name = "description")
    private String description;

    @Column(name = "competences")
    private String competences;
}
