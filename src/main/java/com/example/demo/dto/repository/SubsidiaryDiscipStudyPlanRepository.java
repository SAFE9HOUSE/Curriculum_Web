package com.example.demo.dto.repository;

import com.example.demo.domain.SubsidiaryDiscipStudyPlan;
import com.example.demo.domain.StudyPlan;
import com.example.demo.domain.Discipline;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SubsidiaryDiscipStudyPlanRepository 
    extends JpaRepository<SubsidiaryDiscipStudyPlan, Long> {
    
    // проверка связи для создания начинки
    @Query("SELECT COUNT(sds) > 0 FROM SubsidiaryDiscipStudyPlan sds " +
           "WHERE sds.studyPlan = :studyPlan " +
           "AND sds.discipline = :discipline " +
           "AND sds.term = :term")
    boolean existsByStudyPlanAndDisciplineAndTerm(
        @Param("studyPlan") StudyPlan studyPlan,
        @Param("discipline") Discipline discipline,
        @Param("term") Integer term);
    
    // проверка связи для удаления начинки
    @Query("SELECT sds FROM SubsidiaryDiscipStudyPlan sds " +
           "WHERE sds.studyPlan = :studyPlan " +
           "AND sds.discipline = :discipline " +
           "AND sds.term = :term")
    Optional<SubsidiaryDiscipStudyPlan> findByStudyPlanAndDisciplineAndTerm(
        @Param("studyPlan") StudyPlan studyPlan,
        @Param("discipline") Discipline discipline,
        @Param("term") Integer term);

    // Метод для подсчета дисциплин в учебном плане
    @Query("SELECT COUNT(sds) FROM SubsidiaryDiscipStudyPlan sds " +
           "WHERE sds.studyPlan = :studyPlan")
    Long countByStudyPlan(@Param("studyPlan") StudyPlan studyPlan);
}