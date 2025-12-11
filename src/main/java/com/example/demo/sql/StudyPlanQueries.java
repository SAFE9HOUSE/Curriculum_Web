package com.example.demo.sql;

public class StudyPlanQueries {
    
    public static final String GET_INFO_BY_STUDY_PLAN_ID = """
        SELECT 
            c.curriculum_id,
            c.curriculum_name,
            c.course,
            c.year_start,
            c.year_end,
            c.archive_status,
            c.file_path,
            c.status,
            f.field_id,
            f.field_code,
            f.field_name,
            f.degree_level,
            f.study_length,
            f.profile_name,
            d.discipline_id,
            d.discipline_code,
            d.discipline_name,
            sdc.term AS semester,
            sdc.term_hours AS total_hours,
            t.teacher_id,
            t.fio,
            sdt.teacher_role AS role,
            sdt.hours_assigned
        FROM curriculum c
        INNER JOIN field_of_study f ON c.field_id = f.field_id
        INNER JOIN subsidiary_discip_curriculum sdc ON c.curriculum_id = sdc.curriculum_id
        INNER JOIN discipline d ON sdc.discipline_id = d.discipline_id
        LEFT JOIN subsidiary_discip_teacher sdt 
            ON sdc.subsidiary_discip_curriculum_id = sdt.subsidiary_discip_curriculum_id
        LEFT JOIN teacher t ON sdt.teacher_id = t.teacher_id
        WHERE c.curriculum_id = ?
        ORDER BY sdc.term, d.discipline_name, t.fio
        """;
        
}
