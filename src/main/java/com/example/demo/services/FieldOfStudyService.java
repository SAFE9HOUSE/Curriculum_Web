package com.example.demo.services;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.domain.FieldOfStudy;
import com.example.demo.domain.StudyPlan;
import com.example.demo.dto.StudyPlanDto;
import com.example.demo.dto.repository.FieldOfStudyRepository;
import com.example.demo.dto.repository.StudyPlanRepository;
import com.example.demo.exceptions.FieldNotFoundException;

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
}
