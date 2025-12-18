package com.example.demo.services;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.FieldOfStudy;
import com.example.demo.domain.StudyPlan;
import com.example.demo.dto.StudyPlanDto;
import com.example.demo.dto.repository.FieldOfStudyRepository;
import com.example.demo.dto.repository.StudyPlanRepository;
import com.example.demo.exceptions.FieldNotFoundException;
import com.example.demo.exceptions.DuplicateResourceException;
import com.example.demo.exceptions.ValidationException;

@Service
public class FieldOfStudyService {
    @Autowired
    private FieldOfStudyRepository fieldRepo;

    @Autowired
    private StudyPlanRepository curriculumRepo;

    public List<FieldOfStudy> getAllFields(String search) {
        if (search != null && !search.trim().isEmpty()) {
            return fieldRepo.findByFieldNameContainingIgnoreCase(search.trim());
        }
        return fieldRepo.findAll();
    }

    public Map<String, Object> getFieldWithCurricula(Long id) {
        if (id == null) throw new IllegalArgumentException("ID не может быть пустым");

        FieldOfStudy field = fieldRepo.findById(id)
        .orElseThrow(() -> new FieldNotFoundException("Направление не найдено с ID: " + id));

        List<StudyPlan> curricula = curriculumRepo.findByField_FieldId(id);

        List<StudyPlanDto> plansDto = curricula.stream()
        .map(StudyPlanDto::new) 
        .collect(Collectors.toList());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("direction_info", field);
        response.put("uchebniye_plany", plansDto);
        return response;
    }

    // создание направления  
    @Transactional
    public FieldOfStudy createField(FieldOfStudy field) {

        List<String> errors = new ArrayList<>();

         
        if (field.getFieldCode() == null || field.getFieldCode().trim().isEmpty()) {
            errors.add("Код направления обязателен");
        }
    
    
        if (field.getFieldName() == null || field.getFieldName().trim().isEmpty()) {
            errors.add("Название направления обязательно");
        }
    
   
        if (field.getDegreeLevel() == null || field.getDegreeLevel().trim().isEmpty()) {
            errors.add("Уровень образования обязателен");
        }
    
    
        if (field.getStudyLength() == null) {
            errors.add("Длительность обучения обязательна");
        }
        else if (field.getStudyLength() < 1 || field.getStudyLength() > 10) {
            errors.add("Длительность обучения должна быть от 1 до 10 лет");
        }
    
    
        if (field.getProfileName() == null || field.getProfileName().trim().isEmpty()) {
           errors.add("Наименование профиля обязательно");
        }
        
        if (field.getFieldId() != null) {
            errors.add("ID задается системой, а не пользователем");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        // Проверка уникальности названия профиля
        if (fieldRepo.existsByProfileName(field.getProfileName())) {
            throw new DuplicateResourceException(
                "Профиль с наименованием '" + field.getProfileName() + "' уже существует"
            );
        }
        
        return fieldRepo.save(field);
    }
    
    // удаление направления  
    @SuppressWarnings("null")
    @Transactional
    public void deleteField(Long fieldId) {
    
        FieldOfStudy field = fieldRepo.findById(fieldId)
        .orElseThrow(() -> new FieldNotFoundException(
            "Направление с ID " + fieldId + " не найдено"));
    
    
        List<StudyPlan> studyPlans = curriculumRepo.findByField_FieldId(fieldId);
    
        if (!studyPlans.isEmpty()) {
            String plansInfo = studyPlans.stream()
                .limit(5)
                .map(p -> p.getStudyPlanName() + " (ID:" + p.getStudyPlanId() + ")")
                .collect(Collectors.joining(", "));
        
            String errorMsg = "Невозможно удалить направление \"" + field.getFieldName() + 
                "\". К нему привязано " + studyPlans.size() + 
                " учебных планов: " + plansInfo;
        
            if (studyPlans.size() > 5) {
                errorMsg += " и еще " + (studyPlans.size() - 5) + " планов";
            }
        
            throw new ValidationException(Collections.singletonList(errorMsg));
        }

        fieldRepo.delete(field);  
    }

    // обновление направления  
    @SuppressWarnings("null")
    @Transactional
    public FieldOfStudy updateField(Long fieldId, FieldOfStudy updates) {
    
        if (updates.getFieldId() != null && !updates.getFieldId().equals(fieldId)) {
            throw new IllegalArgumentException(
            "Нельзя изменять ID направления");
        }       
        
        FieldOfStudy existingField = fieldRepo.findById(fieldId)
            .orElseThrow(() -> new FieldNotFoundException(
            "Направление с ID " + fieldId + " не найдено"
        ));
    
    
        if (updates.getProfileName() != null && 
        !existingField.getProfileName().equals(updates.getProfileName())) {
        
            if (fieldRepo.existsByProfileName(updates.getProfileName())) {
                throw new DuplicateResourceException(
                "Профиль с наименованием '" + updates.getProfileName() + "' уже существует"
                );
            }
            existingField.setProfileName(updates.getProfileName());
        }
    
    
    if (updates.getFieldCode() != null) {
        existingField.setFieldCode(updates.getFieldCode());
    }
    
    if (updates.getFieldName() != null) {
        existingField.setFieldName(updates.getFieldName());
    }
    
    if (updates.getDegreeLevel() != null) {
        existingField.setDegreeLevel(updates.getDegreeLevel());
    }
    
    if (updates.getStudyLength() != null) {
        existingField.setStudyLength(updates.getStudyLength());
    }
    
    if (updates.getQualification() != null) {
        existingField.setQualification(updates.getQualification());
    }
    
    return fieldRepo.save(existingField);
    
    }
}
