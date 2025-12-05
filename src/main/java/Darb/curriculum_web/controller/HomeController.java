package Darb.curriculum_web.controller;

import Darb.curriculum_web.entity.FieldOfStudy;
import Darb.curriculum_web.repository.FieldOfStudyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private FieldOfStudyRepository fieldOfStudyRepository;

    @GetMapping("/")
    public String home(@RequestParam(required = false) String query, Model model) {
        List<FieldOfStudy> fields;

        if (query != null && !query.trim().isEmpty()) {
            // Поиск по части названия (регистронезависимо)
            fields = fieldOfStudyRepository.findByFieldNameContainingIgnoreCase(query.trim());
        } else {
            // Показать все
            fields = fieldOfStudyRepository.findAll();
        }

        model.addAttribute("fields", fields);
        model.addAttribute("query", query); // чтобы сохранить текст в строке поиска
        return "index"; // имя шаблона: src/main/resources/templates/index.html
    }
}
    

