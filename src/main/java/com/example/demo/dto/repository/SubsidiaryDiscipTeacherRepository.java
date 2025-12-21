package com.example.demo.dto.repository;

import com.example.demo.domain.SubsidiaryDiscipTeacher;
import com.example.demo.domain.SubsidiaryDiscipStudyPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SubsidiaryDiscipTeacherRepository 
    extends JpaRepository<SubsidiaryDiscipTeacher, Long> {
    
    @Query("SELECT COUNT(sdt) > 0 FROM SubsidiaryDiscipTeacher sdt " +
           "WHERE sdt.subsidiaryDiscipStudyPlan = :sds " +
           "AND sdt.teacher.teacherId = :teacherId " +
           "AND sdt.teacherRole = :teacherRole")
    boolean existsBySubsidiaryDiscipStudyPlanAndTeacherAndTeacherRole(
        @Param("sds") SubsidiaryDiscipStudyPlan sds,
        @Param("teacherId") Long teacherId,
        @Param("teacherRole") String teacherRole);
}