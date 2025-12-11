package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@Data
@JsonPropertyOrder({"status", "data", "metadata"}) 
public class SuccessResponse<T> {
   
    private String status = "success";
    private T data;
    private ResponseMetadata metadata;
}
