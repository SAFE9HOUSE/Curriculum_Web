package com.example.demo.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Interfaces.IStudyPlanServise;
import com.example.demo.dto.DisciplineDto;
import com.example.demo.dto.FieldOfStudyBriefDto;
import com.example.demo.dto.StudyPlansDisciplinesResponseDto;
import com.example.demo.dto.TeacherInDisciplineDto;
import com.example.demo.exceptions.StudyPlanNotFoundException;


@Service
@Transactional(readOnly = true)
public class StudyPlansService implements IStudyPlanServise{

    private final JdbcTemplate _jdbcTemplate;

    public StudyPlansService(JdbcTemplate jdbcTemplate){
        _jdbcTemplate = jdbcTemplate;
    }
     
    @Override
    public StudyPlansDisciplinesResponseDto getDisciplinesByStudyPlanId(Long studyPlanId) {
        String sql = """
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

        List<Map<String, Object>> rows = _jdbcTemplate.queryForList(sql, studyPlanId);

        if (rows.isEmpty()) {
            throw new StudyPlanNotFoundException("Учебный план с ID " + studyPlanId + " не найден");
        }

        Map<String, Object> firstRow = rows.get(0);
        
        FieldOfStudyBriefDto fieldDto = new FieldOfStudyBriefDto(
            (Long) firstRow.get("field_id"),
            (String) firstRow.get("field_code"),
            (String) firstRow.get("field_name"),
            (String) firstRow.get("degree_level"),
            (Integer) firstRow.get("study_length"),
            (String) firstRow.get("profile_name")
        );

        // Используем составной ключ (дисциплина + семестр)
        Map<String, DisciplineDtoBuilder> disciplineBuilders = new HashMap<>();
        
        for (Map<String, Object> row : rows) {
            Long discId = (Long) row.get("discipline_id");
            Integer semester = (Integer) row.get("semester");
            
            // Уникальный ключ для разделения дисциплин по семестрам
            String key = discId + "_" + semester;  
            
            disciplineBuilders.computeIfAbsent(key, k -> new DisciplineDtoBuilder(row));
            disciplineBuilders.get(key).addTeacher(row);
        }

        // Сортируем по семестрам и названиям дисциплин
        List<DisciplineDto> disciplines = disciplineBuilders.values().stream()
            .map(DisciplineDtoBuilder::build)
            .sorted(Comparator.comparingInt(DisciplineDto::getTerm)
                    .thenComparing(DisciplineDto::getDisciplineName))
            .collect(Collectors.toList());

        return new StudyPlansDisciplinesResponseDto(
            (Long) firstRow.get("curriculum_id"),
            (String) firstRow.get("curriculum_name"),
            (Integer) firstRow.get("course"),
            (Integer) firstRow.get("year_start"),
            (Integer) firstRow.get("year_end"),
            (Boolean) firstRow.get("archive_status"),
            (String) firstRow.get("file_path"),
            (String) firstRow.get("status"),
            fieldDto,
            disciplines
        );
    }

    private static class DisciplineDtoBuilder {
        private final Long disciplineId;
        private final String disciplineCode;
        private final String disciplineName;
        private final Integer term;
        private final Integer totalHours;
        private final Integer independentHours;
        private final String report;
        private final String description;
        private final String competences;
        
        // Используем Map для дедупликации преподавателей
        private final Map<String, TeacherInDisciplineDto> teachers = new LinkedHashMap<>();

        public DisciplineDtoBuilder(Map<String, Object> row) {
            
            this.disciplineId = (Long) row.get("discipline_id");
            this.disciplineCode = (String) row.get("discipline_code");
            this.disciplineName = (String) row.get("discipline_name");
            this.term = (Integer) row.get("semester");  
            this.totalHours = (Integer) row.get("total_hours");
            this.independentHours = (Integer) row.get("hours_indepndent");
            this.report = (String) row.get("report");
            this.description = (String) row.get("description");
            this.competences = (String) row.get("competences");
        }

        public void addTeacher(Map<String, Object> row) {
            if (row.get("teacher_id") == null) return;
            
            Long teacherId = (Long) row.get("teacher_id");
            String role = (String) row.get("role");
            
            // Уникальный ключ: преподаватель + роль
            String dedupeKey = teacherId + "_" + role;
            
            // Пропускаем дубликаты
            if (teachers.containsKey(dedupeKey)) return;
            
            Integer hours = Optional.ofNullable((Integer) row.get("hours_assigned")).orElse(0);
            
            teachers.put(dedupeKey, new TeacherInDisciplineDto(
                teacherId,
                (String) row.get("fio"),
                role,
                hours,
                (String) row.get("department"),
                (String) row.get("post"),
                (String) row.get("academic_status"),
                (String) row.get("academic_degree"),
                (String) row.get("email"),
                (String) row.get("phone"),
                (String) row.get("information") 
            ));
        }

        public DisciplineDto build() {
            return new DisciplineDto(
                disciplineId,
                disciplineCode,
                disciplineName,
                term,
                totalHours,
                independentHours,
                report,
                description,
                competences,
                new ArrayList<>(teachers.values())  
            );
        }
    }
}