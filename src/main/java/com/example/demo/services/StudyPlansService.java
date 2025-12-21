package com.example.demo.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.Interfaces.IStudyPlanServise;
import com.example.demo.domain.FieldOfStudy;
import com.example.demo.domain.StudyPlan;
import com.example.demo.dto.StudyPlansDisciplinesResponseDto;
import com.example.demo.exceptions.ArchiveStudyPlanException;
import com.example.demo.exceptions.DuplicateResourceException;
import com.example.demo.exceptions.FieldNotFoundException;
import com.example.demo.exceptions.StudyPlanNotFoundException;
import com.example.demo.dto.repository.StudyPlanCustomRepository;
import com.example.demo.mapper.StudyPlanMapper;

import lombok.extern.slf4j.Slf4j;

import com.example.demo.dto.repository.FieldOfStudyRepository;
import com.example.demo.dto.repository.StudyPlanRepository;
import com.example.demo.dto.repository.SubsidiaryDiscipStudyPlanRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.exceptions.ValidationException;

@Service
@Slf4j
@Transactional(readOnly = true)
public class StudyPlansService implements IStudyPlanServise {

    @Autowired
    private StudyPlanCustomRepository repositoryRepo;
    
    @Autowired
    private StudyPlanMapper mapperRepo;
    
    @Autowired
    private StudyPlanRepository studyPlanRepositoryRepo;
    
    @Autowired
    private FieldOfStudyRepository fieldOfStudyRepositoryRepo;
    
    @Autowired
    private SubsidiaryDiscipStudyPlanRepository sdsRepository;
    
    @Override
    public StudyPlansDisciplinesResponseDto getDisciplinesByStudyPlanId(Long studyPlanId) {
        
        List<Map<String, Object>> rows = repositoryRepo.findDisciplinesByStudyPlanId(studyPlanId);
        
        if (rows.isEmpty()) {
            throw new StudyPlanNotFoundException("Учебный план с ID " + studyPlanId + " не найден");
        }
        
        return mapperRepo.mapToStudyPlanDisciplinesResponse(rows);
    }

    // создание учебного плана
    @SuppressWarnings("null")
    @Transactional
    public StudyPlan createStudyPlan(Long fieldId, StudyPlan studyPlan) {
        
        List<String> errors = new ArrayList<>();
        
        FieldOfStudy field = fieldOfStudyRepositoryRepo.findById(fieldId)
            .orElseThrow(() -> new FieldNotFoundException(
                "Направление с ID " + fieldId + " не найдено"));
        
        if (studyPlan.getStudyPlanName() == null || studyPlan.getStudyPlanName().trim().isEmpty()) {
            errors.add("Название учебного плана обязательно");
        }
        
        if (studyPlan.getYearStart() == null) {
            errors.add("Год начала действия обязателен");
        }
        
        if (studyPlan.getYearEnd() == null) {
            errors.add("Год окончания действия обязателен");
        }
        
        if (studyPlan.getCourse() == null) {
            errors.add("Курс обязателен");
        }
    
        if (studyPlan.getYearStart() != null && studyPlan.getYearEnd() != null) {
            if (studyPlan.getYearEnd() <= studyPlan.getYearStart()) {
                errors.add("Год окончания должен быть больше года начала");
            }
        }
        
        if (studyPlan.getStudyPlanId() != null) {
            errors.add("ID учебного плана задается системой автоматически");
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
        
        // защита от архивации в момент создания учебного плана
        studyPlan.setArchiveStatus(false);
        
        // Определяем фактический статус (с учетом значения по умолчанию)
        if (studyPlan.getStatus() == null || studyPlan.getStatus().trim().isEmpty()) {
            studyPlan.setStatus("1.00");
        }
    
        // проверка уникальности учебного плана для данного направления по определенным параметрам
        if (studyPlanRepositoryRepo.existsByFieldAndParams(fieldId, studyPlan.getStudyPlanName(),
        studyPlan.getYearStart(), studyPlan.getYearEnd(), studyPlan.getCourse(), studyPlan.getStatus())) {
        
            throw new DuplicateResourceException(
                String.format(
                    "Учебный план с такими параметрами уже существует: " +
                    "название='%s', период=%d-%d, курс=%d, версия='%s'",
                    studyPlan.getStudyPlanName(),
                    studyPlan.getYearStart(),
                    studyPlan.getYearEnd(),
                    studyPlan.getCourse(),
                    studyPlan.getStatus()
                )
            );
        }
        
        studyPlan.setField(field);
        
        return studyPlanRepositoryRepo.save(studyPlan);
    }
    
    // архивация учебного плана
    @Transactional
    @SuppressWarnings("null")
    public StudyPlan toggleArchiveStatus(Long fieldId, Long studyPlanId) {

        if (!studyPlanRepositoryRepo.existsById(studyPlanId)) {
            throw new StudyPlanNotFoundException(
                "Учебный план с ID " + studyPlanId + " не найден");
            }
    
        StudyPlan studyPlan = studyPlanRepositoryRepo
            .findByStudyPlanIdAndField_FieldId(studyPlanId, fieldId)
            .orElseThrow(() -> new StudyPlanNotFoundException(
                String.format(
                    "Учебный план с ID %d не принадлежит направлению ID %d",
                    studyPlanId, fieldId
                )));
    
        boolean newStatus = !Boolean.TRUE.equals(studyPlan.getArchiveStatus());
        studyPlan.setArchiveStatus(newStatus);
    
        String action = newStatus ? "архивирован" : "разархивирован";
        log.info("Учебный план {} {} (ID: {})", 
             studyPlan.getStudyPlanName(), action, studyPlanId);
    
        return studyPlanRepositoryRepo.save(studyPlan);
    }

    // удаление учебного плана
    @Transactional
    @SuppressWarnings("null")
    public void deleteStudyPlan(Long fieldId, Long studyPlanId) {

        if (!studyPlanRepositoryRepo.existsById(studyPlanId)) {
            throw new StudyPlanNotFoundException(
                "Учебный план с ID " + studyPlanId + " не найден");
        }
    
        StudyPlan studyPlan = studyPlanRepositoryRepo
            .findByStudyPlanIdAndField_FieldId(studyPlanId, fieldId)
            .orElseThrow(() -> new StudyPlanNotFoundException(
                String.format(
                    "Учебный план с ID %d не принадлежит направлению ID %d",
                    studyPlanId, fieldId
                )));

        if (Boolean.TRUE.equals(studyPlan.getArchiveStatus())) {
            throw new ArchiveStudyPlanException(
                String.format(
                    "Невозможно удалить учебный план, находящийся в архиве (ID: %d). ",
                    studyPlanId));
        }

        Long disciplineCount = sdsRepository.countByStudyPlan(studyPlan);
    
        if (disciplineCount != null && disciplineCount > 0) {
            throw new ValidationException(
                String.format(
                    "Невозможно удалить учебный план ID %d. " +
                    "Сначала удалите все дисциплины из плана (найдено дисциплин: %d).",
                    studyPlan.getStudyPlanId(), disciplineCount));
    }
    
        studyPlanRepositoryRepo.delete(studyPlan);
    }

    // обновление учебного плана 
    @SuppressWarnings("null")
    @Transactional
    public StudyPlan updateStudyPlan(Long fieldId, Long studyPlanId, StudyPlan updates) {

        if (!studyPlanRepositoryRepo.existsById(studyPlanId)) {
            throw new StudyPlanNotFoundException(
                "Учебный план с ID " + studyPlanId + " не найден");
        }
    
        StudyPlan studyPlan = studyPlanRepositoryRepo
            .findByStudyPlanIdAndField_FieldId(studyPlanId, fieldId)
            .orElseThrow(() -> new StudyPlanNotFoundException(
                String.format(
                    "Учебный план с ID %d не принадлежит направлению ID %d",
                    studyPlanId, fieldId
                )));

        if (Boolean.TRUE.equals(studyPlan.getArchiveStatus())) {
            throw new ArchiveStudyPlanException(
                String.format(
                    "Невозможно изменить учебный план, находящийся в архиве (ID: %d). ",
                    studyPlanId));
        }
        
        if (updates.getStudyPlanName() != null) {
            studyPlan.setStudyPlanName(updates.getStudyPlanName());
        }
    
        if (updates.getYearStart() != null) {
            studyPlan.setYearStart(updates.getYearStart());
        }
    
        if (updates.getYearEnd() != null) {
            studyPlan.setYearEnd(updates.getYearEnd());
        }
    
        if (updates.getFilePath() != null) {
            studyPlan.setFilePath(updates.getFilePath());
        }
    
        if (updates.getCourse() != null) {
            studyPlan.setCourse(updates.getCourse());
        }
    
        if (updates.getStatus() != null) {
            studyPlan.setStatus(updates.getStatus());
        }
        
        // защита от пустого json (блокировка от самого себя)
        boolean keyFieldsProvided = 
            (updates.getStudyPlanName() != null) ||
            (updates.getYearStart() != null) ||
            (updates.getYearEnd() != null) ||
            (updates.getCourse() != null) ||
            (updates.getStatus() != null);
        
        if (keyFieldsProvided){
            if (studyPlanRepositoryRepo.existsByFieldAndParamsExcludingSelf(fieldId, studyPlan.getStudyPlanName(),
            studyPlan.getYearStart(), studyPlan.getYearEnd(), studyPlan.getCourse(),
            studyPlan.getStatus(), studyPlanId)) {
        
                throw new DuplicateResourceException(
                    String.format(
                        "Учебный план с такими параметрами уже существует: " +
                        "название='%s', период=%d-%d, курс=%d, версия='%s'",
                        studyPlan.getStudyPlanName(),
                        studyPlan.getYearStart(),
                        studyPlan.getYearEnd(),
                        studyPlan.getCourse(),
                        studyPlan.getStatus()
                    )
                );
            }
        }

        return studyPlanRepositoryRepo.save(studyPlan);
    }
}