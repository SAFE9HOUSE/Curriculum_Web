package com.example.demo.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Data;

@Entity
@Table(name = "subsidiary_discip_curriculum")
@Data
public class SubsidiaryDiscipStudyPlan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subsidiary_discip_curriculum_id")
    private Long subsidiaryDiscipStudyPlanId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discipline_id", nullable = false) 
    @jakarta.validation.constraints.NotNull(message = "Указание связи с дисциплиной обязательно")
    private Discipline discipline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curriculum_id", nullable = false) 
    @jakarta.validation.constraints.NotNull(message = "Указание связи с учебным планом обязательно")
    private StudyPlan studyPlan;
    
    @Column(name = "term", nullable = false)
    @jakarta.validation.constraints.NotNull(message = "Указание семестра обязательно")
    @Min(value = 1, message = "Семестр должен быть не менее 1")
    @Max(value = 2, message = "Семестр должен быть не более 2")
    private Integer term;
    
    @Min(value = 0, message = "Часы в семестре не могут быть отрицательными")
    @Max(value = 1000, message = "Максимальное кол-во часов на семестр - 1000")
    @Column(name = "term_hours")
    private Integer termHours;

    @OneToMany(mappedBy = "subsidiaryDiscipStudyPlan", 
               cascade = CascadeType.ALL, 
               orphanRemoval = true)
    private List<SubsidiaryDiscipTeacher> teachers = new ArrayList<>();
    
}
