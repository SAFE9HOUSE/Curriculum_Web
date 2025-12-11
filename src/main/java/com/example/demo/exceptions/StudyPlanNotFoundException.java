package com.example.demo.exceptions;

public class StudyPlanNotFoundException extends RuntimeException {
    public StudyPlanNotFoundException(String message) {
        super(message);
    }
}
