package Darb.curriculum_web.service;

import Darb.curriculum_web.domain.Curriculum;
import Darb.curriculum_web.domain.FieldOfStudy;
import Darb.curriculum_web.dto.CurriculumDto;
import Darb.curriculum_web.repository.CurriculumRepository;
import Darb.curriculum_web.repository.FieldOfStudyRepository;
import Darb.curriculum_web.exceptions.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;

@Service 
public class FieldOfStudyService {

    @Autowired
    private FieldOfStudyRepository fieldRepo;

    @Autowired
    private CurriculumRepository curriculumRepo;

    // Метод: список направлений (с поиском)
    public List<FieldOfStudy> getAllFields(String search) {
        if (search != null && !search.trim().isEmpty()) {
            return fieldRepo.findByFieldNameContainingIgnoreCase(search.trim());
        }
        return fieldRepo.findAll();
    }

    // Метод: направление + его учебные планы
    public Map<String, Object> getFieldWithCurricula(Long id) {
    if (id == null) throw new IllegalArgumentException("ID не может быть пустым");

    FieldOfStudy field = fieldRepo.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Направление не найдено с ID: " + id));

    List<Curriculum> curricula = curriculumRepo.findByField_FieldId(id);

    // Возвращаем данные даже если планы отсутствуют
    List<CurriculumDto> plansDto = curricula.stream()
    .map(CurriculumDto::new) 
    .collect(Collectors.toList());

    Map<String, Object> response = new LinkedHashMap<>();
    response.put("direction_info", field);
    response.put("uchebniye_plany", plansDto);
    return response;
    }
}