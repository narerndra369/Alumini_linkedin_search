package com.narendra.alumini_linked_search.controller;



import com.narendra.alumini_linked_search.dto.AlumniProfileResponse;
import com.narendra.alumini_linked_search.dto.AlumniSearchRequest;
import com.narendra.alumini_linked_search.service.AlumniProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/alumni")
public class AlumniProfileController {

    private final AlumniProfileService service;

    public AlumniProfileController(AlumniProfileService service) {
        this.service = service;
    }

    @PostMapping("/search")
    public ResponseEntity<?> searchAlumni(@RequestBody AlumniSearchRequest request) {
        List<AlumniProfileResponse> profiles = service.searchAndSaveAlumniProfiles(request);
        HashMap<String, Object> resp = new HashMap<>();
        resp.put("status", "success");
        resp.put("data", profiles);
        System.out.println(resp.get("data"));
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllAlumniProfiles() {
        HashMap<String, Object> resp = new HashMap<>();
        resp.put("status", "success");
        resp.put("data", service.getAllAlumniProfiles());
        return ResponseEntity.ok(resp);
    }
}

