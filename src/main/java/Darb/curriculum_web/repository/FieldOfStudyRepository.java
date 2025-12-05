package Darb.curriculum_web.repository;

import Darb.curriculum_web.entity.FieldOfStudy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FieldOfStudyRepository extends JpaRepository<FieldOfStudy, Long> {

    // Метод для поиска по названию направления (регистронезависимый поиск)
    List<FieldOfStudy> findByFieldNameContainingIgnoreCase(String fieldName);
}
