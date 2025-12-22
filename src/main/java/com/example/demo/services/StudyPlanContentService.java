package com.example.demo.services;

import com.example.demo.domain.*;
import com.example.demo.dto.StudyPlanContentDeleteRequest;
import com.example.demo.dto.StudyPlanContentRequest;
import com.example.demo.dto.StudyPlanContentRequest.TeacherAssignmentDto;
import com.example.demo.dto.StudyPlanContentUpdateRequest;
import com.example.demo.dto.repository.*;
import com.example.demo.exceptions.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyPlanContentService {
    
    private final StudyPlanRepository studyPlanRepository;
    private final DisciplineRepository disciplineRepository;
    private final TeacherRepository teacherRepository;
    private final SubsidiaryDiscipStudyPlanRepository sdsRepository;
    private final SubsidiaryDiscipTeacherRepository sdtRepository;
    
    // добавление контена в учебный план (сначала добавляем дисциплины)
    @Transactional
    public void addContentToPlan(Long fieldId, Long planId, 
                                StudyPlanContentRequest request) {
        
        StudyPlan studyPlan = validateStudyPlan(fieldId, planId);
        List<String> errors = new ArrayList<>();
        
        for (StudyPlanContentRequest.DisciplineTermBlockDto block : request.getBlocks()) {
            try {
                processDisciplineBlock(studyPlan, block, errors);
            } catch (Exception e) {
                errors.add("Ошибка при обработке блока: " + e.getMessage());
            }
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
        
        log.info("Добавлено {} блоков в учебный план ID: {} (направление ID: {})",
            request.getBlocks().size(), planId, fieldId);
    }
    
    private StudyPlan validateStudyPlan(Long fieldId, Long planId) {
        StudyPlan studyPlan = studyPlanRepository
            .findByStudyPlanIdAndField_FieldId(planId, fieldId)
            .orElseThrow(() -> new StudyPlanNotFoundException(
                String.format("Учебный план ID %d не найден в направлении ID %d", 
                    planId, fieldId)));
        
        if (Boolean.TRUE.equals(studyPlan.getArchiveStatus())) {
            throw new ArchiveStudyPlanException(
                "Невозможно изменять архивный учебный план");
        }
        
        return studyPlan;
    }
    
    @SuppressWarnings("null")
    private void processDisciplineBlock(StudyPlan studyPlan,
                                       StudyPlanContentRequest.DisciplineTermBlockDto block,
                                       List<String> errors) {
        Discipline discipline = disciplineRepository.findById(block.getDisciplineId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Дисциплина с ID " + block.getDisciplineId() + " не найдена"));
        
        if (sdsRepository.existsByStudyPlanAndDisciplineAndTerm(
                studyPlan, discipline, block.getTerm())) {
            errors.add(String.format(
                "Дисциплина '%s' уже добавлена в %d семестре учебного плана",
                discipline.getDisciplineName(), block.getTerm()));
            return;
        }
        
        SubsidiaryDiscipStudyPlan sds = new SubsidiaryDiscipStudyPlan();
        sds.setStudyPlan(studyPlan);
        sds.setDiscipline(discipline);
        sds.setTerm(block.getTerm());
        sds.setTermHours(block.getTermHours());
        
        SubsidiaryDiscipStudyPlan savedSds = sdsRepository.save(sds);
        
        if (block.getTeachers() != null && !block.getTeachers().isEmpty()) {
            addTeachersToBlock(savedSds, discipline, block.getTeachers(), errors);
        }
    }
    
    // здесь уже добавляем преподавателей к дисциплинам
    @SuppressWarnings("null")
    private void addTeachersToBlock(SubsidiaryDiscipStudyPlan sds,
                               Discipline discipline,
                               List<StudyPlanContentRequest.TeacherAssignmentDto> teachers,
                               List<String> errors) {
        for (StudyPlanContentRequest.TeacherAssignmentDto teacherDto : teachers) {
            try {
                    Teacher teacher = teacherRepository.findById(teacherDto.getTeacherId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                            "Преподаватель с ID " + teacherDto.getTeacherId() + " не найден"));
            
                    if (teacherDto.getTeacherRole() != null &&
                        sdtRepository.existsBySubsidiaryDiscipStudyPlanAndTeacherAndTeacherRole(
                        sds, teacher.getTeacherId(), teacherDto.getTeacherRole())) {
                            errors.add(String.format(
                            "Преподаватель %s уже назначен на дисциплину '%s' в %d семестре в роли '%s'",
                            teacher.getFio(), discipline.getDisciplineName(), 
                            sds.getTerm(), teacherDto.getTeacherRole()));
                            continue;
                    }
            
                    SubsidiaryDiscipTeacher sdt = new SubsidiaryDiscipTeacher();
                    sdt.setSubsidiaryDiscipStudyPlan(sds);
                    sdt.setTeacher(teacher);
                    sdt.setTeacherRole(
                        teacherDto.getTeacherRole() != null ? 
                        teacherDto.getTeacherRole() : "lecturer");
                    sdt.setHoursAssigned(
                        teacherDto.getHoursAssigned() != null ? 
                        teacherDto.getHoursAssigned() : 0);
            
                    sdtRepository.save(sdt);
            
                } 
            catch (Exception e) {
                errors.add("Ошибка при добавлении преподавателя: " + e.getMessage());
            }
         }
    }
    
    // удаление контента учебного плана
    @Transactional
    public void removeContentFromPlan(Long fieldId, Long planId, 
                                 StudyPlanContentDeleteRequest request) {
    
        StudyPlan studyPlan = validateStudyPlan(fieldId, planId);
        List<String> errors = new ArrayList<>();
    
        for (StudyPlanContentDeleteRequest.DisciplineTermDeleteDto dto : request.getBlocks()) {
            try {
            removeDisciplineBlock(studyPlan, dto, errors);
            } 
            catch (Exception e) {
            errors.add("Ошибка при удалении: " + e.getMessage());
            }
        }
    
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    
        log.info("Удалено {} блоков из учебного плана ID: {}", 
            request.getBlocks().size(), planId);
    }

    // удаление дисциплины (все преподаватели дисциплины тоже удаляются)
    @SuppressWarnings("null")
    private void removeDisciplineBlock(StudyPlan studyPlan,
                                  StudyPlanContentDeleteRequest.DisciplineTermDeleteDto dto,
                                  List<String> errors) {
        
        if (dto.getTerm() == null) {
            errors.add("Не указан номер семестра");
            return;
        }
    
        if (dto.getTerm() != 1 && dto.getTerm() != 2) {
            errors.add(String.format("Семестр должен быть 1 или 2, получено: %d", dto.getTerm()));
            return;
        }

        // находим дисциплину
        Discipline discipline = disciplineRepository.findById(dto.getDisciplineId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Дисциплина с ID " + dto.getDisciplineId() + " не найдена"));
    
        // находим связь 
        SubsidiaryDiscipStudyPlan sds = sdsRepository
            .findByStudyPlanAndDisciplineAndTerm(studyPlan, discipline, dto.getTerm())
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format("Дисциплина '%s' не найдена в %d семестре учебного плана",
                    discipline.getDisciplineName(), dto.getTerm())));
    
        // (каскадно удалятся все преподаватели)
        sdsRepository.delete(sds);
    
        log.debug("Удалена дисциплина '{}' из {} семестра (ID связи: {})",
            discipline.getDisciplineName(), dto.getTerm(), sds.getSubsidiaryDiscipStudyPlanId());
    }
   
    // обновление контента учебного плана (частичное обновление)
    @Transactional
    public void updateContentInPlan(Long fieldId, Long planId,
                               StudyPlanContentUpdateRequest request) {
    
        StudyPlan studyPlan = validateStudyPlan(fieldId, planId);
        List<String> errors = new ArrayList<>();
    
        for (StudyPlanContentUpdateRequest.DisciplineUpdateDto dto : request.getUpdates()) {
            try {
                updateDisciplineInPlan(studyPlan, dto, errors);
            } 
            catch (Exception e) {
                errors.add("Ошибка обновления: " + e.getMessage());
            }
        }
    
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    
        log.info("Обновлено {} дисциплин в учебном плане ID: {}", 
            request.getUpdates().size(), planId);
    }

    @SuppressWarnings("null")
    private void updateDisciplineInPlan(StudyPlan studyPlan,
                                  StudyPlanContentUpdateRequest.DisciplineUpdateDto dto,
                                  List<String> errors) {
        
        if (dto.getCurrentTerm() == null || dto.getNewTerm() == null) {
            errors.add("Семестр не может быть null");
            return;
        }
    
        if (dto.getCurrentTerm() != 1 && dto.getCurrentTerm() != 2) {
            errors.add(String.format("Текущий семестр должен быть 1 или 2, получено: %d", 
                dto.getCurrentTerm()));
            return;
        }

        if (dto.getNewTerm() != 1 && dto.getNewTerm() != 2) {
            errors.add(String.format("Новый семестр должен быть 1 или 2, получено: %d", 
                dto.getNewTerm()));
            return;
        }
    
        // находим дисциплину
        Discipline discipline = disciplineRepository.findById(dto.getDisciplineId())
        .orElseThrow(() -> new ResourceNotFoundException(
            "Дисциплина с ID " + dto.getDisciplineId() + " не найдена"));
    
        // находим текущую связь (по старому семестру)
        SubsidiaryDiscipStudyPlan sds = sdsRepository
            .findByStudyPlanAndDisciplineAndTerm(studyPlan, discipline, dto.getCurrentTerm())
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format("Дисциплина '%s' не найдена в %d семестре",
                    discipline.getDisciplineName(), dto.getCurrentTerm())));
    
        // проверяем конфликты если меняем семестр
        if (!dto.getCurrentTerm().equals(dto.getNewTerm())) {
            boolean existsInNewTerm = sdsRepository.existsByStudyPlanAndDisciplineAndTerm(
                studyPlan, discipline, dto.getNewTerm());
        
            if (existsInNewTerm) {
                errors.add(String.format(
                    "Дисциплина '%s' уже есть в %d семестре",
                    discipline.getDisciplineName(), dto.getNewTerm()));
                return;
            }
        }
    
        // обновляем семестр и часы
        sds.setTerm(dto.getNewTerm());
    
        if (dto.getNewTermHours() != null) {
            sds.setTermHours(dto.getNewTermHours());
        }
    
        // обновляем преподавателей (полная замена)
        updateTeachersForDiscipline(sds, dto.getNewTeachers(), errors);
    
        sdsRepository.save(sds);
    
        log.debug("Обновлена дисциплина '{}': семестр {}→{}, часов {}",
            discipline.getDisciplineName(), 
            dto.getCurrentTerm(), dto.getNewTerm(),
            dto.getNewTermHours());
    }

    @SuppressWarnings("null")
    private void updateTeachersForDiscipline(SubsidiaryDiscipStudyPlan sds,
                                       List<TeacherAssignmentDto> newTeachers,
                                       List<String> errors) {
    
        // удаляем всех старых преподавателей
        if (sds.getTeachers() != null && !sds.getTeachers().isEmpty()) {
            sdtRepository.deleteAll(sds.getTeachers());
            sds.getTeachers().clear();
        }
    
        // добавляем новых
        if (newTeachers != null) {
            for (TeacherAssignmentDto teacherDto : newTeachers) {
                try {
                    Teacher teacher = teacherRepository.findById(teacherDto.getTeacherId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                            "Преподаватель ID " + teacherDto.getTeacherId() + " не найден"));
                
                    SubsidiaryDiscipTeacher sdt = new SubsidiaryDiscipTeacher();
                    sdt.setSubsidiaryDiscipStudyPlan(sds);
                    sdt.setTeacher(teacher);
                    sdt.setTeacherRole(teacherDto.getTeacherRole() != null ? 
                        teacherDto.getTeacherRole() : "lecturer");
                    sdt.setHoursAssigned(teacherDto.getHoursAssigned() != null ? 
                        teacherDto.getHoursAssigned() : 0);
                
                    sdtRepository.save(sdt);
                
                } 
                catch (Exception e) {
                    errors.add("Ошибка с преподавателем ID " + 
                        teacherDto.getTeacherId() + ": " + e.getMessage());
                }
            }
        }
    }
}