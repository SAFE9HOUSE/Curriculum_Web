package com.example.demo.mapper;

import com.example.demo.dto.DisciplineDto;
import com.example.demo.dto.FieldOfStudyBriefDto;
import com.example.demo.dto.StudyPlansDisciplinesResponseDto;
import com.example.demo.dto.TeacherInDisciplineDto;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class StudyPlanMapper {
    
    public StudyPlansDisciplinesResponseDto mapToStudyPlanDisciplinesResponse(
            List<Map<String, Object>> rows) {
        
        if (rows.isEmpty()) {
            throw new IllegalArgumentException("Список строк пуст");
        }

        Map<String, Object> firstRow = rows.get(0);
        
        // Маппинг информации об учебном плане и направлении
        FieldOfStudyBriefDto fieldDto = mapToFieldBrief(firstRow);
        
        // Маппинг дисциплин с преподавателями
        List<DisciplineDto> disciplines = mapToDisciplines(rows);
        
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
    
    private FieldOfStudyBriefDto mapToFieldBrief(Map<String, Object> row) {
        return new FieldOfStudyBriefDto(
            (Long) row.get("field_id"),
            (String) row.get("field_code"),
            (String) row.get("field_name"),
            (String) row.get("degree_level"),
            (Integer) row.get("study_length"),
            (String) row.get("profile_name"),
            (String) row.get("qualification")
        );
    }
    
    private List<DisciplineDto> mapToDisciplines(List<Map<String, Object>> rows) {
        // Группируем строки по дисциплине и семестру
        Map<String, List<Map<String, Object>>> groupedRows = rows.stream()
            .collect(Collectors.groupingBy(row -> 
                row.get("discipline_id") + "_" + row.get("term")
            ));
        
        // Преобразуем каждую группу в DisciplineDto
        return groupedRows.values().stream()
            .map(this::mapToDisciplineDto)
            .sorted(Comparator.comparingInt(DisciplineDto::getTerm)
                    .thenComparing(DisciplineDto::getDisciplineName))
            .collect(Collectors.toList());
    }
    
    private DisciplineDto mapToDisciplineDto(List<Map<String, Object>> disciplineRows) {
        // Берём первую строку для основных данных дисциплины
        Map<String, Object> firstRow = disciplineRows.get(0);
        
        // Собираем преподавателей из всех строк этой дисциплины
        List<TeacherInDisciplineDto> teachers = disciplineRows.stream()
            .filter(row -> row.get("teacher_id") != null)
            .map(this::mapToTeacherDto)
            .distinct()  // Убираем дубликаты
            .collect(Collectors.toList());
        
        return new DisciplineDto(
            (Long) firstRow.get("discipline_id"),
            (String) firstRow.get("discipline_code"),
            (String) firstRow.get("discipline_name"),
            (Integer) firstRow.get("term"),
            (Integer) firstRow.get("term_hours"),
            (Integer) firstRow.get("hours_total"),
            (Integer) firstRow.get("hours_indepndent"),
            (String) firstRow.get("report"),
            (String) firstRow.get("description"),
            (String) firstRow.get("competences"),
            teachers
        );
    }
    
    private TeacherInDisciplineDto mapToTeacherDto(Map<String, Object> row) {
        return new TeacherInDisciplineDto(
            (Long) row.get("teacher_id"),
            (String) row.get("fio"),
            (String) row.get("role"),
            Optional.ofNullable((Integer) row.get("hours_assigned")).orElse(0),
            (String) row.get("department"),
            (String) row.get("post"),
            (String) row.get("academic_status"),
            (String) row.get("academic_degree"),
            (String) row.get("email"),
            (String) row.get("phone"),
            (String) row.get("information")
        );
    }
}