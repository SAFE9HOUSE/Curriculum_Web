package Darb.curriculum_web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Darb.curriculum_web.domain.FieldOfStudy;

import java.util.List;

@Repository
public interface FieldOfStudyRepository extends JpaRepository<FieldOfStudy, Long> {

    // Метод для поиска по названию направления (регистронезависимый поиск)
    List<FieldOfStudy> findByFieldNameContainingIgnoreCase(String fieldName);
}
