package com.example.demo.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.Interfaces.IStudyPlanServise;
import com.example.demo.dto.StudyPlansDisciplinesResponseDto;
import com.example.demo.exceptions.StudyPlanNotFoundException;
import com.example.demo.dto.repository.StudyPlanCustomRepository;
import com.example.demo.mapper.StudyPlanMapper;

import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class StudyPlansService implements IStudyPlanServise {

    private final StudyPlanCustomRepository _repository;
    private final StudyPlanMapper _mapper;
    
    public StudyPlansService(StudyPlanCustomRepository repository, StudyPlanMapper mapper) {
        _repository = repository;
        _mapper = mapper;
    }
     
    @Override
    public StudyPlansDisciplinesResponseDto getDisciplinesByStudyPlanId(Long studyPlanId) {
        
        List<Map<String, Object>> rows = _repository.findDisciplinesByStudyPlanId(studyPlanId);
        
        if (rows.isEmpty()) {
            throw new StudyPlanNotFoundException("Учебный план с ID " + studyPlanId + " не найден");
        }
        
        return _mapper.mapToStudyPlanDisciplinesResponse(rows);
    }
}