package Darb.curriculum_web.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({"status", "data", "metadata"}) // Гарантирует порядок полей
public class SuccessResponse<T> {
    private String status = "success";
    private T data;
    private ResponseMetadata metadata;
}