package com.narendra.alumini_linked_search.repository;


import com.narendra.alumini_linked_search.model.AlumniProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlumniProfileRepository extends JpaRepository<AlumniProfile, Long> { }