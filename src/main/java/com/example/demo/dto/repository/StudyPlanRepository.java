package com.example.demo.dto.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.domain.StudyPlan;

@Repository
public interface StudyPlanRepository extends JpaRepository<StudyPlan, Long> {
    
    // Найти все учебные планы по ID направления
    List<StudyPlan> findByField_FieldId(Long fieldId);
}