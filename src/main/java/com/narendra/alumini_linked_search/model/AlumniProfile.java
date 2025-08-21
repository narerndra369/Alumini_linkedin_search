package com.narendra.alumini_linked_search.model;


import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "alumni_profiles")
@Data
public class AlumniProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String currentRole;
    private String university;
    private String location;
    private String linkedinHeadline;
    private Integer passoutYear;

    // Constructors, Getters, Setters
}
