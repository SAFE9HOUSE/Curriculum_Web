package com.example.demo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@Entity
@Table(name = "discipline")
@Data
public class Discipline {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "discipline_id")
    private Long disciplineId;
    
    @Size(max = 50, message = "Код дисциплины не должен превышать 50 символов")
    @Column(name = "discipline_code", length = 50)
    private String disciplineCode;
    
    @jakarta.validation.constraints.NotNull(message = "Название дисциплины обязательно")
    @Size(max = 200, message = "Название дисциплины не должно превышать 200 символов")
    @Column(name = "discipline_name", nullable = false, length = 200)
    private String disciplineName;
    
    @jakarta.validation.constraints.NotNull(message = "Общее количество часов обязательно")
    @Min(value = 0, message = "Общее кол-во часов дисциплины не может быть отрицательными")
    @Max(value = 400, message = "Общее кол-во часов дисциплины не может превышать 400 часов")
    @Column(name = "hours_total", nullable = false)
    private Integer hoursTotal;
    
    @Min(value = 0, message = "Независимое кол-во часов не может быть отрицательными")
    @Max(value = 20, message = "Независимое кол-во часов дисциплины не может превышать 20 часов")
    @Column(name = "hours_independent")
    private Integer hoursIndependent;
    
    @jakarta.validation.constraints.NotNull(message = "Форма контроля обязательна")
    @Size(max = 100, message = "Форма контроля дисциплины не должен превышать 100 символов")
    @Column(name = "report", nullable = false, length = 100)
    private String report;
    
    @Size(max = 300, message = "Описание дисциплины не должно превышать 300 символов")
    @Column(name = "description", length = 300)
    private String description;
    
    @Size(max = 300, message = "Описание компетенций дисциплины не должно превышать 300 символов")
    @Column(name = "competences", length = 300)
    private String competences;
    
}
