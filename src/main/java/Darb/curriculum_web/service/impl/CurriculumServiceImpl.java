package Darb.curriculum_web.service.impl;

import Darb.curriculum_web.dto.*;
import Darb.curriculum_web.exceptions.CurriculumNotFoundException;
import Darb.curriculum_web.service.CurriculumService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CurriculumServiceImpl implements CurriculumService {

    private final JdbcTemplate jdbcTemplate;

    public CurriculumServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public CurriculumDisciplinesResponseDto getDisciplinesByCurriculumId(Long curriculumId) {
        String sql = """
            SELECT 
                c.curriculum_id,
                c.curriculum_name,
                c.course,
                f.field_id,
                f.field_code,
                f.field_name,
                f.degree_level,
                d.discipline_id,
                d.discipline_code,
                d.discipline_name,
                sdc.term AS semester,
                sdc.term_hours AS total_hours,  -- ← важно: точное написание
                t.teacher_id,
                t.fio,
                sdt.teacher_role AS role,      -- ← точное написание
                sdt.hours_assigned
            FROM curriculum c
            INNER JOIN field_of_study f ON c.field_id = f.field_id
            INNER JOIN subsidiary_discip_curriculum sdc ON c.curriculum_id = sdc.curriculum_id
            INNER JOIN discipline d ON sdc.discipline_id = d.discipline_id
            LEFT JOIN subsidiary_discip_teacher sdt ON sdc.subsidiary_discip_curriculum_id = sdt.subsidiary_discip_curriculum_id
            LEFT JOIN teacher t ON sdt.teacher_id = t.teacher_id
            WHERE c.curriculum_id = ?
            ORDER BY sdc.term, d.discipline_name, t.fio
            """;

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, curriculumId);

        if (rows.isEmpty()) {
            throw new CurriculumNotFoundException("Учебный план с ID " + curriculumId + " не найден");
        }

        Map<String, Object> firstRow = rows.get(0);
        
        FieldOfStudyBriefDto fieldDto = new FieldOfStudyBriefDto(
            (Long) firstRow.get("field_id"),
            (String) firstRow.get("field_code"),
            (String) firstRow.get("field_name"),
            (String) firstRow.get("degree_level")
        );

        Map<Long, DisciplineDtoBuilder> disciplineBuilders = new HashMap<>();
        
        for (Map<String, Object> row : rows) {
            Long discId = (Long) row.get("discipline_id");
            disciplineBuilders.computeIfAbsent(discId, k -> new DisciplineDtoBuilder(row));
            disciplineBuilders.get(discId).addTeacher(row);
        }

        List<DisciplineDto> disciplines = disciplineBuilders.values().stream()
            .map(DisciplineDtoBuilder::build)
            .collect(Collectors.toList());

        return new CurriculumDisciplinesResponseDto(
            (Long) firstRow.get("curriculum_id"),
            (String) firstRow.get("curriculum_name"),
            (Integer) firstRow.get("course"),
            fieldDto,
            disciplines
        );
    }

    private static class DisciplineDtoBuilder {
        private final Long disciplineId;
        private final String disciplineCode;
        private final String disciplineName;
        private final Integer semester;
        private final Integer totalHours;
        private final List<TeacherInDisciplineDto> teachers = new ArrayList<>();

        public DisciplineDtoBuilder(Map<String, Object> row) {
            this.disciplineId = (Long) row.get("discipline_id");
            this.disciplineCode = (String) row.get("discipline_code");
            this.disciplineName = (String) row.get("discipline_name");
            this.semester = (Integer) row.get("semester");
            // Важно: точно как в SQL-алиасе!
            this.totalHours = (Integer) row.get("total_hours"); 
        }

        public void addTeacher(Map<String, Object> row) {
            // Проверяем существование teacher_id
            if (row.get("teacher_id") != null) {
                // Безопасное извлечение часов
                Integer hoursAssigned = (Integer) row.get("hours_assigned");
                if (hoursAssigned == null) hoursAssigned = 0;
                
                this.teachers.add(new TeacherInDisciplineDto(
                    (Long) row.get("teacher_id"),
                    (String) row.get("fio"),
                    (String) row.get("role"), // ← точно как в SQL!
                    hoursAssigned
                ));
            }
        }

        public DisciplineDto build() {
            return new DisciplineDto(
                disciplineId,
                disciplineCode,
                disciplineName,
                semester,
                totalHours,
                teachers
            );
        }
    }
}