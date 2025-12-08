package Darb.curriculum_web.controller;

import Darb.curriculum_web.dto.CurriculumDisciplinesResponseDto;
import Darb.curriculum_web.service.CurriculumService;
import Darb.curriculum_web.dto.SuccessResponse;
import Darb.curriculum_web.dto.ResponseMetadata;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/curricula")
public class CurriculumController {

    private final CurriculumService curriculumService;

    public CurriculumController(CurriculumService curriculumService) {
        this.curriculumService = curriculumService;
    }

    @GetMapping("/{curriculumId}/disciplines")
    public ResponseEntity<SuccessResponse<CurriculumDisciplinesResponseDto>> getCurriculumDisciplines(
            @PathVariable Long curriculumId) {

        // 1. Валидация ID (используем существующую логику из FieldOfStudyController)
        if (curriculumId == null || curriculumId <= 0) {
            throw new IllegalArgumentException("curriculumId должен быть положительным целым числом");
        }

        // 2. Получаем данные через сервис
        CurriculumDisciplinesResponseDto data = curriculumService.getDisciplinesByCurriculumId(curriculumId);

        // 3. Формируем метаданные (как в существующих эндпоинтах)
        ResponseMetadata metadata = new ResponseMetadata();
        metadata.setResponseTime(LocalDateTime.now());
        metadata.setObjectsCount(data.getDisciplines().size()); // Количество дисциплин

        // 4. Формируем ответ в общей обёртке
        SuccessResponse<CurriculumDisciplinesResponseDto> response = new SuccessResponse<>();
        response.setData(data);
        response.setMetadata(metadata);

        return ResponseEntity.ok(response);
    }
}
