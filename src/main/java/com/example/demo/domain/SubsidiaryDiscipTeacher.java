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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
    @jakarta.validation.constraints.NotNull(message = "Указание связи с дисциплиной и учебным планом обязательно")
    private SubsidiaryDiscipStudyPlan subsidiaryDiscipStudyPlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false) 
    @jakarta.validation.constraints.NotNull(message = "Указание связи с преподавателем обязательно")
    private Teacher teacher;

    @Size(max = 50, message = "Роль преподавателя не может быть больше 50 символов")
    @Column(name = "teacher_role", length = 50)
    private String teacherRole = "lecturer";
    
    @Column(name = "hours_assigned")
    @Min(value = 0, message = "Назначенные часы не могут быть отрицательными")
    @Max(value = 200, message = "Максимальное назначенных часов на дисциплину - 200")
    private Integer hoursAssigned;

}
