package com.example.demo.dto.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.sql.StudyPlanQueries;

import java.util.List;
import java.util.Map;

@Repository
public class StudyPlanCustomRepository {
    
    private final JdbcTemplate _jdbcTemplate;
    
    public StudyPlanCustomRepository(JdbcTemplate jdbcTemplate) {
        _jdbcTemplate = jdbcTemplate;
    }
    
    public List<Map<String, Object>> findDisciplinesByStudyPlanId(Long studyPlanId) {
        return _jdbcTemplate.queryForList(
            StudyPlanQueries.GET_INFO_BY_STUDY_PLAN_ID, 
            studyPlanId
        );
    }
}
