package com.narendra.alumini_linked_search.dto;

import lombok.Data;

@Data
public class AlumniSearchRequest {
    private String university;
    private String designation;
    private Integer passoutYear;

    // Getters, Setters
}