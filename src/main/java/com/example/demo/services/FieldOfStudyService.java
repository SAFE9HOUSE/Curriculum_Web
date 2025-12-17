package com.example.demo.services;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        
        if (field.getFieldId() != null) {
            throw new IllegalArgumentException("ID задается системой, а не пользователем");
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
    
    if (!fieldRepo.existsById(fieldId)) {
        throw new FieldNotFoundException(
            "Направление с ID " + fieldId + " не найдено"
        );
    }

        fieldRepo.deleteById(fieldId);
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
