package Darb.curriculum_web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({"status", "error", "Content", "metadata"})
public class ErrorResponse {
    private String status = "error";
    
    private String error;
    
    @JsonProperty("Content") 
    private String content;
    
    private ResponseMetadata metadata;
}