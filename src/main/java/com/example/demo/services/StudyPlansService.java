package com.example.demo.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.Interfaces.IStudyPlanServise;
import com.example.demo.domain.FieldOfStudy;
import com.example.demo.domain.StudyPlan;
import com.example.demo.dto.StudyPlansDisciplinesResponseDto;
import com.example.demo.exceptions.DuplicateResourceException;
import com.example.demo.exceptions.FieldNotFoundException;
import com.example.demo.exceptions.StudyPlanNotFoundException;
import com.example.demo.dto.repository.StudyPlanCustomRepository;
import com.example.demo.mapper.StudyPlanMapper;

import lombok.extern.slf4j.Slf4j;

import com.example.demo.dto.repository.FieldOfStudyRepository;
import com.example.demo.dto.repository.StudyPlanRepository;

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
        
        if (studyPlan.getYearStart() != null) {
            if (studyPlan.getYearStart() < 2015) {
                errors.add("Год начала действия учебного плана не может быть раньше 2015");
            }
            if (studyPlan.getYearStart() > 2025) {
                errors.add("Год начала действия учебного плана не может быть позже 2025");
            }
        }
        
        if (studyPlan.getYearEnd() != null) {
            if (studyPlan.getYearEnd() < 2025) {
                errors.add("Год окончания действия учебного плана не может быть раньше 2025");
            }
            if (studyPlan.getYearEnd() > 2035) {
                errors.add("Год окончания действия учебного плана не может быть позже 2035");
            }
        }
    
        if (studyPlan.getYearStart() != null && studyPlan.getYearEnd() != null) {
            if (studyPlan.getYearEnd() <= studyPlan.getYearStart()) {
                errors.add("Год окончания должен быть больше года начала");
            }
        }
        
        if (studyPlan.getCourse() != null) {
            if (studyPlan.getCourse() < 1) {
                errors.add("Курс не может быть меньше 1");
            }
            else if (studyPlan.getCourse() > 10) {
                errors.add("Курс не может быть больше 10");
            }
        }
        
        if (studyPlan.getStudyPlanName() != null && studyPlan.getStudyPlanName().length() > 200) {
            errors.add("Название учебного плана не должно превышать 200 символов");
        }
        
        if (studyPlan.getFilePath() != null && studyPlan.getFilePath().length() > 300) {
            errors.add("Путь к файлам учебного плана не должен превышать 300 символов");
        }
        
        if (studyPlan.getStatus() != null && studyPlan.getStatus().length() > 20) {
            errors.add("Статус или версия учебного плана ограничены 20 символами");
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

        StudyPlan studyPlan = studyPlanRepositoryRepo.findById(studyPlanId)
            .orElseThrow(() -> new StudyPlanNotFoundException(
                "Учебный план с ID " + studyPlanId + " не найден"));

        studyPlan = studyPlanRepositoryRepo
        .findByStudyPlanIdAndField_FieldId(studyPlanId, fieldId)
        .orElseThrow(() -> new StudyPlanNotFoundException(
            String.format(
                "Учебный план с ID %d не найден в направлении ID %d",
                studyPlanId, fieldId
            )
        ));
    
        boolean newStatus = !Boolean.TRUE.equals(studyPlan.getArchiveStatus());
        studyPlan.setArchiveStatus(newStatus);
    
        String action = newStatus ? "архивирован" : "разархивирован";
        log.info("Учебный план {} {} (ID: {})", 
             studyPlan.getStudyPlanName(), action, studyPlanId);
    
        return studyPlanRepositoryRepo.save(studyPlan);
    }
}