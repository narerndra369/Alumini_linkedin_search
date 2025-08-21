package com.narendra.alumini_linked_search.dto;

import lombok.Data;

@Data
public class AlumniProfileResponse {
    private String name;
    private String currentRole;
    private String university;
    private String location;
    private String linkedinHeadline;
    private Integer passoutYear;

    public AlumniProfileResponse() {
    }

    public AlumniProfileResponse(String name, String currentRole, String university, String location, String linkedinHeadline, Integer passoutYear) {
        this.name = name;
        this.currentRole = currentRole;
        this.university = university;
        this.location = location;
        this.linkedinHeadline = linkedinHeadline;
        this.passoutYear = passoutYear;
    }

    // Constructor, Getters, Setters
}