package com.example.demo.services;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.demo.dto.StudyPlansDisciplinesResponseDto;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;


@Service
public class PdfGenerateService {
    
    public byte[] generateStudyPlanPdf(StudyPlansDisciplinesResponseDto data) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(baos);
            
        PdfDocument pdfDocument = new PdfDocument(writer);
            com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdfDocument)) {
        
            PdfFont font;
        
            byte[] fontData = getClass().getClassLoader()
                .getResourceAsStream("fonts/arial.ttf").readAllBytes();
            
            com.itextpdf.io.font.FontProgram fontProgram = 
                com.itextpdf.io.font.FontProgramFactory.createFont(fontData);
            
            font = PdfFontFactory.createFont(fontProgram, 
                com.itextpdf.io.font.PdfEncodings.IDENTITY_H);
        
            document.setFont(font);

            Paragraph title = new Paragraph("УЧЕБНЫЙ ПЛАН")
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setFontSize(18);
            document.add(title);
            document.add(new Paragraph("\n"));

       
            document.add(new Paragraph("Название: " + 
                safeString(data.getStudyPlanName()))
                .setFontSize(12));
        
            document.add(new Paragraph("Курс: " + 
                safeString(data.getCourse()))
                .setFontSize(12));
        
            document.add(new Paragraph("Годы действия: " + 
                safeString(data.getYearStart()) + " - " + 
                safeString(data.getYearEnd()))
                .setFontSize(12));

            document.add(new Paragraph("Версия: " + 
                safeString(data.getStatus()))
                .setFontSize(12));

            document.add(new Paragraph(" "));

        
            if (data.getFieldOfStudy() != null) {
                document.add(new Paragraph("НАПРАВЛЕНИЕ ПОДГОТОВКИ")
                        .setBold()
                        .setFontSize(14));
            
                document.add(new Paragraph("Код направления: " + 
                    safeString(data.getFieldOfStudy().getFieldCode()))
                    .setFontSize(12));
            
                document.add(new Paragraph("Название: " + 
                    safeString(data.getFieldOfStudy().getFieldName()))
                    .setFontSize(12));
            
                document.add(new Paragraph("Уровень образования: " + 
                    safeString(data.getFieldOfStudy().getDegreeLevel()))
                    .setFontSize(12));
                
                document.add(new Paragraph("Длительность обучения: " + 
                    safeString(data.getFieldOfStudy().getStudyLength()))
                    .setFontSize(12));
            }
        
            document.add(new Paragraph(" "));

       
            document.add(new Paragraph("ДИСЦИПЛИНЫ УЧЕБНОГО ПЛАНА")
                .setBold()
                .setFontSize(14));

            if (data.getDisciplines() != null && !data.getDisciplines().isEmpty()) {
                
                int counter = 1;
                for (var discipline : data.getDisciplines()) {

                    StringBuilder info = new StringBuilder();
                    info.append(counter).append(". ");
                    info.append(safeString(discipline.getDisciplineName()));
                    info.append(" | ");
                    info.append("Семестр: ").append(safeString(discipline.getTerm()));
                    info.append(" | ");
                    info.append("Часы: ").append(safeString(discipline.getTotalHours()));
                    info.append(" | ");
                    info.append("Контроль: ").append(safeString(discipline.getReport()));
                
                        if (discipline.getTeachers() != null && !discipline.getTeachers().isEmpty()) {
                            info.append(" | Преподаватели: ");
                            for (int i = 0; i < discipline.getTeachers().size(); i++) {
                                var teacher = discipline.getTeachers().get(i);
                                info.append(safeString(teacher.getFio()));
                                if (i < discipline.getTeachers().size() - 1) {
                                    info.append(", ");
                                }
                            }
                        }
                
                    document.add(new Paragraph(info.toString())
                        .setFontSize(11)
                        .setMarginBottom(5));
                
                    counter++;
                }
            
                document.add(new Paragraph("\nВсего дисциплин: " + data.getDisciplines().size())
                    .setFontSize(11)
                    .setItalic());
            } 
            else {
                document.add(new Paragraph("Список дисциплин пуст")
                    .setFontSize(11)
                    .setItalic());
            }
        
            document.add(new Paragraph("\n\n").setFontSize(1));
        
            String time = LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        
            Paragraph footer = new Paragraph("Документ сгенерирован: " + time)
                .setFontSize(9)
                .setItalic()
                .setTextAlignment(TextAlignment.RIGHT);
            document.add(footer);

        } 
        catch (Exception e) {
            System.err.println("Ошибка при генерации PDF: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    
        return baos.toByteArray();
    }

    private String safeString(Object value) {
        return value != null ? value.toString() : "не указано";
    }
}
 


