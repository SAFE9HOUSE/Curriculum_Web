package com.example.demo.dto.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.domain.StudyPlan;

@Repository
public interface StudyPlanRepository extends JpaRepository<StudyPlan, Long> {
    
    // найти все учебные планы по ID направления
    List<StudyPlan> findByField_FieldId(Long fieldId);

    // проверка существования учебного плана с такими же признаками для конкретного направления
    @Query("SELECT CASE WHEN COUNT(sp) > 0 THEN true ELSE false END " +
        "FROM StudyPlan sp " +
        "WHERE sp.field.fieldId = :fieldId " +
        "AND sp.studyPlanName = :studyPlanName " +
        "AND sp.yearStart = :yearStart " +
        "AND sp.yearEnd = :yearEnd " +
        "AND sp.course = :course " +
        "AND sp.status = :status")
    boolean existsByFieldAndParams(
        @Param("fieldId") Long fieldId,
        @Param("studyPlanName") String studyPlanName,
        @Param("yearStart") Integer yearStart,
        @Param("yearEnd") Integer yearEnd,
        @Param("course") Integer course,
        @Param("status") String status
    );
    
    // проверка существования учебного плана с такими же признаками для конкретного направления 
    // (исключая себя)
    @Query("SELECT CASE WHEN COUNT(sp) > 0 THEN true ELSE false END " +
       "FROM StudyPlan sp " +
       "WHERE sp.field.fieldId = :fieldId " +
       "AND sp.studyPlanName = :studyPlanName " +
       "AND sp.yearStart = :yearStart " +
       "AND sp.yearEnd = :yearEnd " +
       "AND sp.course = :course " +
       "AND sp.status = :status " +
       "AND sp.studyPlanId != :excludeStudyPlanId")
    boolean existsByFieldAndParamsExcludingSelf(
        @Param("fieldId") Long fieldId,
        @Param("studyPlanName") String studyPlanName,
        @Param("yearStart") Integer yearStart,
        @Param("yearEnd") Integer yearEnd,
        @Param("course") Integer course,
        @Param("status") String status,
        @Param("excludeStudyPlanId") Long excludeStudyPlanId
    );
    
    // проверка принадлежности учебного плана к направлению при архивации (избегание неявных архиваций)
    Optional<StudyPlan> findByStudyPlanIdAndField_FieldId(Long studyPlanId, Long fieldId);
}