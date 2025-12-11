package com.example.demo.dto.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.domain.FieldOfStudy;

@Repository
public interface FieldOfStudyRepository extends JpaRepository<FieldOfStudy, Long> {
   
    // Метод для поиска по названию направления (регистронезависимый поиск)
    List<FieldOfStudy> findByFieldNameContainingIgnoreCase(String fieldName);
}
