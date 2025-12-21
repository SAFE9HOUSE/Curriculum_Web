package com.example.demo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "teacher")
@Data
public class Teacher {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "teacher_id")
    private Long teacherId;
    
    @jakarta.validation.constraints.NotNull(message = "ФИО преподавателя обязательно")
    @Size(max = 300, message = "ФИО преподавателя не должно превышать 300 символов")
    @Column(name = "fio", nullable = false, length = 300)
    private String fio;
    
    @jakarta.validation.constraints.NotNull(message = "Указание кафедры преподавателя обязательно")
    @Size(max = 200, message = "Указание кафедры преподавателя не должно превышать 200 символов")
    @Column(name = "department", nullable = false, length = 200)
    private String department;
    
    @Size(max = 200, message = "Пост преподавателя не должен превышать 200 символов")
    @Column(name = "post", length = 200)
    private String post;
    
    @Size(max = 100, message = "Академический статус преподавателя не должен превышать 100 символов")
    @Column(name = "academic_status", length = 100)
    private String academicStatus;
    
    @Size(max = 100, message = "Академическая степень преподавателя не должна превышать 100 символов")
    @Column(name = "academic_degree", length = 100)
    private String academicDegree;
    
    @Size(max = 255, message = "EMAIL преподавателя не должен превышать 255 символов")
    @Column(name = "email", length = 255)
    private String email;
    
    @Size(max = 20, message = "Номер телефона преподавателя не должен превышать 20 символов")
    @Column(name = "phone", length = 20)
    private String phone;
    
    @Size(max = 500, message = "Информация о преподавателе не должна превышать 500 символов")
    @Column(name = "information", length = 500)
    private String information;
    
}
