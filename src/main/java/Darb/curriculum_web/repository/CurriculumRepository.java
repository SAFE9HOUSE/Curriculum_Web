package Darb.curriculum_web.repository;

import Darb.curriculum_web.domain.Curriculum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurriculumRepository extends JpaRepository<Curriculum, Long> {

    // Найти все учебные планы по ID направления
    List<Curriculum> findByField_FieldId(Long fieldId);
}